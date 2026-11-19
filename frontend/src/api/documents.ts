import type { DocumentMeta } from '../types'
import { http } from './http'

export function uploadDocument(file: File, ownerType: string, ownerId: string): Promise<DocumentMeta> {
  const form = new FormData()
  form.append('file', file)
  form.append('ownerType', ownerType)
  form.append('ownerId', ownerId)
  return http
    .post<DocumentMeta>('/documents', form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    })
    .then((r) => r.data)
}

export function listDocuments(ownerType: string, ownerId: string): Promise<DocumentMeta[]> {
  return http
    .get<DocumentMeta[]>('/documents', { params: { ownerType, ownerId } })
    .then((r) => r.data)
}

export function downloadDocument(id: string): Promise<Blob> {
  return http.get<Blob>(`/documents/${id}/download`, { responseType: 'blob' }).then((r) => r.data)
}