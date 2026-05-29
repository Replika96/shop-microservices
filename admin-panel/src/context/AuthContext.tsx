import { createContext, useContext, useState, type ReactNode } from 'react'
import type { AuthResponse } from '../types'

interface User { email: string; name: string; role: string }

interface AuthContextType {
  token: string | null
  user: User | null
  login: (data: AuthResponse) => void
  logout: () => void
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'))
  const [user, setUser] = useState<User | null>(() => {
    const s = localStorage.getItem('user')
    return s ? JSON.parse(s) : null
  })

  const login = (data: AuthResponse) => {
    localStorage.setItem('token', data.token)
    const u = { email: data.email, name: data.name, role: data.role }
    localStorage.setItem('user', JSON.stringify(u))
    setToken(data.token)
    setUser(u)
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setToken(null)
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ token, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
