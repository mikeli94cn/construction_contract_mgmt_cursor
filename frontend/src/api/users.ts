import type { CreateUserRequest, UpdateUserRequest, User } from '../types'
import { http } from './http'

export function fetchMe(): Promise<User> {
  return http.get<User>('/me').then((r) => r.data)
}

export function listUsers(): Promise<User[]> {
  return http.get<User[]>('/users').then((r) => r.data)
}

export function getUser(id: string): Promise<User> {
  return http.get<User>(`/users/${id}`).then((r) => r.data)
}

export function createUser(body: CreateUserRequest): Promise<User> {
  return http.post<User>('/users', body).then((r) => r.data)
}

export function updateUser(id: string, body: UpdateUserRequest): Promise<User> {
  return http.patch<User>(`/users/${id}`, body).then((r) => r.data)
}

export function assignRoles(id: string, roleCodes: string[]): Promise<User> {
  return http.post<User>(`/users/${id}/roles`, { roleCodes }).then((r) => r.data)
}