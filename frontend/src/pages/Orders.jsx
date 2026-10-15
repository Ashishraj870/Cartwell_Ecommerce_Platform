import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, money } from '../api'

const STEPS = ['PLACED', 'CONFIRMED', 'SHIPPED', 'DELIVERED']
const label = (s) => s.charAt(0) + s.slice(1).toLowerCase()

export function Tracker({ status }) {
  if (status === 'CANCELLED') return <span className="tag bad">Cancelled</span>
  const at = STEPS.indexOf(status)
  return (
    <ol className="track">
      {STEPS.map((s, i) => <li key={s} className={i <= at ? 'done' : ''}>{label(s)}</li>)}
    </ol>
  )
}

export default function Orders() {
  const [orders, setOrders] = useState(null)
  const [err, setErr] = useState('')
  const load = () => api('/orders').then(setOrders).catch((e) => setErr(e.message))
  useEffect(() => { load() }, [])

  const cancel = async (id) => {
    if (!window.confirm('Cancel this order?')) return
    try { await api(`/orders/${id}/cancel`, { method: 'POST' }); load() } catch (e) { setErr(e.message) }
  }

  if (!orders) return <p className="muted">Loading your orders…</p>
  if (orders.length === 0) return <p className="empty">You haven't placed any orders yet. <Link to="/">Start shopping</Link></p>

  return (
    <>
      <h1>My orders</h1>
      {err && <p className="error" role="alert">{err}</p>}
      {orders.map((o) => (
        <section key={o.id} className="panel order">
          <div className="row">
            <div><strong>Order #{o.id}</strong><div className="muted small-text">{new Date(o.createdAt).toLocaleString()}</div></div>
            <strong className="price">{money(o.total)}</strong>
          </div>
          <Tracker status={o.status} />
          <ul className="items">
            {o.items.map((i) => <li key={i.productId}>{i.quantity} × {i.name}<span>{money(i.unitPrice * i.quantity)}</span></li>)}
          </ul>
          <div className="row">
            <span className="muted small-text">Ships to: {o.shippingAddress}</span>
            {(o.status === 'PLACED' || o.status === 'CONFIRMED') && <button className="btn ghost small" onClick={() => cancel(o.id)}>Cancel order</button>}
          </div>
        </section>
      ))}
    </>
  )
}
