import type { TokenResponse } from '../types'
import { clearTokens, http, setTokens } from './http'

export async function login(username: string, password: string): Promise<TokenResponse> {
  const { data } = await http.post<TokenResponse>('/auth/login', { username, password })
  setTokens(data.accessToken, data.refreshToken)
  return data
}

export async function refresh(refreshToken: string): Promise<TokenResponse> {
  const { data } = await http.post<TokenResponse>('/auth/refresh', { refreshToken })
  setTokens(data.accessToken, data.refreshToken)
  return data
}

export async function logout(): Promise<void> {
  const refreshToken = localStorage.getItem('ccm_refresh_token')
  if (refreshToken) {
    try {
      await http.post('/auth/logout', { refreshToken })
    } catch {
      /* ignore */
    }
  }
  clearTokens()
}