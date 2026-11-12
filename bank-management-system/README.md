# Mini Bank Management System

A small bank administration web app built with Java Servlets, JSP, JDBC and MySQL (MVC, no frameworks).

**Features:** admin login/logout (session + filter), dashboard (total customers, total balance, recent transactions), add/search/view customers with auto-generated account numbers, deposits and withdrawals with balance checks, transaction history (latest first).

## 1. Requirements

| Tool | Version |
|------|---------|
| JDK | 17 |
| Apache Maven | 3.8+ |
| MySQL Server | 8.0.16+ (needed for CHECK constraints) |
| Apache Tomcat | **10.x** (Jakarta namespace; Tomcat 9 will not work) |

## 2. Setup

### Step 1 - Create the database
```bash
mysql -u root -p < database/bank_db.sql
```
This creates `bank_db`, the `admin`, `customers` and `transactions` tables, and the default admin.

### Step 2 - Configure the connection
Edit `src/main/resources/db.properties` and set your MySQL user and password:
```properties
db.url=jdbc:mysql://localhost:3306/bank_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=root
```

### Step 3 - Build the WAR
```bash
mvn clean package
```
Output: `target/bank.war`

### Step 4 - Deploy on Tomcat 10
1. Copy `target/bank.war` into `<TOMCAT_HOME>/webapps/`
2. Start Tomcat: `<TOMCAT_HOME>/bin/startup.sh` (Linux/macOS) or `startup.bat` (Windows)
3. Open **http://localhost:8080/bank/**

### Step 5 - Sign in
- Username: `admin`
- Password: `admin123`

## 3. Using the app

1. **Add customer** - fill in name, phone, email and address. An account number like `AC1234567890` is generated and shown after saving. The opening balance is 0.
2. **Customers** - view all customers, or search by name or account number.
3. **Deposit / Withdraw** - choose an account, choose the type, enter the amount. Withdrawals larger than the balance are rejected.
4. **Transactions** - every successful deposit and withdrawal, newest first.

## 4. Project structure

```
bank-management-system/
├── pom.xml
├── database/bank_db.sql                  database, tables, default admin
└── src/main/
    ├── java/com/bank/
    │   ├── controller/   LoginServlet, LogoutServlet, DashboardServlet,
    │   │                 CustomerServlet, TransactionServlet, HistoryServlet
    │   ├── dao/          AdminDAO, CustomerDAO, TransactionDAO
    │   ├── model/        Admin, Customer, Transaction
    │   ├── filter/       AuthFilter
    │   └── util/         DBUtil, PasswordUtil, ValidationUtil, BankException
    ├── resources/db.properties
    └── webapp/
        ├── index.jsp, css/style.css, js/app.js
        └── WEB-INF/ web.xml, views/*.jsp, views/fragments/{header,footer}.jsp
```

URL map: `/login`, `/logout`, `/dashboard`, `/customers`, `/customers/add`, `/transaction`, `/history`.
JSP views live under `WEB-INF/views`, so they can only be reached through a servlet (and therefore through the login filter).

## 5. How the key requirements are met

- **PreparedStatements** are used for every query.
- **Password hashing:** PBKDF2-HMAC-SHA256 with a random salt, stored as `pbkdf2$iterations$salt$hash`.
- **Authentication:** `AuthFilter` guards every URL except `/login`, `/css/*` and `/js/*`. The session id is renewed at login.
- **Transactions:** `TransactionDAO.perform()` runs in one JDBC transaction: it locks the customer row (`SELECT ... FOR UPDATE`), checks the balance, updates it, inserts the transaction row, then commits. Any failure rolls everything back.
- **BigDecimal** is used for all money values (`DECIMAL(15,2)` in MySQL).
- **Validation:** all inputs are validated on the server (`ValidationUtil`); the HTML attributes and `app.js` are only conveniences.
- **Errors:** database failures are logged to the Tomcat log and shown as friendly messages; unexpected errors go to `error.jsp`.

## 6. Customising

- **Currency symbol:** change the `currencySymbol` context-param in `WEB-INF/web.xml`.
- **New admin password:** run the command below, then `UPDATE admin SET password_hash = '<output>' WHERE username = 'admin';`
  ```bash
  mvn compile
  java -cp target/classes com.bank.util.PasswordUtil myNewPassword
  ```

## 7. Troubleshooting

| Problem | Fix |
|---------|-----|
| Login page says it cannot reach the database | Start MySQL; check `db.properties`; make sure you ran the SQL script. |
| `ClassNotFoundException: javax.servlet...` / 404 on every page | You are on Tomcat 9. Use Tomcat 10.x. |
| `Access denied for user` | Wrong `db.user` / `db.password`. Rebuild (`mvn clean package`) and redeploy after editing. |
| Port 8080 already in use | Change the connector port in `<TOMCAT_HOME>/conf/server.xml`. |
| Changes not showing | Delete `<TOMCAT_HOME>/webapps/bank` and `bank.war`, then copy the new WAR. |

## 8. Scope notes

This is a learning project. It has one admin role, no CSRF tokens, no pagination (history shows the latest 500 rows) and no edit/delete for customers. Add those before any real-world use.
