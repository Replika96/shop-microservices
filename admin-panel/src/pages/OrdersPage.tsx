import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { ChevronDown, ChevronRight } from 'lucide-react'
import { getOrders, updateOrderStatus } from '../api/orders'
import { STATUS_LABELS, STATUS_COLORS } from './DashboardPage'

const ALL_STATUSES = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED']

export function OrdersPage() {
  const qc = useQueryClient()
  const [page, setPage] = useState(0)
  const [statusFilter, setStatusFilter] = useState('')
  const [expandedId, setExpandedId] = useState<number | null>(null)

  const { data, isLoading } = useQuery({
    queryKey: ['orders', page, statusFilter],
    queryFn: () => getOrders(page, 15, statusFilter || undefined),
    placeholderData: prev => prev,
  })

  const statusMutation = useMutation({
    mutationFn: ({ id, status }: { id: number; status: string }) => updateOrderStatus(id, status),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['orders'] }),
  })

  return (
    <div className="p-6 space-y-4">
      {/* Header */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Заказы</h1>
      </div>

      {/* Filter */}
      <div className="bg-white rounded-xl shadow-sm p-4 flex flex-wrap gap-3">
        <select
          value={statusFilter}
          onChange={e => { setStatusFilter(e.target.value); setPage(0) }}
          className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
        >
          <option value="">Все статусы</option>
          {ALL_STATUSES.map(s => (
            <option key={s} value={s}>{STATUS_LABELS[s] ?? s}</option>
          ))}
        </select>
        {data && (
          <span className="self-center text-sm text-gray-500">
            Найдено: {data.totalElements}
          </span>
        )}
      </div>

      {/* Table */}
      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        {isLoading ? (
          <div className="text-center py-16 text-gray-400">Загрузка...</div>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-gray-50">
                  <tr>
                    {['', 'ID', 'Покупатель', 'Сумма', 'Статус', 'Дата', 'Изменить статус'].map((h, i) => (
                      <th key={i} className="text-left px-4 py-3 text-gray-500 font-medium whitespace-nowrap">{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {data?.content.map(order => (
                    <>
                      <tr
                        key={order.id}
                        className="hover:bg-gray-50 cursor-pointer"
                        onClick={() => setExpandedId(expandedId === order.id ? null : order.id)}
                      >
                        <td className="px-4 py-3 text-gray-400">
                          {expandedId === order.id
                            ? <ChevronDown size={15} />
                            : <ChevronRight size={15} />
                          }
                        </td>
                        <td className="px-4 py-3 font-medium text-gray-900">#{order.id}</td>
                        <td className="px-4 py-3 text-gray-600 max-w-[180px] truncate">{order.userEmail}</td>
                        <td className="px-4 py-3 font-medium whitespace-nowrap">
                          {Number(order.totalPrice).toLocaleString('ru-RU')} ₽
                        </td>
                        <td className="px-4 py-3">
                          <span className={`px-2.5 py-1 rounded-full text-xs font-medium whitespace-nowrap ${STATUS_COLORS[order.status] ?? 'bg-gray-100 text-gray-700'}`}>
                            {STATUS_LABELS[order.status] ?? order.status}
                          </span>
                        </td>
                        <td className="px-4 py-3 text-gray-500 whitespace-nowrap">
                          {new Date(order.createdAt).toLocaleDateString('ru-RU')}
                        </td>
                        <td className="px-4 py-3" onClick={e => e.stopPropagation()}>
                          <select
                            value={order.status}
                            onChange={e => statusMutation.mutate({ id: order.id, status: e.target.value })}
                            disabled={statusMutation.isPending}
                            className="border border-gray-200 rounded-lg px-2 py-1.5 text-xs focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white disabled:opacity-50"
                          >
                            {ALL_STATUSES.map(s => (
                              <option key={s} value={s}>{STATUS_LABELS[s] ?? s}</option>
                            ))}
                          </select>
                        </td>
                      </tr>

                      {expandedId === order.id && (
                        <tr key={`${order.id}-items`} className="bg-blue-50/40">
                          <td colSpan={7} className="px-8 py-3">
                            {order.items && order.items.length > 0 ? (
                              <div className="space-y-2">
                                <div className="text-xs font-medium text-gray-500 uppercase tracking-wide mb-2">Состав заказа</div>
                                <div className="grid gap-2">
                                  {order.items.map(item => (
                                    <div key={item.id} className="flex items-center gap-3 bg-white rounded-lg px-3 py-2 shadow-sm">
                                      {item.imageUrl
                                        ? <img src={item.imageUrl} alt={item.productName} className="w-9 h-9 object-cover rounded-md bg-gray-100 shrink-0" />
                                        : <div className="w-9 h-9 bg-gray-100 rounded-md shrink-0 flex items-center justify-center text-gray-400 text-xs">—</div>
                                      }
                                      <div className="flex-1 min-w-0">
                                        <div className="text-sm font-medium text-gray-800 truncate">{item.productName}</div>
                                      </div>
                                      <div className="text-sm text-gray-500 whitespace-nowrap">
                                        {item.quantity} × {Number(item.price).toLocaleString('ru-RU')} ₽
                                      </div>
                                      <div className="text-sm font-semibold text-gray-800 whitespace-nowrap">
                                        {(item.quantity * Number(item.price)).toLocaleString('ru-RU')} ₽
                                      </div>
                                    </div>
                                  ))}
                                </div>
                                <div className="flex justify-end pt-1">
                                  <span className="text-sm font-semibold text-gray-700">
                                    Итого: {Number(order.totalPrice).toLocaleString('ru-RU')} ₽
                                  </span>
                                </div>
                              </div>
                            ) : (
                              <div className="text-sm text-gray-400 py-1">Нет данных о составе заказа</div>
                            )}
                          </td>
                        </tr>
                      )}
                    </>
                  ))}
                </tbody>
              </table>

              {data?.content.length === 0 && (
                <div className="text-center py-12 text-gray-400">Заказов не найдено</div>
              )}
            </div>

            {/* Pagination */}
            {(data?.totalPages ?? 0) > 1 && (
              <div className="flex items-center justify-between px-6 py-4 border-t border-gray-100">
                <span className="text-sm text-gray-500">Всего: {data?.totalElements}</span>
                <div className="flex items-center gap-2">
                  <button
                    onClick={() => setPage(p => p - 1)}
                    disabled={page === 0}
                    className="px-3 py-1.5 text-sm border border-gray-200 rounded-lg disabled:opacity-40 hover:bg-gray-50"
                  >←</button>
                  <span className="text-sm text-gray-600 px-1">{page + 1} / {data?.totalPages}</span>
                  <button
                    onClick={() => setPage(p => p + 1)}
                    disabled={page + 1 >= (data?.totalPages ?? 0)}
                    className="px-3 py-1.5 text-sm border border-gray-200 rounded-lg disabled:opacity-40 hover:bg-gray-50"
                  >→</button>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}
