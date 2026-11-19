import type { Partner } from '../types'
import { http } from './http'

export function listPartners(): Promise<Partner[]> {
  return http.get<Partner[]>('/partners').then((r) => r.data)
}

export function createPartner(body: Partial<Partner>): Promise<Partner> {
  return http.post<Partner>('/partners', body).then((r) => r.data)
}

export function getPartner(id: string): Promise<Partner> {
  return http.get<Partner>(`/partners/${id}`).then((r) => r.data)
}

export function updatePartner(id: string, body: Partial<Partner>): Promise<Partner> {
  return http.patch<Partner>(`/partners/${id}`, body).then((r) => r.data)
}