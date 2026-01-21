# Farm2Market – Spring Boot Backend Application

## 📌 Overview
Farm2Market is an online backend platform built using Spring Boot that connects farmers, equipment owners, and traders on a single digital marketplace. The application allows farmers to sell agricultural products online, rent farming equipment, and directly connect with bulk buyers (traders). It also enables equipment owners to onboard and rent farming equipment such as tractors and drones to nearby farmers.

The platform supports role-based access for Farmers, Equipment Owners, and Traders with complete product, equipment, booking, and order management workflows.

---

## 🛠 Tech Stack
- Java 17  
- Spring Boot  
- Spring MVC  
- Spring Data JPA  
- Hibernate  
- MySQL  
- RESTful APIs  
- Maven  
- Postman (API Testing)  

---

## 🚀 Core Features & Use Cases

### 🔹 User Management
- Role-based user registration for:
  - Farmer  
  - Equipment Owner  
  - Trader  
- Secure login and logout functionality  
- User profile information includes username, mobile number, state, and pincode  

---

### 🔹 Farmer Module
- View and search available farming equipment by city  
- Book equipment based on availability and return after usage  
- Add, edit, and delete agricultural products (Rice, Millets, Pulses, Vegetables, etc.)  
- Manage product listings for traders to place orders  

---

### 🔹 Equipment Owner Module
- Add new farming equipment (Tractors, Drones, Sprayers, etc.)  
- Edit and delete equipment details  
- Manage availability of listed equipment for rental  

---

### 🔹 Trader Module
- Search farmer products based on city and location  
- Place bulk orders directly with farmers  
- Track purchased goods  

---

## 🔹 Functional Highlights
- Role-based access control for different user types  
- Equipment availability and booking workflow  
- Product listing and ordering system  
- City-based search and filtering  
- RESTful API design for all modules  

---

## ▶️ How to Run the Application
1. Clone the repository  
2. Configure MySQL database in `application.properties`  
3. Run the application using:  
