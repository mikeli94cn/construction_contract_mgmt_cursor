import type { Settlement } from '../types'
import { http } from './http'
import type { AxiosError } from 'axios'

export async function getSettlement(contractId: string): Promise<Settlement | null> {
  try {
    const { data } = await http.get<Settlement>(`/contracts/${contractId}/settlements`)
    return data
  } catch (e) {
    const err = e as AxiosError
    if (err.response?.status === 404) {
      return null
    }
    throw e
  }
}

export function createSettlement(
  contractId: string,
  body: { settlementNo: string; finalAmount: number; currencyCode?: string },
): Promise<Settlement> {
  return http.post<Settlement>(`/contracts/${contractId}/settlements`, body).then((r) => r.data)
}

export function submitSettlement(id: string): Promise<Settlement> {
  return http.post<Settlement>(`/settlements/${id}/submit`).then((r) => r.data)
}

export function sealSettlement(id: string): Promise<Settlement> {
  return http.post<Settlement>(`/settlements/${id}/seal`).then((r) => r.data)
}
