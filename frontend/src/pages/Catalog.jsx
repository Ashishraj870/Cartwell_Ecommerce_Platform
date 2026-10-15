import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api, money } from '../api'
import { useAuth, useCart } from '../store'

export default function Catalog() {
  const { user } = useAuth()
  const { add } = useCart()
  const nav = useNavigate()
  const [categories, setCategories] = useState([])
  const [q, setQ] = useState('')
  const [cat, setCat] = useState(null)
  const [page, setPage] = useState(0)
  const [data, setData] = useState({ content: [], totalPages: 0 })
  const [note, setNote] = useState('')

  useEffect(() => { api('/categories').then(setCategories) }, [])

  useEffect(() => { // debounce typing in the search box
    const t = setTimeout(() => {
      const p = new URLSearchParams({ page, size: 8 })
      if (q.trim()) p.set('q', q.trim())
      if (cat) p.set('categoryId', cat)
      api('/products?' + p).then(setData).catch((e) => setNote(e.message))
    }, 250)
    return () => clearTimeout(t)
  }, [q, cat, page])

  const addToCart = async (p) => {
    if (!user) return nav('/login')
    try { await add(p.id, 1); setNote(`${p.name} added to your cart`) } catch (e) { setNote(e.message) }
  }

  return (
    <>
      <div className="toolbar">
        <input className="search" type="search" placeholder="Search products" value={q}
               onChange={(e) => { setQ(e.target.value); setPage(0) }} aria-label="Search products" />
        <div className="chips">
          <button className={!cat ? 'chip on' : 'chip'} onClick={() => { setCat(null); setPage(0) }}>All</button>
          {categories.map((c) => (
            <button key={c.id} className={cat === c.id ? 'chip on' : 'chip'} onClick={() => { setCat(c.id); setPage(0) }}>{c.name}</button>
          ))}
        </div>
      </div>
      {note && <p className="note" role="status" onClick={() => setNote('')}>{note}</p>}

      {data.content.length === 0 ? <p className="empty">No products match. Try a different search or category.</p> : (
        <div className="grid">
          {data.content.map((p) => (
            <article key={p.id} className="card">
              <img src={p.imageUrl} alt={p.name} loading="lazy" />
              <div className="card-body">
                <span className="muted small-text">{p.categoryName}</span>
                <h3>{p.name}</h3>
                <p className="desc">{p.description}</p>
                <div className="row">
                  <strong className="price">{money(p.price)}</strong>
                  {p.stock > 0 ? <button className="btn small" onClick={() => addToCart(p)}>Add to cart</button>
                               : <span className="muted">Out of stock</span>}
                </div>
              </div>
            </article>
          ))}
        </div>
      )}

      {data.totalPages > 1 && (
        <div className="pager">
          <button className="btn ghost small" disabled={page === 0} onClick={() => setPage(page - 1)}>Previous</button>
          <span>Page {page + 1} of {data.totalPages}</span>
          <button className="btn ghost small" disabled={page + 1 >= data.totalPages} onClick={() => setPage(page + 1)}>Next</button>
        </div>
      )}
    </>
  )
}
