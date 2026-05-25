# SplitWiseX 💸

A Java-based expense sharing and balance tracking application built using Core Java, JDBC, MySQL, and Swing GUI.

SplitWiseX helps groups manage shared expenses, calculate balances automatically, and keep track of expense history in an organized way.

---

## 🚀 Features

* 👤 User Management

  * Create and manage users

* 👥 Group Management

  * Create groups
  * Add members to groups

* 💰 Expense Tracking

  * Add expenses with:

    * payer
    * participants
    * amount
    * description
    * currency

* ⚖️ Smart Balance Calculation

  * Automatically calculates:

    * who owes whom
    * how much should be settled

* 📜 Expense History

  * View all expenses for a group

* 🖥️ Swing GUI Support

  * Basic desktop interface using Java Swing

---

# 🛠️ Tech Stack

| Technology | Usage                  |
| ---------- | ---------------------- |
| Java       | Core application logic |
| JDBC       | Database connectivity  |
| MySQL      | Relational database    |
| Swing      | Desktop GUI            |
| Gradle     | Build automation       |

---

# 🧠 Core Concepts Used

* Object-Oriented Programming (OOP)
* DAO Design Pattern
* JDBC Connectivity
* Exception Handling
* SQL Joins
* Collections Framework
* Layered Architecture

---

# 🗄️ Database Design

The application uses multiple relational tables:

* `users`
* `groups`
* `expenses`
* `expense_participants`
* `group_members`

Foreign key relationships are used to maintain data consistency.

---

# 📂 Project Structure

```text
src/main/java
│
├── app
│   └── Main.java
│
├── db
│   ├── DBConnection.java
│   ├── ExpenseDAO.java
│   ├── GroupDAO.java
│   └── UserDAO.java
│
├── exceptions
│   ├── ConsoleInputException.java
│   ├── GroupException.java
│   ├── GroupNotFoundException.java
│   └── UserException.java
│
├── gui
│   └── MainFrame.java
│
├── logic
│   ├── ExpenseManagement.java
│   └── SplitUtil.java
│
├── models
│   ├── CurrencyCode.java
│   ├── Expense.java
│   ├── Group.java
│   ├── Money.java
│   └── User.java
│
└── ui
    ├── ConsoleUI.java
    └── MenuController.java
```

---

# ⚙️ Installation & Setup

## 1️⃣ Clone Repository

```bash
git clone https://github.com/Praharsha08/SplitWiseX.git
```

---

## 2️⃣ Setup MySQL Database

Run the SQL script located inside:

```text
/sql/schema.sql
```

This creates all required tables.

---

## 3️⃣ Configure Database Credentials

Update the database configuration inside:

```text
src/main/resources/config.properties
```

Add:

* database URL
* username
* password

---

## 4️⃣ Run Application

```bash
./gradlew run
```

---

# 📸 Application Functionalities

* Create User
* Create Group
* Add Members
* Add Expenses
* Show Group Balances
* View Expense History

---

# 🔮 Future Improvements

* Spring Boot REST API
* React Frontend
* Authentication System
* Cloud Deployment
* Expense Settlement Feature
* Real-Time Currency Conversion

---

# 👨‍💻 Author

**Praharsha**

