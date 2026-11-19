package com.company.ccm.project.api;

import com.company.ccm.project.api.dto.ProjectDtos.CreateProjectRequest;
import com.company.ccm.project.api.dto.ProjectDtos.ProjectResponse;
import com.company.ccm.project.api.dto.ProjectDtos.ReplaceMembersRequest;
import com.company.ccm.project.api.dto.ProjectDtos.UpdateProjectRequest;
import com.company.ccm.project.application.ProjectService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectResponse> list() {
        return projectService.list();
    }

    @PostMapping
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.create(request);
    }

    @GetMapping("/{id}")
    public ProjectResponse get(@PathVariable String id) {
        return projectService.get(id);
    }

    @PatchMapping("/{id}")
    public ProjectResponse update(@PathVariable String id, @Valid @RequestBody UpdateProjectRequest request) {
        return projectService.update(id, request);
    }

    @PutMapping("/{id}/members")
    public ProjectResponse replaceMembers(
            @PathVariable String id, @Valid @RequestBody ReplaceMembersRequest request) {
        return projectService.replaceMembers(id, request);
    }
}
