# 💳 RevPay – Console Financial Application

## 📌 Project Overview
RevPay is a Java-based console financial application that allows users to manage digital payments securely.  
The system supports both personal and business users, enabling money transfers, card management, invoice handling, and basic financial operations through a console interface.

This project demonstrates concepts of Core Java, JDBC, OOPS, Exception Handling, Database Connectivity, and Security (Encryption).

---

## 🚀 Features

### 👤 User Module
- User Registration & Login
- Role-based Access (Personal / Business)
- Secure Password Handling
- Session Management

### 💰 Transaction Module
- Send Money
- Request Money
- View Transaction History
- Balance Check
- Secure Fund Transfer

### 💳 Card Management
- Add Card
- View Cards
- Delete Card
- AES Encryption for Card Details

### 🧾 Invoice Module
- Generate Invoice
- View Invoice Details
- Track Payment Status
- Business User Invoice Management

### 🏢 Business Tools
- Business Account Creation
- Invoice Tracking
- Loan Application (Future Scope)
- Business Transaction Reports

---

## 🛠️ Technologies Used
- Java (Core Java)
- JDBC
- MySQL Database
- AES Encryption
- Eclipse IDE
- Git & GitHub

---

## 🗂️ Project Structure


revpay/
│
├── dao/
│ ├── UserDao.java
│ ├── PaymentMethodDao.java
│ └── TransactionDao.java
│
├── model/
│ ├── User.java
│ ├── Transaction.java
│ └── PaymentMethod.java
│
├── service/
│ ├── UserService.java
│ ├── TransactionService.java
│
├── util/
│ ├── DBConnection.java
│ ├── EncryptionUtil.java
│
└── Main.java


---

## 🔐 Security Features
- AES Encryption for sensitive data
- Secure password storage
- Input validation
- Exception handling for invalid operations

---

## ⚙️ How to Run the Project

1. Clone the repository

git clone https://github.com/your-username/RevPay-Console-Financial-Application.git


2. Import project into Eclipse

3. Configure MySQL database
- Create database `revpay`
- Update DB credentials in `DBConnection.java`

4. Run `Main.java`

---

## 🧪 Testing
- Exception Handling Tested
- Database Operations Tested
- Console Flow Testing
- Manual Unit Testing

---

## 📈 Future Enhancements
- Spring Boot Integration
- REST API Development
- Web UI Version
- Payment Gateway Integration
- Business Loan Module
- Report Generation

---

## 👩‍💻 Developed By
Kashish Kallurwar  

---

## 📌 Learning Outcomes
- Practical implementation of OOPS
- Database connectivity using JDBC
- Secure data handling with encryption
- Real-world financial application structure
- Exception handling and validation techniques

---

⭐ If you like this project, give it a star on GitHub!
