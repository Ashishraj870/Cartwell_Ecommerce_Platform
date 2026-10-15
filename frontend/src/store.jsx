import { createContext, useContext, useEffect, useState, useCallback } from 'react'
import { api } from './api'

const AuthCtx = createContext(null)
const CartCtx = createContext(null)
export const useAuth = () => useContext(AuthCtx)
export const useCart = () => useContext(CartCtx)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => JSON.parse(localStorage.getItem('user') || 'null'))
  const login = (data) => {
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify({ name: data.name, email: data.email, role: data.role }))
    setUser({ name: data.name, email: data.email, role: data.role })
  }
  const logout = () => { localStorage.clear(); setUser(null) }
  return <AuthCtx.Provider value={{ user, login, logout }}>{children}</AuthCtx.Provider>
}

// Cart lives on the server; this context just mirrors it so the navbar badge stays current.
export function CartProvider({ children }) {
  const { user } = useAuth()
  const [cart, setCart] = useState({ items: [], total: 0, count: 0 })

  const refresh = useCallback(async () => {
    if (!user) return setCart({ items: [], total: 0, count: 0 })
    try { setCart(await api('/cart')) } catch { /* handled by api() on 401 */ }
  }, [user])
  useEffect(() => { refresh() }, [refresh])

  const add = async (productId, quantity = 1) => setCart(await api('/cart/items', { method: 'POST', body: { productId, quantity } }))
  const setQty = async (productId, quantity) => setCart(await api(`/cart/items/${productId}`, { method: 'PUT', body: { quantity } }))
  const remove = async (productId) => setCart(await api(`/cart/items/${productId}`, { method: 'DELETE' }))

  return <CartCtx.Provider value={{ cart, refresh, add, setQty, remove }}>{children}</CartCtx.Provider>
}
