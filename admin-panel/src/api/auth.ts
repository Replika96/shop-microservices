import { api } from './client'
import type { AuthResponse } from '../types'

export const loginApi = (email: string, password: string) =>
  api.post<AuthResponse>('/auth/login', { email, password }).then(r => r.data)
