# Inventory & Purchase Order Management System

A backend REST API built with **Java, Spring Boot, Spring Data JPA/Hibernate, MySQL, and Spring Security with JWT** for managing products, suppliers, purchase orders, and inventory operations.

The project demonstrates real-world backend concepts including authentication, role-based authorization, transactional business operations, database relationships, validation, exception handling, and inventory tracking.

---

## 🚀 Features

### Authentication & Authorization
- JWT-based authentication
- Secure password storage using BCrypt
- Role-based authorization
- Three roles:
  - `ADMIN`
  - `PURCHASE_MANAGER`
  - `WAREHOUSE_MANAGER`
- Admin-only user management
- Public registration creates `PURCHASE_MANAGER` users only

### Product Management
- Create, update, retrieve and delete products
- Unique SKU
- Product status management
- Category association
- Low-stock product detection
- Database indexes for SKU and product name

### Supplier Management
- Supplier CRUD operations
- Unique supplier email
- Supplier status management

### Purchase Order Management
- Create purchase orders
- Supplier association
- Multiple products per purchase order
- Automatic subtotal and total calculation
- Purchase order lifecycle:

```text
DRAFT → APPROVED → RECEIVED
```

- Purchase order cancellation
- Validation of supplier and product status

### Inventory Management
- Automatic stock update when a purchase order is received
- Inventory transaction history
- Purchase receipt transactions linked to purchase orders
- Low-stock detection based on reorder level

### Exception Handling
Centralized exception handling using `@RestControllerAdvice`.

Supported responses include:

- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found`
- `409 Conflict`
- `500 Internal Server Error`

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 17+ | Programming language |
| Spring Boot | Backend framework |
| Spring Web | REST APIs |
| Spring Data JPA | Data access |
| Hibernate | ORM |
| MySQL | Relational database |
| Spring Security | Authentication & authorization |
| JWT | Stateless authentication |
| Maven | Build & dependency management |
| Lombok | Boilerplate reduction |
| Bean Validation | Request validation |
| Postman | API testing |

---

## 🏗️ Architecture

The application follows a layered architecture:

```text
Client / Postman
       ↓
Controller
       ↓
Service
       ↓
Repository
       ↓
Hibernate / JPA
       ↓
MySQL
```

### Controller Layer
Responsible for:
- HTTP requests
- Request validation
- HTTP responses

### Service Layer
Contains:
- Business logic
- Validation
- Transaction management
- Entity-to-response mapping

### Repository Layer
Responsible for:
- Database operations
- Spring Data JPA queries

### Entity Layer
Represents the database model and relationships.

---

## 🗃️ Database Relationships

```text
Category
   │
   └─────── 1 : N ─────── Product
                              │
                              ├────── 1 : N ─────── PurchaseOrderItem
                              │
                              └────── 1 : N ─────── InventoryTransaction

Supplier
   │
   └─────── 1 : N ─────── PurchaseOrder
                              │
                              └────── 1 : N ─────── PurchaseOrderItem
```

Main entities:

- `User`
- `Category`
- `Product`
- `Supplier`
- `PurchaseOrder`
- `PurchaseOrderItem`
- `InventoryTransaction`

---

## 🔐 Security Flow

```text
User
 ↓
Login
 ↓
AuthenticationManager
 ↓
UserDetailsService
 ↓
BCrypt password verification
 ↓
JWT generated
 ↓
Client stores JWT
 ↓
Request with Authorization: Bearer <token>
 ↓
JwtAuthenticationFilter
 ↓
JWT validation
 ↓
SecurityContext
 ↓
Controller
```

JWT signing secrets are not stored in source code.

The application reads the secret from an environment variable:

```properties
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=3600000
```

Set the environment variable before starting the application:

```text
JWT_SECRET=your-secure-secret
```

**Never commit the actual JWT secret to GitHub.**

---

## 👥 Roles & Permissions

| Operation | ADMIN | PURCHASE_MANAGER | WAREHOUSE_MANAGER |
|---|:---:|:---:|:---:|
| Login | ✅ | ✅ | ✅ |
| Product operations | ✅ | ✅ | ✅ |
| Supplier operations | ✅ | ✅ | ❌ |
| Purchase orders | ✅ | ✅ | ❌ |
| User management | ✅ | ❌ | ❌ |

---

## 📦 Purchase Order Lifecycle

A purchase order starts in `DRAFT`.

```text
DRAFT
  │
  ├── APPROVE ──→ APPROVED
  │                   │
  │                   └── RECEIVE ──→ RECEIVED
  │
  └── CANCEL ──→ CANCELLED
```

Important business rules:

- Only `DRAFT` orders can be approved.
- Only `APPROVED` orders can be received.
- A `RECEIVED` order cannot be cancelled.
- A cancelled order cannot be received.
- Supplier must be active.
- Products must be active.
- Product prices are read from the database instead of being trusted from the client.

---

## 💰 Purchase Order Calculation

For every purchase order item:

```text
subtotal = quantity × product unit price
```

The purchase order total is:

```text
totalAmount = sum(all item subtotals)
```

`BigDecimal` is used for monetary calculations.

The purchase order item stores its own `unitPrice` so historical purchase orders retain the original price even if the product price changes later.

---

## 📊 Inventory Management

When an approved purchase order is received:

```text
Old Stock
    +
Received Quantity
    ↓
