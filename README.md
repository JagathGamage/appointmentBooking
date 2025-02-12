# 🏥 Appointment Booking App

An appointment booking system built with **Spring Boot (Backend)** and **React (Frontend)**. This app allows users to view available slots, book appointments, and manage their appointments.

---

## 📌 Features

- **User Authentication**: Users can log in to book and manage appointments.
- **View Available Slots**: Users can browse and select from available appointment slots.
- **Book Appointments**: Users can book an appointment by providing their name and email.
- **Manage Appointments**: Users can view and cancel their scheduled appointments.
- **Admin Dashboard**: Admins can add, edit, and delete appointment slots.
- **Secure API**: Uses **JWT authentication** for secure access.
- **Responsive UI**: Built with **Material-UI** for a modern and user-friendly experience.

---

## 🛠️ Tech Stack

### **Frontend**
- **React.js** (JavaScript framework)
- **Material-UI** (UI Components)
- **Axios** (API requests)
- **React Router** (Navigation)

### **Backend**
- **Spring Boot** (Java Framework)
- **Spring Security** (Authentication & JWT)
- **Spring Data JPA** (Database access)
- **H2 / MySQL** (Database)
- **Lombok** (Reduces boilerplate code)

---

## 🚀 Setup Instructions

### **Backend (Spring Boot) Setup**
1. **Clone the repository:**
   ```sh
   git clone https://github.com/your-repo/appointment-booking.git
   cd appointment-booking/backend
Set up the database:

If using H2 Database (default): No extra setup needed.
If using MySQL:
Create a database named appointments
Update application.properties:
properties
Copy
Edit
spring.datasource.url=jdbc:mysql://localhost:3306/appointments
spring.datasource.username=root
spring.datasource.password=yourpassword
Run the backend server:

sh
Copy
Edit
mvn spring-boot:run
The backend API will be available at: http://localhost:8080

Frontend (React) Setup
Navigate to the frontend directory:

sh
Copy
Edit
cd ../frontend
Install dependencies:

sh
Copy
Edit
npm install
Start the React app:

sh
Copy
Edit
npm start
The frontend will be available at: http://localhost:3000

🔗 API Endpoints
Authentication
POST /api/auth/login – User login
POST /api/auth/register – User registration
Appointments
GET /api/appointments/available – Fetch available slots
POST /api/appointments/book – Book an appointment
GET /api/appointments/user/{email} – Fetch user’s appointments
POST /api/appointments/cancel/{id} – Cancel an appointment
Admin
POST /api/admin/appointments/add – Add a new slot
PUT /api/admin/appointments/edit/{id} – Edit an appointment slot
DELETE /api/admin/appointments/delete/{id} – Delete an appointment slot
🏃‍♂️ Running with Docker (Optional)
Build the backend Docker image:

sh
Copy
Edit
docker build -t appointment-backend .
Run the backend container:

sh
Copy
Edit
docker run -p 8080:8080 appointment-backend
Run the frontend with Docker Compose:

sh
Copy
Edit
docker-compose up --build
📝 Future Improvements
Email notifications for bookings.
Payment integration for paid appointments.
Calendar view for better scheduling.
📄 License
This project is open-source and available under the MIT License.

