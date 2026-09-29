# Product Requirements Document — E-Commerce Full-Stack Platform

| | |
|---|---|
| **Document type** | Product Requirements Document (PRD) |
| **Companion document** | `Architecture.md` |
| **Last updated** | September 2026 |

**Status legend:** ✅ IMPLEMENTED · 🔧 IN PROGRESS · ⏭️ NEXT · 📋 PLANNED · 🔮 FUTURE. Planned features are not claimed to exist.

---

## 1. Product Overview

The E-Commerce Full-Stack Platform is a marketplace where **buyers** purchase products from **sellers**, and **administrators** manage the platform. Frontend: React.js. Backend: Spring Boot microservices with MySQL. The product began as a modular monolith and is being decomposed into microservices; it is currently in the **microservices migration phase**.

**Current product state**

| Capability | Status |
|---|---|
| Registration, login, JWT + refresh token, RBAC | ✅ |
| Address management | ✅ |
| Products, categories, inventory (seller-owned) | ✅ |
| Product listing, search/filter, pagination (MySQL-based) | ✅ |
| Cart | ✅ (inside Order Service) |
| Checkout / order creation, order history, cancellation | ✅ |
| Service discovery (Eureka), inter-service calls (OpenFeign) | ✅ |
| API Gateway | ⏭️ NEXT |
| Payment, Elasticsearch search, real-time notifications, analytics | 📋 |

**Validated business flow:** Seller Login → Create Category → Create Product → Create Inventory → Buyer Login → Create Address → Add to Cart → Get Cart → Create Order (Order Service calls Product Service) → Inventory Reservation → Order Items → Order Address Snapshot → Cart Cleared → Post-order inventory verified → Order history verified.

## 2. Problem Statement

Buyers need a trustworthy place to discover and purchase products; sellers need tools to list products and keep stock accurate; administrators need to maintain the platform. A monolithic implementation limits independent scaling, fault isolation, and team ownership as the platform grows. The product must also stay correct under concurrency (stock) and partial failure (distributed orders).

## 3. Product Vision

A complete, secure, scalable marketplace: buyers register, search, filter, add to cart, check out, pay, receive real-time order updates, and track history; sellers manage catalog and stock; admins administer users, with the platform observable, fault-tolerant, and supported by analytics over historical orders.

## 4. Goals

1. Deliver the full buyer/seller/admin business workflow.
2. Enforce secure, backend-authoritative authorization.
3. Keep inventory correct under concurrent purchases.
4. Evolve incrementally from monolith to microservices with each technology justified.
5. Preserve historical order integrity via snapshots.
6. Enable search, real-time notifications, resilience, tracing, containerization, and analytics (planned).

## 5. Non-Goals

- No public Admin registration.
- No specific payment provider is selected or assumed.
- Frontend authorization is not a security control.
- No features beyond those in this document are in scope.
- Elasticsearch will not replace MySQL as the transactional store.
- Services will not access each other's databases.

## 6. Target Users

Buyers, Sellers, Administrators.

## 7. User Personas

| Persona | Description | Needs |
|---|---|---|
| **Buyer (Bhavna)** | Online shopper | Find products fast, trust prices, track orders, cancel before fulfillment, be notified |
| **Seller (Sanjay)** | Merchant listing products | Create products, keep stock accurate, manage own catalog only |
| **Admin (Asha)** | Platform operator | Manage users, perform administrative operations |

*(Personas are illustrative archetypes for documentation.)*

## 8. User Roles

| Role | Created by | Notes |
|---|---|---|
| `BUYER` | `POST /api/auth/register` (public) | Default role |
| `SELLER` | `POST /api/auth/register/seller` (public) | Role assigned by backend |
| `ADMIN` | Bootstrapped separately | No public registration |

The frontend can never choose `ADMIN` or `SELLER` during normal buyer registration.

## 9. Buyer Requirements

Register; login; browse; search; filter; view product details; add to cart; update cart; remove items; checkout; create orders; view order history and details; cancel eligible orders; manage addresses; receive notifications (📋).

