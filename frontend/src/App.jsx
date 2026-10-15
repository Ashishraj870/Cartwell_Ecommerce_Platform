import { Routes, Route, NavLink, Link, Navigate } from 'react-router-dom'
import { useAuth, useCart } from './store'
import Catalog from './pages/Catalog'
import CartPage from './pages/CartPage'
import Orders from './pages/Orders'
import { Login, Register } from './pages/Auth'
import Admin from './pages/Admin'

function Protected({ admin, children }) {
  const { user } = useAuth()
  if (!user) return <Navigate to="/login" replace />
  if (admin && user.role !== 'ADMIN') return <Navigate to="/" replace />
  return children
}

export default function App() {
  const { user, logout } = useAuth()
  const { cart } = useCart()
  return (
    <>
      <header className="nav">
        <Link to="/" className="brand">Cartwell</Link>
        <nav>
          <NavLink to="/" end>Shop</NavLink>
          {user && <NavLink to="/orders">My orders</NavLink>}
          {user?.role === 'ADMIN' && <NavLink to="/admin">Admin</NavLink>}
          {user && <NavLink to="/cart">Cart{cart.count > 0 && <span className="badge">{cart.count}</span>}</NavLink>}
        </nav>
        <div className="who">
          {user ? (<><span>{user.name}</span><button className="link" onClick={logout}>Sign out</button></>)
                : (<><Link to="/login">Sign in</Link><Link to="/register" className="btn small">Create account</Link></>)}
        </div>
      </header>
      <main>
        <Routes>
          <Route path="/" element={<Catalog />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/cart" element={<Protected><CartPage /></Protected>} />
          <Route path="/orders" element={<Protected><Orders /></Protected>} />
          <Route path="/admin" element={<Protected admin><Admin /></Protected>} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </>
  )
}
