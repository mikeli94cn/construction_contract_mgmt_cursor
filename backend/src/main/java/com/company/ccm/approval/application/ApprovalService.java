package com.company.ccm.approval.application;

import com.company.ccm.approval.api.dto.ApprovalDtos.ApprovalActionRequest;
import com.company.ccm.approval.api.dto.ApprovalDtos.ApprovalDefinitionRequest;
import com.company.ccm.approval.api.dto.ApprovalDtos.ApprovalDefinitionResponse;
import com.company.ccm.approval.api.dto.ApprovalDtos.ApprovalStepRequest;
import com.company.ccm.approval.api.dto.ApprovalDtos.ApprovalStepResponse;
import com.company.ccm.approval.api.dto.ApprovalDtos.ApprovalTaskResponse;
import com.company.ccm.approval.domain.ApprovalActionEntity;
import com.company.ccm.approval.domain.ApprovalCallback;
import com.company.ccm.approval.domain.ApprovalDefinitionEntity;
import com.company.ccm.approval.domain.ApprovalInstanceEntity;
import com.company.ccm.approval.domain.ApprovalStepEntity;
import com.company.ccm.approval.domain.ApprovalTargetType;
import com.company.ccm.approval.domain.ApprovalTaskEntity;
import com.company.ccm.approval.infrastructure.ApprovalActionRepository;
import com.company.ccm.approval.infrastructure.ApprovalDefinitionRepository;
import com.company.ccm.approval.infrastructure.ApprovalInstanceRepository;
import com.company.ccm.approval.infrastructure.ApprovalStepRepository;
import com.company.ccm.approval.infrastructure.ApprovalTaskRepository;
import com.company.ccm.audit.application.AuditService;
import com.company.ccm.organization.domain.RoleCodes;
import com.company.ccm.organization.domain.UserEntity;
import com.company.ccm.organization.infrastructure.UserRepository;
import com.company.ccm.project.infrastructure.ProjectMemberRepository;
import com.company.ccm.shared.domain.BusinessException;
import com.company.ccm.shared.domain.NotFoundException;
import com.company.ccm.shared.security.SecurityUtils;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApprovalService {

    private final ApprovalDefinitionRepository definitionRepository;
    private final ApprovalStepRepository stepRepository;
    private final ApprovalInstanceRepository instanceRepository;
    private final ApprovalTaskRepository taskRepository;
    private final ApprovalActionRepository actionRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final AuditService auditService;
    private final Map<ApprovalTargetType, ApprovalCallback> callbacks;

    public ApprovalService(
            ApprovalDefinitionRepository definitionRepository,
            ApprovalStepRepository stepRepository,
            ApprovalInstanceRepository instanceRepository,
            ApprovalTaskRepository taskRepository,
            ApprovalActionRepository actionRepository,
            UserRepository userRepository,
            ProjectMemberRepository projectMemberRepository,
            AuditService auditService,
            List<ApprovalCallback> callbackList) {
        this.definitionRepository = definitionRepository;
        this.stepRepository = stepRepository;
        this.instanceRepository = instanceRepository;
        this.taskRepository = taskRepository;
        this.actionRepository = actionRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.auditService = auditService;
        this.callbacks = callbackList.stream()
                .collect(Collectors.toMap(ApprovalCallback::supports, Function.identity()));
    }

    @Transactional(readOnly = true)
    public List<ApprovalDefinitionResponse> listDefinitions() {
        return definitionRepository.findAll().stream().map(this::toDefinitionResponse).toList();
    }

    @Transactional
    public ApprovalDefinitionResponse createDefinition(ApprovalDefinitionRequest request) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN);
        if (definitionRepository.existsByCode(request.code())) {
            throw new BusinessException("Approval definition code already exists");
        }
        ApprovalDefinitionEntity def = new ApprovalDefinitionEntity();
        def.setCode(request.code());
        def.setName(request.name());
        def.setTargetType(request.targetType());
        def.setActive(request.active() == null || request.active());
        ApprovalDefinitionEntity saved = definitionRepository.save(def);
        saveSteps(saved.getId(), request.steps());
        return toDefinitionResponse(saved);
    }

    @Transactional
    public ApprovalDefinitionResponse updateDefinition(String id, ApprovalDefinitionRequest request) {
        SecurityUtils.requireAnyRole(RoleCodes.SYSTEM_ADMIN);
        ApprovalDefinitionEntity def =
                definitionRepository.findById(id).orElseThrow(() -> new NotFoundException("Definition not found"));
        def.setName(request.name());
        def.setTargetType(request.targetType());
        if (request.active() != null) {
            def.setActive(request.active());
        }
        stepRepository.deleteByDefinitionId(id);
        saveSteps(id, request.steps());
        return toDefinitionResponse(definitionRepository.save(def));
    }

    @Transactional
    public String startApproval(ApprovalTargetType targetType, String targetId, String projectId) {
        if (instanceRepository.existsByTargetTypeAndTargetIdAndStatus(targetType, targetId, "IN_PROGRESS")) {
            throw new BusinessException("An approval is already in progress for this target");
        }
        ApprovalDefinitionEntity def = definitionRepository
                .findFirstByTargetTypeAndActiveTrueOrderByCreatedAtDesc(targetType)
                .orElseThrow(() -> new BusinessException("No active approval definition for " + targetType));
        List<ApprovalStepEntity> steps = stepRepository.findByDefinitionIdOrderByStepOrderAsc(def.getId());
        if (steps.isEmpty()) {
            throw new BusinessException("Approval definition has no steps");
        }
        ApprovalInstanceEntity instance = new ApprovalInstanceEntity();
        instance.setDefinitionId(def.getId());
        instance.setTargetType(targetType);
        instance.setTargetId(targetId);
        instance.setProjectId(projectId);
        instance.setStatus("IN_PROGRESS");
        instance.setCurrentStepOrder(steps.getFirst().getStepOrder());
        instance.setStartedBy(SecurityUtils.currentUserId());
        ApprovalInstanceEntity saved = instanceRepository.save(instance);
        createTasksForStep(saved, steps.getFirst(), projectId);
        auditService.record(
                "APPROVAL_STARTED",
                targetType.name(),
                targetId,
                projectId,
                Map.of("instanceId", saved.getId(), "definitionCode", def.getCode()));
        return saved.getId();
    }

    @Transactional(readOnly = true)
    public List<ApprovalTaskResponse> myTasks() {
        return taskRepository.findByAssigneeUserIdAndStatus(SecurityUtils.currentUserId(), "PENDING").stream()
                .map(this::toTaskResponse)
                .toList();
    }

    @Transactional
    public void approve(String taskId, ApprovalActionRequest request) {
        actOnTask(taskId, true, request == null ? null : request.comment());
    }

    @Transactional
    public void reject(String taskId, ApprovalActionRequest request) {
        actOnTask(taskId, false, request == null ? null : request.comment());
    }

    private void actOnTask(String taskId, boolean approve, String comment) {
        ApprovalTaskEntity task =
                taskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Task not found"));
        if (!task.getAssigneeUserId().equals(SecurityUtils.currentUserId())
                && !SecurityUtils.hasRole(RoleCodes.SYSTEM_ADMIN)) {
            throw new BusinessException("Not assignee of this task");
        }
        if (!"PENDING".equals(task.getStatus())) {
            throw new BusinessException("Task is not pending");
        }
        ApprovalInstanceEntity instance = instanceRepository
                .findById(task.getInstanceId())
                .orElseThrow(() -> new NotFoundException("Instance not found"));
        if (!"IN_PROGRESS".equals(instance.getStatus())) {
            throw new BusinessException("Approval instance is not in progress");
        }

        task.setStatus(approve ? "APPROVED" : "REJECTED");
        task.setComment(comment);
        task.setActedAt(Instant.now());
        taskRepository.save(task);

        ApprovalActionEntity action = new ApprovalActionEntity();
        action.setInstanceId(instance.getId());
        action.setTaskId(task.getId());
        action.setActorUserId(SecurityUtils.currentUserId());
        action.setAction(approve ? "APPROVE" : "REJECT");
        action.setComment(comment);
        actionRepository.save(action);

        if (!approve) {
            instance.setStatus("REJECTED");
            instance.setCompletedAt(Instant.now());
            instanceRepository.save(instance);
            callback(instance.getTargetType()).onRejected(instance.getTargetId());
            auditService.record(
                    "APPROVAL_REJECTED",
                    instance.getTargetType().name(),
                    instance.getTargetId(),
                    instance.getProjectId(),
                    Map.of("instanceId", instance.getId()));
            return;
        }

        ApprovalStepEntity currentStep = stepRepository
                .findById(task.getStepId())
                .orElseThrow(() -> new NotFoundException("Step not found"));
        long approvedCount = taskRepository.findByInstanceIdAndStepId(instance.getId(), currentStep.getId()).stream()
                .filter(t -> "APPROVED".equals(t.getStatus()))
                .count();
        if (approvedCount < currentStep.getMinApprovals()) {
            return;
        }

        List<ApprovalStepEntity> steps =
                stepRepository.findByDefinitionIdOrderByStepOrderAsc(instance.getDefinitionId());
        ApprovalStepEntity next = steps.stream()
                .filter(s -> s.getStepOrder() > currentStep.getStepOrder())
                .findFirst()
                .orElse(null);
        if (next == null) {
            instance.setStatus("APPROVED");
            instance.setCompletedAt(Instant.now());
            instanceRepository.save(instance);
            callback(instance.getTargetType()).onApproved(instance.getTargetId());
            auditService.record(
                    "APPROVAL_APPROVED",
                    instance.getTargetType().name(),
                    instance.getTargetId(),
                    instance.getProjectId(),
                    Map.of("instanceId", instance.getId()));
        } else {
            instance.setCurrentStepOrder(next.getStepOrder());
            instanceRepository.save(instance);
            createTasksForStep(instance, next, instance.getProjectId());
        }
    }

    private void createTasksForStep(ApprovalInstanceEntity instance, ApprovalStepEntity step, String projectId) {
        Set<String> userIds = resolveAssignees(step, projectId);
        if (userIds.isEmpty()) {
            throw new BusinessException("No assignees resolved for approval step: " + step.getName());
        }
        for (String userId : userIds) {
            ApprovalTaskEntity task = new ApprovalTaskEntity();
            task.setInstanceId(instance.getId());
            task.setStepId(step.getId());
            task.setAssigneeUserId(userId);
            task.setStatus("PENDING");
            taskRepository.save(task);
        }
    }

    private Set<String> resolveAssignees(ApprovalStepEntity step, String projectId) {
        Set<String> userIds = new HashSet<>();
        switch (step.getAssigneeType()) {
            case "USER" -> userIds.add(step.getAssigneeValue());
            case "ROLE" -> userRepository.findActiveByRoleCode(step.getAssigneeValue()).stream()
                    .map(UserEntity::getId)
                    .forEach(userIds::add);
            case "PROJECT_ROLE" -> {
                if (projectId == null) {
                    throw new BusinessException("Project id required for PROJECT_ROLE assignee");
                }
                projectMemberRepository.findByProjectIdAndProjectRole(projectId, step.getAssigneeValue()).stream()
                        .map(m -> m.getUserId())
                        .forEach(userIds::add);
            }
            default -> throw new BusinessException("Unknown assignee type: " + step.getAssigneeType());
        }
        return userIds;
    }

    private ApprovalCallback callback(ApprovalTargetType type) {
        ApprovalCallback cb = callbacks.get(type);
        if (cb == null) {
            throw new BusinessException("No approval callback for " + type);
        }
        return cb;
    }

    private void saveSteps(String definitionId, List<ApprovalStepRequest> steps) {
        if (steps == null || steps.isEmpty()) {
            throw new BusinessException("At least one approval step is required");
        }
        for (ApprovalStepRequest req : steps) {
            ApprovalStepEntity step = new ApprovalStepEntity();
            step.setDefinitionId(definitionId);
            step.setStepOrder(req.stepOrder());
            step.setName(req.name());
            step.setAssigneeType(req.assigneeType());
            step.setAssigneeValue(req.assigneeValue());
            step.setMinApprovals(req.minApprovals() == null ? 1 : req.minApprovals());
            stepRepository.save(step);
        }
    }

    private ApprovalDefinitionResponse toDefinitionResponse(ApprovalDefinitionEntity def) {
        List<ApprovalStepResponse> steps = stepRepository.findByDefinitionIdOrderByStepOrderAsc(def.getId()).stream()
                .map(s -> new ApprovalStepResponse(
                        s.getId(),
                        s.getStepOrder(),
                        s.getName(),
                        s.getAssigneeType(),
                        s.getAssigneeValue(),
                        s.getMinApprovals()))
                .toList();
        return new ApprovalDefinitionResponse(
                def.getId(), def.getCode(), def.getName(), def.getTargetType(), def.isActive(), def.getVersion(), steps);
    }

    private ApprovalTaskResponse toTaskResponse(ApprovalTaskEntity task) {
        ApprovalInstanceEntity instance = instanceRepository
                .findById(task.getInstanceId())
                .orElseThrow(() -> new NotFoundException("Instance not found"));
        return new ApprovalTaskResponse(
                task.getId(),
                task.getInstanceId(),
                instance.getTargetType(),
                instance.getTargetId(),
                instance.getProjectId(),
                task.getStatus(),
                task.getCreatedAt());
    }
}