**User stories**
- As a buyer, I want to register, so that I can purchase products.
- As a buyer, I want to search products, so that I can quickly find what I need.
- As a buyer, I want to add products to my cart, so that I can purchase multiple products.
- As a buyer, I want to place an order, so that I can purchase products.
- As a buyer, I want to see my order history, so that I can track previous purchases.
- As a buyer, I want to cancel eligible orders, so that I can stop an order before fulfillment.
- As a buyer, I want real-time notifications, so that I know when my order status changes. 📋

## 10. Seller Requirements

Register; login; dashboard; create/update/delete own products; manage inventory; view own products; receive relevant notifications (📋). A seller cannot manage another seller's products or inventory unless future business rules explicitly allow it.

**User stories**
- As a seller, I want to register, so that I can sell products.
- As a seller, I want to create products, so that buyers can purchase them.
- As a seller, I want to update inventory, so that available stock remains accurate.
- As a seller, I want to see my products, so that I can manage my catalog.

## 11. Admin Requirements

Login; view users; create/manage users where permitted; update users; delete users; perform administrative operations.

- As an admin, I want to manage users, so that I can administer the platform.
- As an admin, I want to perform administrative operations, so that platform data can be maintained.

Admin bootstrap strategy is an implementation concern to be documented.

## 12. Authentication Requirements ✅

| ID | Requirement |
|---|---|
| AUTH-1 | Users can register as Buyer or Seller through separate public endpoints |
| AUTH-2 | Login returns a short-lived JWT access token and a refresh token |
| AUTH-3 | Refresh token is delivered via HttpOnly cookie, is longer-lived, and is revocable |
| AUTH-4 | `POST /api/auth/refresh` issues a new access token |
| AUTH-5 | Logout revokes the refresh token |
| AUTH-6 | Passwords stored as BCrypt hashes |
| AUTH-7 | Protected requests use `Authorization: Bearer <access-token>` |
| AUTH-8 | Public endpoints: register, register/seller, login, refresh, logout; all others require authentication |
| AUTH-9 | Frontend HTTP client refreshes on 401, retries the original request, and logs out if refresh fails |

## 13. Authorization Requirements ✅

| ID | Requirement |
|---|---|
| AZ-1 | RBAC for BUYER, SELLER, ADMIN enforced in the backend (`@PreAuthorize`) |
| AZ-2 | Unauthenticated → 401; wrong role → 403 |
| AZ-3 | Sellers may only modify their own products and inventory |
| AZ-4 | Buyer identity for cart/order operations comes from the JWT, never request input |
| AZ-5 | Frontend `ProtectedRoute`/`RoleBasedRoute` are UX only; backend independently rejects unauthorized calls |
| AZ-6 | Each service validates JWTs independently; JWT is propagated on inter-service calls |

## 14. User Management ✅

Retrieve/update own profile (`GET/PUT /api/users/me`); retrieve/delete users (`GET/DELETE /api/users/{id}`, role-restricted); multiple phone numbers per user; user status (enum). Users list is paginated.

## 15. Address Management ✅

- Address fields: line, city, state, postal code, type (`AddressType` enum e.g. HOME/WORK), default flag.
- CRUD supported; a user has many addresses.
- First address becomes default automatically; default-address rules are enforced.
- The address used for an order is stored as a **snapshot** in the order.

## 16. Product Management ✅

- Product: seller, category, name, brand, description, price (`BigDecimal`), unique SKU, status (enum).
- Create/update/delete restricted to SELLER; `sellerId` is taken from the authenticated JWT.
- Listing, retrieval, seller-specific retrieval, search, category filter, price filter, pagination.
- Bulk retrieval (`/api/products/ids`) with lightweight response (`id, sellerId, name, sku, price, status`) for Order Service.

## 17. Category Management ✅

Category has name (unique) and description. Duplicate names return **409 Conflict**.

## 18. Inventory Management ✅

- One inventory record per product (1:1).
- Fields: available quantity, reserved quantity, version, updated time.
- Operations: create, update, get, reserve, release, deduct.
- Rules: quantity validation; invalid operations and insufficient stock rejected; seller ownership enforced.
- Concurrency: optimistic locking (`@Version`).

## 19. Cart Requirements ✅

