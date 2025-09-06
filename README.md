🎯 Features
👤 User Functionalities

Register and create an account with a unique username and secure password.

Log in securely to their account.

View current account balance.

Send money to other users (only if sufficient balance is available).

Request money from other users.

View history of transactions (sent and received).

Edit user profile (e.g., update password).

Log out of the application.

👨‍💻 Admin Functionalities

Secure login using admin credentials.

View all registered users with:

Username

Current balance

Transaction history

Add, edit, delete, or suspend user accounts.

Suspended accounts cannot perform transactions until reactivated.

View all transactions in the system (sender, recipient, amount, date, status).

Adjust a user’s balance manually if needed.

✅ Minimum Requirements Implemented

Authentication for both users and admins.

Money transactions are validated (cannot send more than available balance).

Immediate balance update after each transaction.

Request and approval system for money requests.

Basic error handling (e.g., invalid username, insufficient funds, requesting money from self).

📂 Data Management

Data is persisted in a MySQL database (wallet_schema).

The system ensures that users’ data, balances, and transactions remain stored even after program restarts.

💻 Tech Stack

Language: Java

Database: MySQL

JDBC: For database connectivity

Tools/IDE: IntelliJ IDEA / Eclipse

Version Control: Git & GitHub
