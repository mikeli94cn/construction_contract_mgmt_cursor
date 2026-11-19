import type { ChangeOrder } from '../types'
import { http } from './http'

export function listChangeOrders(contractId: string): Promise<ChangeOrder[]> {
  return http.get<ChangeOrder[]>(`/contracts/${contractId}/change-orders`).then((r) => r.data)
}

export function createChangeOrder(contractId: string, body: Partial<ChangeOrder>): Promise<ChangeOrder> {
  return http.post<ChangeOrder>(`/contracts/${contractId}/change-orders`, body).then((r) => r.data)
}

export function getChangeOrder(id: string): Promise<ChangeOrder> {
  return http.get<ChangeOrder>(`/change-orders/${id}`).then((r) => r.data)
}

export function updateChangeOrder(id: string, body: Partial<ChangeOrder>): Promise<ChangeOrder> {
  return http.patch<ChangeOrder>(`/change-orders/${id}`, body).then((r) => r.data)
}

export function submitChangeOrder(id: string): Promise<ChangeOrder> {
  return http.post<ChangeOrder>(`/change-orders/${id}/submit`).then((r) => r.data)
}