- One active cart per buyer.
- Operations: get cart, add item, update quantity, remove item, clear cart.
- Adding an item verifies product exists and is ACTIVE and uses the **current product price** from Product Service.
- Cart currently resides in Order Service; extraction to Cart & Checkout Service is 📋 PLANNED after Order flow stabilizes.

## 20. Checkout Requirements

**Current ✅:** Checkout is performed by Order Service via `POST /api/orders`.

**Planned 📋:** Cart & Checkout Service handles cart management, checkout orchestration, and cart validation.

## 21. Order Requirements ✅

| ID | Requirement |
|---|---|
| ORD-1 | Order has orderNumber, buyerId, totalAmount, status, paymentStatus, timestamps |
| ORD-2 | Order items snapshot productName, unitPrice, sellerId, plus quantity and subtotal |
| ORD-3 | Order address is a snapshot of the buyer's address at order time |
| ORD-4 | Price changes after ordering must not alter historical orders |
| ORD-5 | Status lifecycle: CREATED → CONFIRMED → PROCESSING → SHIPPED → DELIVERED |
| ORD-6 | Order Service enforces valid transitions |
| ORD-7 | Payment status maintained separately (currently `PENDING`) |
| ORD-8 | Buyers view only their own orders (history, by id, by status) |
| ORD-9 | Local persistence (order, items, address) occurs in one local transaction |

**Known limitation (documented, intentional):** inventory reservation goes through Product Service; Order Service's `@Transactional` does not include `product_db`. A failure after successful reservation may leave inconsistent state until Saga is implemented (📋).

## 22. Order Cancellation ✅

Buyers may cancel eligible orders: `CREATED → CANCELLED`, `CONFIRMED → CANCELLED`, `PROCESSING → CANCELLED`. Shipped/delivered orders are not cancellable; invalid transitions are rejected. Endpoint: `PUT /api/orders/{orderId}/cancel`. Inventory release on cancellation is part of Product Service inventory operations (release).

## 23. Payment Requirements 📋

- Payment becomes a separate microservice integrated with Order Service.
- Target order flow: Create Order → Reserve Inventory → Process Payment → Confirm Order.
- Payment failure triggers compensation: release inventory, cancel order.
- Additional payment states will be introduced with the service.
- No payment provider is specified.

## 24. Search Requirements

**Current ✅:** MySQL-based listing with search text, category and price filters, pagination.

**Planned 📋 (Elasticsearch):** full-text, fuzzy, prefix search; category and price filtering; sorting; pagination over `name, description, brand, category`; e.g. `GET /api/search/products?q=wireless`. Product changes flow Product Service → Kafka → Search Indexer → Elasticsearch. MySQL remains the source of truth.

## 25. Notifications 📋

Real-time notifications via Kafka → Notification Service → WebSocket → React for: order confirmed, shipped, delivered, cancelled; seller-related inventory notifications where required. No polling required.

## 26. Analytics 📋

Order events → Kafka → HDFS → Spark → reports: total sales; sales by day/month/category; top-selling products; top buyers; average order value; orders per day; revenue per seller; product popularity.

## 27. Functional Requirements

| ID | Requirement | Status |
|---|---|---|
| FR-1 | Buyer and seller registration; login; logout; token refresh | ✅ |
| FR-2 | RBAC | ✅ |
| FR-3 | Profile and address management | ✅ |
| FR-4 | Category CRUD with uniqueness | ✅ |
| FR-5 | Product CRUD with seller ownership | ✅ |
| FR-6 | Inventory create/update/get/reserve/release/deduct | ✅ |
| FR-7 | Product listing, filtering, pagination | ✅ |
| FR-8 | Cart operations | ✅ |
| FR-9 | Order creation with inventory reservation, snapshots, cart clearing | ✅ |
| FR-10 | Order history, by id, by status; cancellation | ✅ |
| FR-11 | Service discovery and Feign-based inter-service calls with JWT propagation | ✅ |
| FR-12 | Single entry point through API Gateway | ⏭️ |
| FR-13 | Cart & Checkout Service | 📋 |
| FR-14 | Payment Service and order confirmation | 📋 |
| FR-15 | Kafka event publication/consumption | 📋 |
| FR-16 | Saga with compensation | 📋 |
| FR-17 | Elasticsearch product search | 📋 |
| FR-18 | Real-time WebSocket notifications | 📋 |
| FR-19 | Analytics reports | 📋 |

