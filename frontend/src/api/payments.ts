import type { Payment } from '../types'
import { http } from './http'

export function listPayments(contractId: string): Promise<Payment[]> {
  return http.get<Payment[]>(`/contracts/${contractId}/payments`).then((r) => r.data)
}

export function createPayment(contractId: string, body: Partial<Payment>): Promise<Payment> {
  return http.post<Payment>(`/contracts/${contractId}/payments`, body).then((r) => r.data)
}

export function getPayment(id: string): Promise<Payment> {
  return http.get<Payment>(`/payments/${id}`).then((r) => r.data)
}

export function updatePayment(id: string, body: Partial<Payment>): Promise<Payment> {
  return http.patch<Payment>(`/payments/${id}`, body).then((r) => r.data)
}

export function submitPayment(id: string): Promise<Payment> {
  return http.post<Payment>(`/payments/${id}/submit`).then((r) => r.data)
}