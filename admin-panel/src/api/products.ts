import { api } from './client'
import type { PageResponse, Product } from '../types'

export interface ProductFilters {
  search?: string
  category?: string
  page?: number
  size?: number
  sortBy?: string
  sortDir?: string
}

export const getProducts = (filters: ProductFilters = {}) =>
  api.get<PageResponse<Product>>('/products', {
    params: { page: 0, size: 20, sortBy: 'id', sortDir: 'asc', ...filters },
  }).then(r => r.data)

export const getCategories = () =>
  api.get<string[]>('/products/categories').then(r => r.data)

export const uploadImage = (file: File) => {
  const fd = new FormData()
  fd.append('file', file)
  return api.post<{ url: string }>('/products/upload-image', fd).then(r => r.data.url)
}

export const createProduct = (data: {
  name: string; description: string; price: number
  stock: number; category: string; imageUrl: string
}) => api.post<Product>('/products', data).then(r => r.data)

export const updateProduct = (id: number, data: {
  name?: string; description?: string; price?: number
  stock?: number; category?: string; imageUrl?: string
}) => api.patch<Product>(`/products/${id}`, data).then(r => r.data)

export const deleteProduct = (id: number) => api.delete(`/products/${id}`)