**Frontend routes:** public `/login /register /products /products/:id`; buyer `/cart /orders /orders/:id /profile`; seller `/seller /seller/products /seller/products/add /seller/products/:id/edit /seller/inventory`; admin `/admin /admin/users /admin/users/:id`.

## 28. Non-Functional Requirements

| Category | Requirement |
|---|---|
| Security | Secure auth, RBAC, password hashing, JWT validation, refresh handling, backend authorization, input validation, secret management |
| Scalability | Independent scaling of services (Product ×N, Order ×N, Search ×N) |
| Availability | Circuit breaker, retry, bulkhead, service discovery (📋 for resilience patterns) |
| Performance | Pagination; Elasticsearch; caching where justified; asynchronous Kafka events |
| Reliability | Saga, idempotency, compensation, optimistic locking, retries, circuit breakers |
| Observability | Centralized logs, metrics, distributed tracing, correlation IDs, health checks |
| Maintainability | DTO-based APIs, database-per-service, consistent error and response models |
| Portability | Docker, no hardcoded hosts/secrets |

## 29. API Requirements

- REST conventions; nouns not verbs; correct methods and status codes.
- DTOs only; entities never exposed.
- `ApiResponse<T>` and `PageResponse<T>` wrappers; existing `ErrorResponse` / `ValidationErrorResponse` reused.
- Pagination response: `content, page, size, totalElements, totalPages, first, last`.
- Status codes: 200, 201, 204, 400, 401, 403, 404, 409, 422, 429, 500, 503.
- Validation via Jakarta Bean Validation → 400.
- After Gateway, frontend calls only the Gateway.

Key endpoints: auth (`/api/auth/*`), users (`/api/users/me`, `/api/users/{id}`), addresses (`/api/address/*`), products (`/api/products`, `/{id}`, `/seller/{sellerId}`, `/ids`), orders (`/api/orders`, `/{orderId}`, `/status/{status}`, `/{orderId}/cancel`), search (📋 `/api/search/products`).

## 30. Security Requirements

BCrypt passwords; JWT authentication; RBAC; HTTPS in production; secrets in environment/config and never in Git; backend always validates authorization; no buyer/seller ID trusted from client input; HttpOnly cookie for refresh token; rate limiting on login/search/product/order APIs with configurable limits (429) 📋; Config Server for secrets/config 📋.

## 31. Reliability Requirements

| ID | Requirement | Status |
|---|---|---|
| REL-1 | Optimistic locking prevents lost/concurrent inventory updates | ✅ |
| REL-2 | Saga coordinates order, inventory, and payment across services | 📋 |
| REL-3 | Compensation releases inventory and cancels order on payment failure | 📋 |
| REL-4 | Kafka consumers are idempotent via `eventId` / `processed_events` | 📋 |
| REL-5 | Circuit breaker, retry, time limiter, bulkhead on inter-service calls | 📋 |
| REL-6 | Graceful 503 on unavailable dependency | 📋 |

## 32. Performance Requirements

Bounded response sizes through pagination; search offloaded to Elasticsearch (📋); asynchronous processing for notifications/analytics through Kafka (📋); caching only where justified. Specific numeric latency targets are not defined and should be set after baseline measurements.

## 33. Scalability Requirements

Services deployable and scalable independently; multiple instances discoverable through Eureka; each service owns its database; event-driven decoupling for burst load; containerized deployment (📋).

## 34. Acceptance Criteria

**AC-1 Registration**
- Given a valid registration request, when `POST /api/auth/register`, then a BUYER is created. When `POST /api/auth/register/seller`, then a SELLER is created. Role cannot be supplied to obtain ADMIN.

**AC-2 Login/Refresh**
- Given valid credentials, when login, then access token returned and refresh token set as HttpOnly cookie. Given an expired access token and valid refresh token, refresh yields a new access token. After logout, the refresh token is rejected.

**AC-3 Product Creation**
- Given an authenticated Seller, when `POST /api/products`, then the product is created with `sellerId` = authenticated seller. A seller cannot create a product for another seller. A Buyer receives 403.

