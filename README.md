# Foodies API + React Apps

Simple food ordering project with:

- `Customer App`: browse products, add to cart, checkout with delivery address + COD
- `Admin App`: add products (with image upload to AWS S3) and view all orders

## Tech Stack

- Backend: Spring Boot, MongoDB, AWS S3
- Frontend: React + Vite

## Project Structure

- `src/main/java/...` -> Spring Boot backend
- `src/main/resources/application.properties` -> backend config
- `frontend/` -> React frontend

## Features

- Product catalog
- Cart management (add/update/remove)
- Checkout with delivery details
- COD payment flow
- Order history for customer
- All orders dashboard for admin
- Product image upload to S3

## Run Locally

### 1) Backend

Set environment variables:

- `AWS_ACCESS_KEY`
- `AWS_SECRET_KEY`

Make sure MongoDB is running locally, then:

```bash
./mvnw spring-boot:run
```

Backend runs on `http://localhost:8080`

### 2) Frontend

```bash
cd frontend
npm install
npm run dev -- --host 127.0.0.1 --port 5174
```

Frontend runs on `http://127.0.0.1:5174`

## App Routes

- Customer app: `http://127.0.0.1:5174/#/customer`
- Admin app: `http://127.0.0.1:5174/#/admin`

## Important Config

Backend config file: `src/main/resources/application.properties`

Key values:

- `spring.data.mongodb.uri`
- `aws.region`
- `aws.s3.bucketname`
- `spring.servlet.multipart.max-file-size`
- `spring.servlet.multipart.max-request-size`

## Main APIs

- `GET /api/products`
- `POST /api/products`
- `POST /api/files/upload`
- `GET /api/cart`
- `POST /api/cart/items`
- `PUT /api/cart/items/{productId}`
- `DELETE /api/cart/items/{productId}`
- `POST /api/cart/checkout`
- `GET /api/orders` (customer)
- `GET /api/admin/orders` (admin)

## Deployment (High Level)

1. Deploy backend (Render/Railway/etc.)
2. Use MongoDB Atlas URI for production
3. Set AWS env vars on backend
4. Deploy frontend (Vercel/Netlify)
5. Set `VITE_API_BASE_URL` to backend URL
6. Update backend CORS to allow frontend domain

## Notes

- If image upload fails, backend now returns AWS error details in the response.
- If image URL does not open, check S3 bucket/object access policy.
