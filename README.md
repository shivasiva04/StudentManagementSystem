# 🎓 Student Management System

A fully functional, full-stack **Student Management System** built using **Spring Boot**, **PostgreSQL**, **Thymeleaf**, and **Tailwind CSS**. This system was developed as part of my internship project and includes secure login, role-based dashboards, course management, attendance tracking, and more.

---

## 🚀 Features

- 🔐 **Role-based Authentication** (Admin / Staff / Student)
- 📧 **Email OTP Verification** during signup
- 📊 **Personalized Dashboards** for each role
- 📚 **Course Enrollment** and Progress Tracking
- 🗓️ **Daily Attendance** with Date Filtering
- 💰 **Fee Allotment** & **Payment Status Tracking** *(UI only)*
- 📅 **Upcoming Deadlines / Events** Section
- 🎨 **Modern UI** with 3D-style design using Tailwind CSS & Chart.js
- 🛠️ **Admin Panel** to manage Users, Courses, Attendance, and Reports

---

## 🛠️ Tech Stack

| Layer      | Technology          |
|------------|---------------------|
| Backend    | ☕ Java + Spring Boot |
| Security   | 🔐 Spring Security   |
| Database   | 🛢️ PostgreSQL         |
| Frontend   | 🖼️ Thymeleaf + 🎨 Tailwind CSS |
| Charts     | 📈 Chart.js          |
| Email OTP  | 📬 JavaMailSender    |

---

## 📸 Screenshots

*(Include screenshots or a demo GIF of the UI here if available)*

---

## 📂 Project Structure

student-management-system/
│
├── src/
│ ├── main/
│ │ ├── java/
│ │ │ └── com.shivasiva.student.management.system
│ │ │ ├── controller/
│ │ │ ├── model/
│ │ │ ├── repository/
│ │ │ ├── security/
│ │ │ ├── service/
│ │ └── resources/
│ │ ├── templates/ (Thymeleaf files)
│ │ ├── static/ (Tailwind, JS, images)
│ │ └── application.properties
└── pom.xml

yaml

## 📦 Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/student-management-system.git
   cd student-management-system
Configure the database
Update your application.properties with your PostgreSQL DB details:

properties:

spring.datasource.url=jdbc:postgresql://localhost:5432/your_db_name
spring.datasource.username=your_username
spring.datasource.password=your_password
Run the application

You can run it from IntelliJ IDEA or use:
bash
./mvnw spring-boot:run
Access the app

Visit: http://localhost:8080

📧 Email OTP Setup
This project uses JavaMailSender to send OTPs to users during signup.

Configure the following in application.properties:

properties:

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

🙋‍♂️ Author:

SIVA N
B.Tech CSE (Cybersecurity) | Java Intern
📍 Kalasalingam Academy of Research and Education
🌐 [LinkedIn](https://www.linkedin.com/in/shiva-siva04/)
📫 Contact: 004shivasiva@gmail.com

📃 License
This project is licensed for educational and demo purposes. Feel free to explore, learn, and extend it!
