export interface TokenResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresInSeconds: number
}

export interface User {
  id: string
  username: string
  displayName: string
  email: string
  phone: string
  status: string
  roles: string[]
  version: number
}

export interface CreateUserRequest {
  username: string
  password: string
  displayName: string
  email?: string
  phone?: string
  roleCodes: string[]
}

export interface UpdateUserRequest {
  displayName?: string
  email?: string
  phone?: string
  status?: string
  password?: string
}

export interface Project {
  id: string
  code: string
  name: string
  status: string
  startDate?: string
  endDate?: string
  managerUserId?: string
  version?: number
  members?: ProjectMember[]
}

export interface ProjectMember {
  id?: string
  userId: string
  projectRole: string
}

export interface Partner {
  id: string
  code: string
  name: string
  type: string
  contactName?: string
  contactPhone?: string
  taxId?: string
  bankName?: string
  bankAccount?: string
  status?: string
  version?: number
}

export interface Contract {
  id: string
  projectId: string
  partnerId: string
  contractNo: string
  title: string
  type?: string
  pricingType?: string
  currencyCode?: string
  originalAmount?: number
  committedAmount?: number
  status: string
  version?: number
  createdBy?: string
}

export interface ApprovalDefinition {
  id: string
  code: string
  name: string
  targetType: string
  active: boolean
  version?: number
  steps?: ApprovalStep[]
}

export interface ApprovalStep {
  id?: string
  stepOrder: number
  name: string
  assigneeType: string
  assigneeValue: string
  minApprovals?: number
}

export interface ApprovalTask {
  id: string
  instanceId: string
  targetType: string
  targetId: string
  projectId?: string
  status: string
  createdAt?: string
}

export interface DocumentMeta {
  id: string
  fileName: string
  ownerType: string
  ownerId: string
  contentType?: string
  sizeBytes?: number
}

export interface ChangeOrder {
  id: string
  contractId: string
  changeNo: string
  title: string
  amountDelta: number
  currencyCode?: string
  reason?: string
  status: string
  version?: number
}

export interface Payment {
  id: string
  contractId: string
  paymentNo: string
  paymentType: string
  periodLabel?: string
  amount: number
  currencyCode?: string
  status: string
  version?: number
}

export interface Settlement {
  id: string
  contractId: string
  settlementNo: string
  finalAmount: number
  currencyCode?: string
  status: string
  sealedAt?: string
  version?: number
}

export interface ContractSummaryRow {
  contractId: string
  contractNo: string
  projectId: string
  status: string
  currencyCode: string
  originalAmount: number
  committedAmount: number
  paidAmount: number
  remainingAmount: number
}

export interface AuditEvent {
  id: string
  action: string
  entityType: string
  entityId: string
  actorUserId?: string
  projectId?: string
  createdAt?: string
}
