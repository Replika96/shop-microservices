import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Plus, Search, Pencil, Trash2, X, Upload } from 'lucide-react'
import { getProducts, getCategories, createProduct, updateProduct, deleteProduct, uploadImage } from '../api/products'
import type { Product } from '../types'

const CATEGORY_NAMES: Record<string, string> = {
  BATH: 'Бани и чаны',
  GRILL: 'Мангалы и барбекю',
  FURNITURE: 'Мебель',
  GARDEN: 'Сад и огород',
  TOOLS: 'Инструменты',
  OTHER: 'Другое',
}

interface ProductForm {
  name: string; description: string; price: string
  stock: string; category: string; imageUrl: string
}

const emptyForm: ProductForm = {
  name: '', description: '', price: '', stock: '0', category: 'OTHER', imageUrl: '',
}

export function ProductsPage() {
  const qc = useQueryClient()
  const [search, setSearch] = useState('')
  const [categoryFilter, setCategoryFilter] = useState('')
  const [page, setPage] = useState(0)
  const [modal, setModal] = useState<'add' | 'edit' | null>(null)
  const [editingProduct, setEditingProduct] = useState<Product | null>(null)
  const [form, setForm] = useState<ProductForm>(emptyForm)
  const [uploading, setUploading] = useState(false)
  const [deleteId, setDeleteId] = useState<number | null>(null)

  const { data, isLoading } = useQuery({
    queryKey: ['products', search, categoryFilter, page],
    queryFn: () => getProducts({ search: search || undefined, category: categoryFilter || undefined, page, size: 12 }),
    placeholderData: prev => prev,
  })

  const { data: categories } = useQuery({
    queryKey: ['categories'],
    queryFn: getCategories,
  })

  const createMutation = useMutation({
    mutationFn: (f: ProductForm) => createProduct({
      name: f.name, description: f.description,
      price: Number(f.price), stock: Number(f.stock),
      category: f.category, imageUrl: f.imageUrl,
    }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['products'] }); closeModal() },
  })

  const updateMutation = useMutation({
    mutationFn: (f: ProductForm) => updateProduct(editingProduct!.id, {
      name: f.name, description: f.description,
      price: Number(f.price), stock: Number(f.stock),
      category: f.category, imageUrl: f.imageUrl,
    }),
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['products'] }); closeModal() },
  })

  const deleteMutation = useMutation({
    mutationFn: deleteProduct,
    onSuccess: () => { qc.invalidateQueries({ queryKey: ['products'] }); setDeleteId(null) },
  })

  const openAdd = () => { setForm(emptyForm); setModal('add') }

  const openEdit = (p: Product) => {
    setEditingProduct(p)
    setForm({ name: p.name, description: p.description, price: String(p.price), stock: String(p.stock), category: p.category, imageUrl: p.imageUrl })
    setModal('edit')
  }

  const closeModal = () => { setModal(null); setEditingProduct(null); setForm(emptyForm) }

  const handleImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return
    setUploading(true)
    try {
      const url = await uploadImage(file)
      setForm(f => ({ ...f, imageUrl: url }))
    } catch {
      alert('Ошибка загрузки изображения')
    } finally {
      setUploading(false)
      e.target.value = ''
    }
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    if (modal === 'add') createMutation.mutate(form)
    else updateMutation.mutate(form)
  }

  const isSaving = createMutation.isPending || updateMutation.isPending

  return (
    <div className="p-6 space-y-4">
      {/* Header */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">Товары</h1>
        <button onClick={openAdd} className="flex items-center gap-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors text-sm font-medium">
          <Plus size={16} /> Добавить
        </button>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-xl shadow-sm p-4 flex flex-wrap gap-3">
        <div className="relative flex-1 min-w-48">
          <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
          <input
            value={search}
            onChange={e => { setSearch(e.target.value); setPage(0) }}
            placeholder="Поиск по названию..."
            className="w-full pl-9 pr-3 py-2 border border-gray-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
        <select
          value={categoryFilter}
          onChange={e => { setCategoryFilter(e.target.value); setPage(0) }}
          className="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white"
        >
          <option value="">Все категории</option>
          {categories?.map(c => <option key={c} value={c}>{CATEGORY_NAMES[c] ?? c}</option>)}
        </select>
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
                    {['Товар', 'Категория', 'Цена', 'Остаток', ''].map(h => (
                      <th key={h} className={`${h ? 'text-left' : 'text-right'} px-6 py-3 text-gray-500 font-medium`}>{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {data?.content.map(p => (
                    <tr key={p.id} className="hover:bg-gray-50">
                      <td className="px-6 py-3">
                        <div className="flex items-center gap-3">
                          {p.imageUrl
                            ? <img src={p.imageUrl} alt={p.name} className="w-10 h-10 object-cover rounded-lg bg-gray-100 shrink-0" />
                            : <div className="w-10 h-10 bg-gray-100 rounded-lg shrink-0 flex items-center justify-center text-gray-400 text-xs">—</div>
                          }
                          <div>
                            <div className="font-medium text-gray-900">{p.name}</div>
                            <div className="text-xs text-gray-400 truncate max-w-xs">{p.description}</div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-3 text-gray-600">{CATEGORY_NAMES[p.category] ?? p.category}</td>
                      <td className="px-6 py-3 font-medium">{Number(p.price).toLocaleString('ru-RU')} ₽</td>
                      <td className="px-6 py-3">
                        <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                          p.stock > 10 ? 'bg-green-100 text-green-700'
                          : p.stock > 0 ? 'bg-yellow-100 text-yellow-700'
                          : 'bg-red-100 text-red-700'
                        }`}>
                          {p.stock} шт.
                        </span>
                      </td>
                      <td className="px-6 py-3">
                        <div className="flex items-center justify-end gap-1">
                          <button onClick={() => openEdit(p)} className="p-1.5 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors">
                            <Pencil size={15} />
                          </button>
                          <button onClick={() => setDeleteId(p.id)} className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors">
                            <Trash2 size={15} />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {data?.content.length === 0 && (
                <div className="text-center py-12 text-gray-400">Товары не найдены</div>
              )}
            </div>

            {/* Pagination */}
            {(data?.totalPages ?? 0) > 1 && (
              <div className="flex items-center justify-between px-6 py-4 border-t border-gray-100">
                <span className="text-sm text-gray-500">Всего: {data?.totalElements}</span>
                <div className="flex items-center gap-2">
                  <button onClick={() => setPage(p => p - 1)} disabled={page === 0} className="px-3 py-1.5 text-sm border border-gray-200 rounded-lg disabled:opacity-40 hover:bg-gray-50">←</button>
                  <span className="text-sm text-gray-600 px-1">{page + 1} / {data?.totalPages}</span>
                  <button onClick={() => setPage(p => p + 1)} disabled={page + 1 >= (data?.totalPages ?? 0)} className="px-3 py-1.5 text-sm border border-gray-200 rounded-lg disabled:opacity-40 hover:bg-gray-50">→</button>
                </div>
              </div>
            )}
          </>
        )}
      </div>

      {/* Add / Edit Modal */}
      {modal && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl w-full max-w-md max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100 sticky top-0 bg-white rounded-t-2xl">
              <h2 className="font-semibold text-lg">{modal === 'add' ? 'Добавить товар' : 'Редактировать'}</h2>
              <button onClick={closeModal} className="p-1 hover:bg-gray-100 rounded-lg"><X size={18} /></button>
            </div>

            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Название *</label>
                <input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} required
                  className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Описание</label>
                <textarea value={form.description} onChange={e => setForm(f => ({ ...f, description: e.target.value }))} rows={3}
                  className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none" />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Цена (₽) *</label>
                  <input type="number" step="0.01" min="0.01" value={form.price} onChange={e => setForm(f => ({ ...f, price: e.target.value }))} required
                    className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">Остаток</label>
                  <input type="number" min="0" value={form.stock} onChange={e => setForm(f => ({ ...f, stock: e.target.value }))}
                    className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500" />
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Категория</label>
                <select value={form.category} onChange={e => setForm(f => ({ ...f, category: e.target.value }))}
                  className="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 bg-white">
                  {categories?.map(c => <option key={c} value={c}>{CATEGORY_NAMES[c] ?? c}</option>)}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">Фото</label>
                <div className="flex items-center gap-3">
                  {form.imageUrl && (
                    <img src={form.imageUrl} alt="preview" className="w-16 h-16 object-cover rounded-lg border border-gray-200 shrink-0" />
                  )}
                  <label className="flex items-center gap-2 px-3 py-2 border border-gray-200 rounded-lg text-sm text-gray-600 hover:bg-gray-50 cursor-pointer transition-colors">
                    <Upload size={14} />
                    {uploading ? 'Загрузка...' : 'Выбрать фото'}
                    <input type="file" accept="image/jpeg,image/png,image/webp" onChange={handleImageUpload} className="hidden" disabled={uploading} />
                  </label>
                </div>
              </div>

              {(createMutation.isError || updateMutation.isError) && (
                <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg px-3 py-2 text-sm">
                  Ошибка сохранения. Проверьте данные.
                </div>
              )}

              <div className="flex gap-3 pt-2">
                <button type="button" onClick={closeModal} className="flex-1 px-4 py-2.5 border border-gray-200 rounded-lg text-sm font-medium hover:bg-gray-50 transition-colors">
                  Отмена
                </button>
                <button type="submit" disabled={isSaving} className="flex-1 px-4 py-2.5 bg-blue-600 text-white rounded-lg text-sm font-medium hover:bg-blue-700 disabled:opacity-50 transition-colors">
                  {isSaving ? 'Сохранение...' : 'Сохранить'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete confirm */}
      {deleteId !== null && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl shadow-xl p-6 w-full max-w-sm">
            <h2 className="font-semibold text-lg mb-1">Удалить товар?</h2>
            <p className="text-gray-500 text-sm mb-5">Это действие нельзя отменить.</p>
            <div className="flex gap-3">
              <button onClick={() => setDeleteId(null)} className="flex-1 px-4 py-2.5 border border-gray-200 rounded-lg text-sm font-medium hover:bg-gray-50">
                Отмена
              </button>
              <button onClick={() => deleteMutation.mutate(deleteId)} disabled={deleteMutation.isPending}
                className="flex-1 px-4 py-2.5 bg-red-600 text-white rounded-lg text-sm font-medium hover:bg-red-700 disabled:opacity-50">
                {deleteMutation.isPending ? 'Удаление...' : 'Удалить'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
