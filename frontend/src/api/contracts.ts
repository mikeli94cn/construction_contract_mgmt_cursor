import type { Contract } from '../types'
import { http } from './http'

export function listContracts(): Promise<Contract[]> {
  return http.get<Contract[]>('/contracts').then((r) => r.data)
}

export function listContractsByProject(projectId: string): Promise<Contract[]> {
  return http.get<Contract[]>(`/projects/${projectId}/contracts`).then((r) => r.data)
}

export function createContract(
  projectId: string,
  body: {
    partnerId: string
    contractNo: string
    title: string
    pricingType: string
    originalAmount: number
    type?: string
    currencyCode?: string
  },
): Promise<Contract> {
  return http.post<Contract>(`/projects/${projectId}/contracts`, body).then((r) => r.data)
}

export function getContract(id: string): Promise<Contract> {
  return http.get<Contract>(`/contracts/${id}`).then((r) => r.data)
}

export function updateContract(id: string, body: Partial<Contract>): Promise<Contract> {
  return http.patch<Contract>(`/contracts/${id}`, body).then((r) => r.data)
}

export function submitContract(id: string): Promise<Contract> {
  return http.post<Contract>(`/contracts/${id}/submit`).then((r) => r.data)
}

export function activateContract(id: string): Promise<Contract> {
  return http.post<Contract>(`/contracts/${id}/activate`).then((r) => r.data)
}
