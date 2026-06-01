export interface AuthResponse {
  token: string
  refreshToken: string
  email: string
  name: string
  role: string
}

export interface Product {
  id: number
  name: string
  description: string
  price: number
  stock: number
  category: string
  imageUrl: string      // первое фото (обратная совместимость)
  imageUrls: string[]   // все фото
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  page: number
  size: number
}

export interface OrderItem {
  id: number
  productId: number
  productName: string
  price: number
  quantity: number
  imageUrl: string
  total: number
}

export interface Order {
  id: number
  userEmail: string
  totalPrice: number
  status: string
  address: string
  createdAt: string
  items: OrderItem[]
}
