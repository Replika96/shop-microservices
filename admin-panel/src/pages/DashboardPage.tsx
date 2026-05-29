import { useQuery } from '@tanstack/react-query'
import { Package, ShoppingCart, Clock, Truck } from 'lucide-react'
import { getProducts } from '../api/products'
import { getOrders } from '../api/orders'

export const STATUS_LABELS: Record<string, string> = {
  PENDING: 'Ожидает',
  CONFIRMED: 'Подтверждён',
  SHIPPED: 'Отправлен',
  DELIVERED: 'Доставлен',
  CANCELLED: 'Отменён',
}

export const STATUS_COLORS: Record<string, string> = {
  PENDING: 'bg-yellow-100 text-yellow-800',
  CONFIRMED: 'bg-blue-100 text-blue-800',
  SHIPPED: 'bg-indigo-100 text-indigo-800',
  DELIVERED: 'bg-green-100 text-green-800',
  CANCELLED: 'bg-red-100 text-red-800',
}

export function DashboardPage() {
  const { data: products } = useQuery({
    queryKey: ['products-count'],
    queryFn: () => getProducts({ page: 0, size: 1 }),
  })

  const { data: orders } = useQuery({
    queryKey: ['orders-dashboard'],
    queryFn: () => getOrders(0, 10),
  })

  const statusCounts = orders?.content.reduce((acc, o) => {
    acc[o.status] = (acc[o.status] || 0) + 1
    return acc
  }, {} as Record<string, number>) ?? {}

  const stats = [
    { label: 'Всего товаров', value: products?.totalElements ?? '—', icon: Package, color: 'bg-blue-500' },
    { label: 'Всего заказов', value: orders?.totalElements ?? '—', icon: ShoppingCart, color: 'bg-emerald-500' },
    { label: 'Ожидают обработки', value: statusCounts['PENDING'] ?? 0, icon: Clock, color: 'bg-yellow-500' },
    { label: 'В доставке', value: statusCounts['SHIPPED'] ?? 0, icon: Truck, color: 'bg-indigo-500' },
  ]

  return (
    <div className="p-6 space-y-6">
      <h1 className="text-2xl font-bold text-gray-900">Дашборд</h1>

      {/* Stats cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        {stats.map(({ label, value, icon: Icon, color }) => (
          <div key={label} className="bg-white rounded-xl shadow-sm p-5 flex items-center gap-4">
            <div className={`${color} text-white p-3 rounded-xl shrink-0`}>
              <Icon size={22} />
            </div>
            <div>
              <div className="text-2xl font-bold text-gray-900">{value}</div>
              <div className="text-sm text-gray-500">{label}</div>
            </div>
          </div>
        ))}
      </div>

      {/* Recent orders */}
      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-100">
          <h2 className="font-semibold text-gray-900">Последние заказы</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50">
              <tr>
                {['ID', 'Покупатель', 'Сумма', 'Статус', 'Дата'].map(h => (
                  <th key={h} className="text-left px-6 py-3 text-gray-500 font-medium">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {orders?.content.map(order => (
                <tr key={order.id} className="hover:bg-gray-50">
                  <td className="px-6 py-3 font-medium text-gray-900">#{order.id}</td>
                  <td className="px-6 py-3 text-gray-600">{order.userEmail}</td>
                  <td className="px-6 py-3 font-medium">{Number(order.totalPrice).toLocaleString('ru-RU')} ₽</td>
                  <td className="px-6 py-3">
                    <span className={`px-2.5 py-1 rounded-full text-xs font-medium ${STATUS_COLORS[order.status] ?? 'bg-gray-100 text-gray-700'}`}>
                      {STATUS_LABELS[order.status] ?? order.status}
                    </span>
                  </td>
                  <td className="px-6 py-3 text-gray-500">
                    {new Date(order.createdAt).toLocaleDateString('ru-RU')}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {orders?.content.length === 0 && (
            <div className="text-center py-12 text-gray-400">Заказов пока нет</div>
          )}
        </div>
      </div>
    </div>
  )
}
