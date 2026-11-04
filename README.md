# Crypto Trading Platform 🚀

A final-year B.Tech project built with **Spring Boot** (backend) and **React.js** (frontend).  
This platform allows users to trade cryptocurrencies, view live charts, and analyze market trends.

---

## 🏗️ Tech Stack
- **Frontend**: React.js + TradingView API (for live charts)
- **Backend**: Spring Boot (Java)
- **Database**: MySQL
- **Deployment**: Docker (containerization), GitHub (version control)
- **Authentication**: JWT-based login system
- **Other Tools**: Maven, application.properties with `.env` for secrets

---

## ⚙️ Configuration
Sensitive configs are stored in `.env` file (not pushed to GitHub).  
Example `.env`:



---

## 📂 Workflow

### 1. Frontend (React.js + TradingView)
- User login/signup (JWT auth).
- Dashboard with live crypto charts.
- Order placement UI (Buy/Sell).
- REST API calls → Backend.

### 2. Backend (Spring Boot)
- **Auth Controller** → Handles JWT authentication.
- **Trading Controller** → Processes buy/sell orders.
- **Market Controller** → Fetches live crypto prices (via TradingView API).
- **Notification Controller** → Sends confirmations (email/SMS if configured).

### 3. Database (MySQL)
- Stores user accounts, trades, balances, and transaction history.

### 4. Deployment
- **Docker** → Containerize backend + frontend.
- **GitHub** → Version control and CI/CD pipeline.
- **Local/Cloud** → Run services locally or deploy to cloud (AWS/Heroku).

---

## 🔄 Workflow Diagram (Textual)


---

## 🚀 Flow Example
1. User logs in → React frontend → Spring Boot Auth Controller → JWT issued.  
2. User places **Buy BTC** order → Trading Controller validates & stores in MySQL.  
3. Market Controller fetches live price → updates frontend dashboard.  
4. User sees updated balance + chart refresh.  

---

## 📌 Features
- 🔐 Secure login & JWT authentication
- 📊 Real-time crypto charts (TradingView)
- 💱 Buy/Sell crypto trades
- 📈 Transaction history stored in MySQL
- ☁️ Dockerized deployment

---

## 🤝 Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

---

## 📜 License
This project is licensed under the MIT License.

