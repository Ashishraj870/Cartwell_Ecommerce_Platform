import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { api } from '../api'
import { useAuth } from '../store'

function AuthForm({ mode }) {
  const register = mode === 'register'
  const { login } = useAuth()
  const nav = useNavigate()
  const [f, setF] = useState({ name: '', email: '', password: '' })
  const [err, setErr] = useState('')
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })

  const submit = async (e) => {
    e.preventDefault(); setErr('')
    try {
      const data = await api(register ? '/auth/register' : '/auth/login', { method: 'POST', body: f })
      login(data)
      nav(data.role === 'ADMIN' ? '/admin' : '/')
    } catch (e) { setErr(e.message) }
  }

  return (
    <form className="panel narrow" onSubmit={submit}>
      <h1>{register ? 'Create your account' : 'Sign in'}</h1>
      {register && <label>Name<input value={f.name} onChange={set('name')} required /></label>}
      <label>Email<input type="email" value={f.email} onChange={set('email')} required /></label>
      <label>Password<input type="password" value={f.password} onChange={set('password')} required minLength={register ? 6 : 1} /></label>
      {err && <p className="error" role="alert">{err}</p>}
      <button className="btn">{register ? 'Create account' : 'Sign in'}</button>
      <p className="muted">
        {register ? <>Already have an account? <Link to="/login">Sign in</Link></>
                  : <>New here? <Link to="/register">Create an account</Link></>}
      </p>
      {!register && <p className="muted small-text">Demo: customer@shop.com / Customer@123 · admin@shop.com / Admin@123</p>}
    </form>
  )
}
export const Login = () => <AuthForm mode="login" />
export const Register = () => <AuthForm mode="register" />
