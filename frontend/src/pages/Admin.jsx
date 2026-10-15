import { useEffect, useState } from 'react'
import { api, money } from '../api'

const STATUSES = ['PLACED', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED']
const empty = { name: '', description: '', price: '', stock: 0, imageUrl: '', categoryId: '' }

function Products() {
  const [products, setProducts] = useState([])
  const [categories, setCategories] = useState([])
  const [form, setForm] = useState(empty)
  const [editing, setEditing] = useState(null)
  const [newCat, setNewCat] = useState('')
  const [err, setErr] = useState('')

  const load = () => api('/products?size=100').then((d) => setProducts(d.content))
  useEffect(() => { load(); api('/categories').then(setCategories) }, [])
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })

  const save = async (e) => {
    e.preventDefault(); setErr('')
    const body = { ...form, price: Number(form.price), stock: Number(form.stock), categoryId: Number(form.categoryId) }
    try {
      await api(editing ? `/products/${editing}` : '/products', { method: editing ? 'PUT' : 'POST', body })
      setForm(empty); setEditing(null); load()
    } catch (e) { setErr(e.message) }
  }
  const edit = (p) => { setEditing(p.id); setForm({ name: p.name, description: p.description || '', price: p.price, stock: p.stock, imageUrl: p.imageUrl || '', categoryId: p.categoryId }) }
  const del = async (p) => { if (window.confirm(`Remove "${p.name}" from the catalog?`)) { await api(`/products/${p.id}`, { method: 'DELETE' }); load() } }
  const addCat = async (e) => {
    e.preventDefault()
    try { await api('/categories', { method: 'POST', body: { name: newCat } }); setNewCat(''); setCategories(await api('/categories')) } catch (e) { setErr(e.message) }
  }

  return (
    <div className="two-col wide-left">
      <section className="panel">
        <table>
          <thead><tr><th>Product</th><th>Category</th><th>Price</th><th>Stock</th><th></th></tr></thead>
          <tbody>
            {products.map((p) => (
              <tr key={p.id}>
                <td>{p.name}</td><td>{p.categoryName}</td><td>{money(p.price)}</td>
                <td className={p.stock < 10 ? 'low' : ''}>{p.stock}</td>
                <td className="actions"><button className="link" onClick={() => edit(p)}>Edit</button><button className="link danger" onClick={() => del(p)}>Remove</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
      <div>
        <form className="panel" onSubmit={save}>
          <h2>{editing ? 'Edit product' : 'Add a product'}</h2>
          <label>Name<input value={form.name} onChange={set('name')} required /></label>
          <label>Description<textarea rows={3} value={form.description} onChange={set('description')} /></label>
          <div className="pair">
            <label>Price (₹)<input type="number" min="0.01" step="0.01" value={form.price} onChange={set('price')} required /></label>
            <label>Stock<input type="number" min="0" value={form.stock} onChange={set('stock')} required /></label>
          </div>
          <label>Image URL<input value={form.imageUrl} onChange={set('imageUrl')} /></label>
          <label>Category
            <select value={form.categoryId} onChange={set('categoryId')} required>
              <option value="">Choose a category</option>
              {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </label>
          {err && <p className="error" role="alert">{err}</p>}
          <div className="row">
            <button className="btn">{editing ? 'Save changes' : 'Add product'}</button>
            {editing && <button type="button" className="link" onClick={() => { setEditing(null); setForm(empty) }}>Cancel edit</button>}
          </div>
        </form>
        <form className="panel" onSubmit={addCat}>
          <h2>New category</h2>
          <div className="row"><input value={newCat} onChange={(e) => setNewCat(e.target.value)} required placeholder="Category name" /><button className="btn small">Add</button></div>
        </form>
      </div>
    </div>
  )
}

function AllOrders() {
  const [orders, setOrders] = useState([])
  const [err, setErr] = useState('')
  const load = () => api('/admin/orders').then(setOrders)
  useEffect(() => { load() }, [])
  const change = async (id, status) => {
    setErr('')
    try { await api(`/admin/orders/${id}/status`, { method: 'PUT', body: { status } }) } catch (e) { setErr(e.message) }
    load()
  }
  return (
    <section className="panel">
      {err && <p className="error" role="alert">{err}</p>}
      {orders.length === 0 ? <p className="empty">No orders yet.</p> : (
        <table>
          <thead><tr><th>Order</th><th>Customer</th><th>Items</th><th>Total</th><th>Status</th></tr></thead>
          <tbody>
            {orders.map((o) => (
              <tr key={o.id}>
                <td>#{o.id}<div className="muted small-text">{new Date(o.createdAt).toLocaleDateString()}</div></td>
                <td>{o.customerName}<div className="muted small-text">{o.customerEmail}</div></td>
                <td>{o.items.map((i) => `${i.quantity}× ${i.name}`).join(', ')}</td>
                <td>{money(o.total)}</td>
                <td>
                  <select value={o.status} disabled={o.status === 'CANCELLED' || o.status === 'DELIVERED'} onChange={(e) => change(o.id, e.target.value)}>
                    {STATUSES.map((s) => <option key={s}>{s}</option>)}
                  </select>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </section>
  )
}

export default function Admin() {
  const [tab, setTab] = useState('orders')
  return (
    <>
      <h1>Admin</h1>
      <div className="chips">
        <button className={tab === 'orders' ? 'chip on' : 'chip'} onClick={() => setTab('orders')}>Orders</button>
        <button className={tab === 'products' ? 'chip on' : 'chip'} onClick={() => setTab('products')}>Products</button>
      </div>
      {tab === 'orders' ? <AllOrders /> : <Products />}
    </>
  )
}
