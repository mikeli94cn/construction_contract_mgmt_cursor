import type { Project, ProjectMember } from '../types'
import { http } from './http'

export function listProjects(): Promise<Project[]> {
  return http.get<Project[]>('/projects').then((r) => r.data)
}

export function createProject(body: Partial<Project>): Promise<Project> {
  return http.post<Project>('/projects', body).then((r) => r.data)
}

export function getProject(id: string): Promise<Project> {
  return http.get<Project>(`/projects/${id}`).then((r) => r.data)
}

export function updateProject(id: string, body: Partial<Project>): Promise<Project> {
  return http.patch<Project>(`/projects/${id}`, body).then((r) => r.data)
}

export function setProjectMembers(id: string, members: ProjectMember[]): Promise<void> {
  return http.put(`/projects/${id}/members`, { members }).then(() => undefined)
}