**AC-4 Category**
- Creating a category with an existing name returns 409.

**AC-5 Inventory**
- Given a seller's own product, seller can create/update inventory. A different seller is forbidden. Reserve moves quantity from available to reserved; release reverses it; deduct reduces reserved. Reserving more than available fails with insufficient-stock error.

**AC-6 Order Creation**
- Given an authenticated Buyer, a valid address, an active cart, and available inventory, when `POST /api/orders`, then: the order is created; inventory is reserved; cart is cleared; order items are stored; address snapshot is stored.

**AC-7 Snapshot**
- Given a placed order at ₹500 and a later product price change to ₹700, the order still shows ₹500.

**AC-8 Order Isolation**
- A buyer can list/read only their own orders; buyer ID is derived from JWT.

**AC-9 Cancellation**
- Orders in CREATED, CONFIRMED, or PROCESSING can be cancelled; others return an invalid-state error.

**AC-10 Service Discovery**
- user-service, product-service, and order-service appear `UP` in Eureka; Order Service reaches them by logical name.

**AC-11 Gateway (⏭️)**
- Frontend reaches all APIs through the Gateway; routes resolve via Eureka; unauthenticated requests to protected routes are rejected.

**AC-12 Saga (📋)**
- Given payment failure after reservation, inventory is released and the order is cancelled.

**AC-13 Idempotency (📋)**
- A duplicate event with the same `eventId` is processed once.

**AC-14 Search (📋)**
- Fuzzy/prefix queries return relevant products with filters, sorting, and pagination.

**AC-15 Notification (📋)**
- On order status change, the connected buyer receives a real-time WebSocket notification.

## 35. Error Scenarios

| Scenario | Expected behavior |
|---|---|
| Invalid credentials | 401 |
| Expired JWT | 401; client attempts refresh |
| Invalid/revoked refresh token | Rejected; user logged out |
| Unauthorized access | 401 |
| Forbidden role / other seller's resource | 403 |
| User / address / product / category / inventory / order not found | 404 |
| Duplicate SKU or category | 409 |
| Validation failure | 400 with `ValidationErrorResponse` |
| Insufficient inventory | Error via `InsufficientStockException` |
| Invalid inventory operation | `InvalidInventoryOperationException` |
| Cart empty at checkout | `CartException` |
| Product unavailable (not ACTIVE) | Rejected |
| Product price changed | Order uses validated current price; mismatch handled at checkout |
| Invalid order state transition | Rejected |
| Service unavailable | 503 (circuit breaker 📋) |
| Rate limit exceeded | 429 📋 |
| Kafka event duplication | Skipped via idempotency 📋 |
| Payment failure | Saga compensation 📋 |
| Inventory reservation failure | Order cancelled via compensation 📋 |
| Failure after reservation, before order persisted (current) | Possible inconsistency — known limitation, resolved by Saga |

## 36. Future Roadmap

| Phase | Item | Status |
|---|---|---|
| 1–9 | Domain, monolith, APIs, security, React, testing, extraction, OpenFeign, Eureka | ✅ |
| 10 | API Gateway | ⏭️ NEXT |
| 11 | Cart & Checkout extraction | 📋 |
| 12 | Payment Service | 📋 |
| 13 | Kafka | 📋 |
| 14 | Saga | 📋 |
| 15 | Resilience4j | 📋 |
| 16 | Elasticsearch | 📋 |
| 17 | WebSocket notifications | 📋 |
| 18 | Distributed tracing | 📋 |
| 19 | Docker | 📋 |
| 20 | HDFS | 📋 |
| 21 | Spark | 📋 |
| 22 | End-to-end testing | 📋 |

### Final Expected User Experience 📋

- **Buyer:** Register/Login → Browse/Search → Filter → View → Add to Cart → Checkout → Payment → Order Confirmation → Real-time Notification → Order Tracking → Order History.
- **Seller:** Register/Login → Dashboard → Create Product → Manage Inventory → View Products → Relevant notifications.
- **Admin:** Login → Dashboard → Manage Users → Administrative Operations.

### Open Items

Admin bootstrap strategy; payment provider (unselected); Saga coordination style; Config Server phase placement; numeric performance targets.
