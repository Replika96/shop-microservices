import { api } from './client'
import type { PageResponse, Order } from '../types'

export const getOrders = (page = 0, size = 20, status?: string) =>
  api.get<PageResponse<Order>>('/orders', { params: { page, size, ...(status ? { status } : {}) } }).then(r => r.data)

export const updateOrderStatus = (id: number, status: string) =>
  api.patch<Order>(`/orders/${id}/status`, { status }).then(r => r.data)
