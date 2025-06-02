# 📘 Fintechnic API Documentation

## Auth Endpoints
### Register
- **Method:** `POST`
- **URL:** `{{base_url}}/api/auth/register`
- **Request Body:**
```json
{
  "username": "testuser",
  "password": "123456",
  "email": "test@email.com",
  "phoneNumber": "0123456789"
}
```

### Login
- **Method:** `POST`
- **URL:** `{{base_url}}/api/auth/login`
- **Request Body:**
```json
{
  "username": "testuser",
  "password": "123456"
}
```

### Logout
- **Method:** `POST`
- **URL:** `{{base_url}}/api/auth/logout`
- **Request Body:**
```json
{
  "username": "testuser"
}
```
- 🔐 **Authorization required**: Yes


## Bill Endpoints
### Get User Bills
- **Method:** `GET`
- **URL:** `{{base_url}}/api/bills`
- 🔐 **Authorization required**: Yes

### Pay Bill
- **Method:** `POST`
- **URL:** `{{base_url}}/api/bills//{userId}pay`
- 🔐 **Authorization required**: Yes

### Create New Bill (Admin)
- **Method:** `POST`
- **URL:** `{{base_url}}/api/admin/new-bill`
- **Request Body:**
```json
{
  "type": "electric",
  "phoneNumber": "0123456789",
  "amount": 100000
}
```
- 🔐 **Authorization required**: Yes

## Transaction Endpoints
### Transfer Money
- **Method:** `POST`
- **URL:** `{{base_url}}/api/transaction/transfer`
- **Request Body:**
```json
{
  "phoneNumber": "0987654321",
  "amount": 50000,
  "description": "Payment"
}
```
- 🔐 **Authorization required**: Yes

### Withdraw Money
- **Method:** `POST`
- **URL:** `{{base_url}}/api/transaction/withdraw`
- **Request Body:**
```json
{
  "amount": 50000
}
```
- 🔐 **Authorization required**: Yes

### Transaction History
- **Method:** `GET`
- **URL:** `{{base_url}}/api/transaction/history`
- 🔐 **Authorization required**: Yes

### Admin Transaction History
- **Method:** `GET`
- **URL:** `{{base_url}}/api/admin/history`
- 🔐 **Authorization required**: Yes

### Filter Transactions
- **Method:** `POST`
- **URL:** `{{base_url}}/api/admin/filter`
- **Request Body:**
```json
{
  "sortBy": "createdAt",
  "sortDirection": "DESC",
  "page": 0,
  "size": 10
}
```
- 🔐 **Authorization required**: Yes

## QR Code Endpoints
### Get My QR Code
- **Method:** `GET`
- **URL:** `{{base_url}}/api/qrcode/myqrcode`
- 🔐 **Authorization required**: Yes

### Scan QR Code
- **Method:** `POST`
- **URL:** `{{base_url}}/api/qrcode/scanner`
- **Request Body:**
```json
{
  "encryptedData": "encrypted_string_here"
}
```
- 🔐 **Authorization required**: Yes

## Wallet Endpoints
### Search Wallet
- **Method:** `POST`
- **URL:** `{{base_url}}/api/admin/transaction/search-wallet`
- **Request Body:**
```json
{
  "username": "agentuser"
}
```
- 🔐 **Authorization required**: Yes

### Top Up Agent Wallet
- **Method:** `POST`
- **URL:** `{{base_url}}/api/admin/transaction/top-up`
- **Request Body:**
```json
{
  "phoneNumber": "0123456789",
  "amount": 200000,
  "description": "Top up"
}
```
- 🔐 **Authorization required**: Yes

### Wallet Summary
- **Method:** `GET`
- **URL:** `{{base_url}}/api/admin/wallet/summary`
- 🔐 **Authorization required**: Yes
  

## User Management Endpoints
### Search And List Users
- **Method:** `POST`
- **URL:** `{{base_url}}/api/admin/user`
- **Request Body:**
```json
{
  "username": "testuser",
  "email": "test@gmail.com"
}
```
- 🔐 **Authorization required**: Yes



### Detail User
- **Method:** `GET`
- **URL:** `{{base_url}}/api/admin/user/{userId}`
- 🔐 **Authorization required**: Yes

### Reset Password
- **Method:** `POST`
- **URL:** `{{base_url}}/api/admin/user/{userId}/reset-password`
- **Request Body:**
```json
{
  "newPassword": "123abc"
}
```
- 🔐 **Authorization required**: Yes

### Update Role
- **Method:** `POST`
- **URL:** `{{base_url}}/api/admin/user/{userId}/update-role`
- **Request Body:**
```json
{
  "newRole": "ADMIN"
}
```
- 🔐 **Authorization required**: Yes

### Unlock User
- **Method:** `POST`
- **URL:** `{{base_url}}/api/admin/user/{userId}/unlock`
- **Request Body:**
```json
{
  "username": "testuser"
}
```
- 🔐 **Authorization required**: Yes

## Stats Endpoint
### System Stats Summary
- **Method:** `GET`
- **URL:** `{{base_url}}/api/admin/system-stats`
- 🔐 **Authorization required**: Yes