New Stock
```

For every received item, an `InventoryTransaction` is created:

```text
Transaction Type: PURCHASE_RECEIVED
Reference Type: PURCHASE_ORDER
Reference ID: Purchase Order ID
Quantity: Received Quantity
```

The entire receive operation runs inside a transaction.

If the operation fails, the database changes are rolled back.

---

## ⚠️ Low Stock Detection

A product is considered low-stock when:

```text
currentStock <= reorderLevel
```

Only active products are returned by the low-stock query.

---

## 🔑 Authentication Endpoints

### Register

```http
POST /api/auth/register
```

Example:

```json
{
  "email": "manager@example.com",
  "password": "Manager@123"
}
```

Public registration creates a `PURCHASE_MANAGER`.

Users cannot select `ADMIN` through public registration.

---

### Login

```http
POST /api/auth/login
```

Example:

```json
{
  "email": "manager@example.com",
  "password": "Manager@123"
}
```

Returns a JWT token.

Use the token for protected APIs:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

## 👤 User Management

Admin-only APIs:

```http
POST /api/users
GET /api/users
PATCH /api/users/{id}/disable
```

Example:

```json
{
  "email": "warehouse@example.com",
  "password": "Warehouse@123",
  "role": "WAREHOUSE_MANAGER"
}
```

Only an `ADMIN` can create or manage users through these APIs.

---

## 📦 Main API Endpoints

### Categories

```http
POST   /api/categories
GET    /api/categories
```

### Products

```http
POST   /api/products
GET    /api/products
GET    /api/products/{id}
GET    /api/products/low-stock
PUT    /api/products/{id}
DELETE /api/products/{id}
```

### Suppliers

```http
POST   /api/suppliers
GET    /api/suppliers
GET    /api/suppliers/{id}
PUT    /api/suppliers/{id}
DELETE /api/suppliers/{id}
```

### Purchase Orders

```http
POST /api/purchase-orders
GET  /api/purchase-orders
GET  /api/purchase-orders/{id}
PUT  /api/purchase-orders/{id}/approve
PUT  /api/purchase-orders/{id}/receive
PUT  /api/purchase-orders/{id}/cancel
```

### Inventory

Inventory transactions are generated automatically during inventory-changing operations such as purchase order receiving.

---

## ⚙️ Configuration

Create the MySQL database:

```sql
CREATE DATABASE inventory_management;
```

Configure the database connection:

```properties
spring.application.name=inventory-management

spring.datasource.url=jdbc:mysql://localhost:3306/inventory_management
spring.datasource.username=root
spring.datasource.password=YOUR_DATABASE_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=3600000
```

Set:

```text
JWT_SECRET=your-secure-secret
```

before starting the application.

---

## ▶️ Running the Application

### 1. Clone the repository

```bash
git clone <your-github-repository-url>
cd inventory-management
```

### 2. Create the MySQL database

```sql
CREATE DATABASE inventory_management;
```

### 3. Configure database credentials

Update `application.properties`.

### 4. Set JWT secret

Configure:

```text
JWT_SECRET
```

as an environment variable.

### 5. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

Or run the main Spring Boot application from IntelliJ IDEA.

---

## 🧪 Testing Checklist

The application should be tested for:

### Authentication
- [ ] Successful registration
- [ ] Duplicate email
- [ ] Successful login
- [ ] Invalid password
- [ ] Invalid JWT
- [ ] Missing JWT

### Authorization
- [ ] ADMIN can manage users
- [ ] PURCHASE_MANAGER cannot manage users
- [ ] WAREHOUSE_MANAGER cannot access admin-only APIs
- [ ] Protected endpoints reject unauthenticated requests

### Products
- [ ] Create product
- [ ] Duplicate SKU
- [ ] Update product
- [ ] Delete product
- [ ] Low-stock products

### Suppliers
- [ ] Create supplier
- [ ] Duplicate email
- [ ] Update supplier
- [ ] Delete supplier

### Purchase Orders
- [ ] Create PO
- [ ] Approve PO
- [ ] Receive PO
- [ ] Cancel PO
- [ ] Invalid status transitions
- [ ] Inactive supplier
- [ ] Inactive product
- [ ] Multiple products in one PO

### Inventory
- [ ] Stock increases after PO receipt
- [ ] Inventory transaction is created
- [ ] Correct purchase order reference is stored
- [ ] Transaction rollback works when an operation fails

### Validation
- [ ] Invalid email
- [ ] Empty required fields
- [ ] Negative stock
- [ ] Invalid quantity
- [ ] Invalid price
- [ ] Missing supplier/product IDs

---

## 🎯 Important Backend Concepts Demonstrated

This project demonstrates practical knowledge of:

- REST API design
- Layered architecture
- Dependency Injection
- Spring Data JPA
- Hibernate ORM
- Entity relationships
- Lazy loading
- Database indexing
- DTO pattern
- Bean Validation
- Global exception handling
- `@Transactional`
- JPA dirty checking
- BCrypt password hashing
- JWT authentication
- Spring Security
- Role-based authorization
- Enum-based state management
- Database transactions
- Inventory audit/history tracking
- Monetary calculations using `BigDecimal`

---

## 📌 Key Design Decisions

### Why DTOs?

Entities are not directly exposed through the REST API.

DTOs provide:

- Controlled API responses
- Request validation
- Separation between API and database models
- Reduced accidental exposure of internal fields

### Why `BigDecimal`?

Financial values such as product prices and purchase order totals use `BigDecimal` instead of floating-point types to avoid precision problems.

### Why store price in `PurchaseOrderItem`?

Product prices can change over time.

The purchase order must preserve the price that was used when the order was created.

### Why `@Transactional` for receiving a PO?

Receiving changes multiple pieces of data:

```text
Purchase Order
      +
Product Stock
      +
Inventory Transaction
```

These operations should succeed or fail together.

### Why inventory transactions?

Instead of only storing the current stock, the application also maintains a history of inventory changes.

This provides an audit trail for stock movement.


## 👨‍💻 Project Goal

The goal of this project is to demonstrate the ability to design and implement a realistic backend application using the Spring ecosystem, with emphasis on:

**business logic + database design + security + transactional consistency + clean API design.**
