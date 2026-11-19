import type { ApprovalDefinition, ApprovalTask } from '../types'
import { http } from './http'

export function listApprovalDefinitions(): Promise<ApprovalDefinition[]> {
  return http.get<ApprovalDefinition[]>('/approval-definitions').then((r) => r.data)
}

export function createApprovalDefinition(body: Partial<ApprovalDefinition>): Promise<ApprovalDefinition> {
  return http.post<ApprovalDefinition>('/approval-definitions', body).then((r) => r.data)
}

export function getApprovalDefinition(id: string): Promise<ApprovalDefinition> {
  return http.get<ApprovalDefinition>(`/approval-definitions/${id}`).then((r) => r.data)
}

export function updateApprovalDefinition(
  id: string,
  body: Partial<ApprovalDefinition>,
): Promise<ApprovalDefinition> {
  return http.put<ApprovalDefinition>(`/approval-definitions/${id}`, body).then((r) => r.data)
}

export function listMyApprovalTasks(): Promise<ApprovalTask[]> {
  return http.get<ApprovalTask[]>('/approval-tasks/mine').then((r) => r.data)
}

export function approveTask(id: string, comment?: string): Promise<ApprovalTask> {
  return http.post<ApprovalTask>(`/approval-tasks/${id}/approve`, { comment }).then((r) => r.data)
}

export function rejectTask(id: string, comment?: string): Promise<ApprovalTask> {
  return http.post<ApprovalTask>(`/approval-tasks/${id}/reject`, { comment }).then((r) => r.data)
}