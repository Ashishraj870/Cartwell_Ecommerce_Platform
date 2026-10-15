import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { api, money } from '../api'
import { useCart } from '../store'

export default function CartPage() {
  const { cart, setQty, remove, refresh } = useCart()
  const nav = useNavigate()
  const [address, setAddress] = useState('')
  const [err, setErr] = useState('')

  const run = (fn) => async () => { setErr(''); try { await fn() } catch (e) { setErr(e.message) } }

  const placeOrder = async (e) => {
    e.preventDefault(); setErr('')
    try { await api('/orders', { method: 'POST', body: { shippingAddress: address } }); await refresh(); nav('/orders') }
    catch (e) { setErr(e.message) }
  }

  if (cart.items.length === 0)
    return <p className="empty">Your cart is empty. <Link to="/">Browse products</Link></p>

  return (
    <div className="two-col">
      <section className="panel">
        <h1>Your cart</h1>
        {cart.items.map((i) => (
          <div key={i.productId} className="line">
            <img src={i.imageUrl} alt="" />
            <div className="grow"><strong>{i.name}</strong><div className="muted">{money(i.price)} each</div></div>
            <div className="qty">
              <button aria-label="Decrease" onClick={run(() => setQty(i.productId, i.quantity - 1))}>−</button>
              <span>{i.quantity}</span>
              <button aria-label="Increase" disabled={i.quantity >= i.stock} onClick={run(() => setQty(i.productId, i.quantity + 1))}>+</button>
            </div>
            <strong className="price">{money(i.price * i.quantity)}</strong>
            <button className="link" onClick={run(() => remove(i.productId))}>Remove</button>
          </div>
        ))}
      </section>

      <form className="panel" onSubmit={placeOrder}>
        <h2>Checkout</h2>
        <div className="row"><span>Total</span><strong className="price big">{money(cart.total)}</strong></div>
        <label>Shipping address
          <textarea rows={4} value={address} onChange={(e) => setAddress(e.target.value)} required placeholder="House no., street, city, PIN" />
        </label>
        {err && <p className="error" role="alert">{err}</p>}
        <button className="btn">Place order</button>
        <p className="muted small-text">Payment is cash on delivery in this demo.</p>
      </form>
    </div>
  )
}
