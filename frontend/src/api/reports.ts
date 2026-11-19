import type { AuditEvent, ContractSummaryRow } from '../types'
import { http } from './http'

export function getContractSummary(): Promise<ContractSummaryRow[]> {
  return http.get<ContractSummaryRow[]>('/reports/contract-summary').then((r) => r.data)
}

export function getAuditEvents(projectId?: string): Promise<AuditEvent[]> {
  return http
    .get<AuditEvent[]>('/audit-events', { params: projectId ? { projectId } : undefined })
    .then((r) => r.data)
}
