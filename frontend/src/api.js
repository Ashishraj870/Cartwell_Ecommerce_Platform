// Thin fetch wrapper: attaches the JWT and turns error bodies into Error messages.
export async function api(path, { method = 'GET', body } = {}) {
  const token = localStorage.getItem('token')
  const res = await fetch('/api' + path, {
    method,
    headers: { 'Content-Type': 'application/json', ...(token && { Authorization: `Bearer ${token}` }) },
    body: body ? JSON.stringify(body) : undefined,
  })
  if (res.status === 401 && token) { // expired or invalid token
    localStorage.clear()
    window.location.href = '/login'
  }
  const data = res.status === 204 ? null : await res.json().catch(() => null)
  if (!res.ok) throw new Error(data?.message || 'Something went wrong. Try again.')
  return data
}

export const money = (n) => '₹' + Number(n).toLocaleString('en-IN', { minimumFractionDigits: 0, maximumFractionDigits: 2 })
