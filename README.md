# Cartwell: Full-Stack E-Commerce Platform

Spring Boot 3 + Spring Security (JWT) + MySQL + React 18 (Vite).

## Features
- Product catalog with debounced search, category filter and pagination
- Server-side cart (persists across devices) and checkout
- Orders: place, track (Placed > Confirmed > Shipped > Delivered), cancel
- Admin dashboard: add/edit/remove products, add categories, view all orders, update order status
- JWT auth with role-based access control (CUSTOMER, ADMIN)

## Run it
**Prerequisites:** Java 17+, Maven 3.9+, Node 18+, MySQL 8 running locally.

```bash
# 1. Backend (http://localhost:8080). The database is created automatically.
cd backend
export DB_USER=root DB_PASS=yourpassword      # defaults: root / root
mvn spring-boot:run

# 2. Frontend (http://localhost:5173)
cd frontend
npm install
npm run dev
```

Demo accounts (seeded on first run):
| Role | Email | Password |
|---|---|---|
| Admin | admin@shop.com | Admin@123 |
| Customer | customer@shop.com | Customer@123 |

## Database schema (7 tables, 3NF)
```
users 1──1 carts 1──* cart_items *──1 products *──1 categories
users 1──* orders 1──* order_items *──1 products
```
| Table | Notes |
|---|---|
| users | unique email, BCrypt password, role enum |
| categories | unique name |
| products | price DECIMAL(10,2), stock, soft-delete flag, FK to category, index on name |
| carts | one per user (unique FK) |
| cart_items | unique (cart_id, product_id) prevents duplicate lines |
| orders | status enum, total, shipping address, timestamp |
| order_items | **unit price snapshot** so price edits never change history |

## API
| Method | Path | Access |
|---|---|---|
| POST | /api/auth/register, /api/auth/login | public |
| GET | /api/products?q=&categoryId=&page=&size= | public |
| GET | /api/products/{id}, /api/categories | public |
| POST/PUT/DELETE | /api/products, /api/products/{id}, POST /api/categories | ADMIN |
| GET/POST/PUT/DELETE | /api/cart, /api/cart/items, /api/cart/items/{productId} | authenticated |
| POST | /api/orders (checkout from cart) | authenticated |
| GET | /api/orders, /api/orders/{id} | owner (admin can view any) |
| POST | /api/orders/{id}/cancel | owner, only while PLACED/CONFIRMED |
| GET | /api/admin/orders | ADMIN |
| PUT | /api/admin/orders/{id}/status | ADMIN |

## Design decisions (interview talking points)
1. **Stateless JWT + RBAC.** `JwtFilter` validates the token on every request and builds the security context with a `ROLE_` authority. `SecurityConfig` guards routes by role. Public sign-up can only create customers; admins are provisioned separately, which prevents privilege escalation.
2. **Server-side cart.** Stored in `carts`/`cart_items`, so a customer who signs in on another device sees the same cart.
3. **Transactional checkout.** One `@Transactional` method converts cart to order, decrements stock, snapshots prices and clears the cart. A failure rolls everything back.
4. **No overselling.** Stock rows are read with `PESSIMISTIC_WRITE` (`SELECT ... FOR UPDATE`), so concurrent checkouts for the last unit serialize.
5. **Cancellation restocks inventory** and is only allowed before shipping. Admin status changes can only move forward.
6. **Soft-deleting products** preserves order history integrity.
7. **DTOs (Java records)** keep entities out of the API; validation errors return clean `{message}` JSON.

## Ideas to extend
Payments (Razorpay/Stripe), product images upload, refresh tokens, order-confirmation emails, Flyway migrations, integration tests with Testcontainers, Dockerfile + docker-compose.
