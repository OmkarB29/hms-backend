# Admin Dashboard API Setup 🎯

## Overview
The Admin Dashboard now fetches **real-time data** from the backend instead of displaying hardcoded values.

## Backend API Endpoints Created

All endpoints are in `AdminController.java` at `/api/admin/`

### 1. **Get All Students**
```
GET /api/admin/all-students
```
**Response**: List of all Student objects with their details
**Usage**: Fetches total student count for dashboard

### 2. **Get All Rooms**
```
GET /api/admin/all-rooms
```
**Response**: List of all Room objects
**Usage**: Fetches total rooms count for dashboard

### 3. **Get All Fees**
```
GET /api/admin/all-fees
```
**Response**: List of all Fee objects
**Usage**: Fetches all fees and sums them up for total fees collected

### 4. **Get Pending Registrations**
```
GET /api/admin/pending-registrations
```
**Response**: Currently returns empty list (can be enhanced)
**Usage**: Fetches pending registrations count

### 5. **Get Dashboard Overview**
```
GET /api/admin/overview
```
**Response**:
```json
{
  "totalStudents": 0,
  "totalRooms": 0,
  "totalFees": 0,
  "totalFeesCollected": 0,
  "pendingRegistrations": 0
}
```
**Usage**: Single endpoint to fetch all dashboard statistics

## Frontend Integration

### AdminDashboard.js Changes
- Added `axios` for HTTP requests
- Added state management for dashboard data, loading, and error states
- Implemented `useEffect` hook to fetch data on component mount
- Updated API endpoints to call new backend endpoints:
  - `/api/admin/all-students`
  - `/api/admin/all-rooms`
  - `/api/admin/all-fees`
  - `/api/admin/pending-registrations`

### Data Display
Overview section now displays:
- ✅ **Pending Registrations**: Real-time count from backend
- ✅ **Total Students**: Real-time count from database
- ✅ **Total Rooms**: Real-time count from database
- ✅ **Total Fees Collected**: Sum of all fee amounts in database

## Authentication
All endpoints require JWT Bearer token from localStorage:
```javascript
const token = localStorage.getItem("token");
const headers = { Authorization: `Bearer ${token}` };
```

## Error Handling
- Loading state shown while fetching data
- Error messages displayed if API calls fail
- Graceful fallback for optional endpoints (pending registrations)

## Build Status
✅ **Compilation**: SUCCESS
✅ **Application**: Running on port 8080
✅ **Database**: H2 initialized with schema

## Testing the Dashboard

1. **Register a new user** (Student/Admin/Warden)
2. **Login** as admin/user
3. **Navigate** to Admin Dashboard
4. **Overview tab** will now show:
   - Real student count
   - Real room count
   - Real total fees collected
   - Real pending registrations count

## Database Relationships

**Students Table** ↔ **Users Table** (OneToOne)
- When a student registers, both User and Student records are created
- Each student has a foreign key reference to their User record

**Fees Table**
- Stores fee records with amount field
- Dashboard sums all fee.amount values

**Rooms Table**
- Stores room information
- Dashboard counts total rooms

## Future Enhancements

1. **Pending Registrations**: Create logic to track users awaiting approval
2. **Dynamic Refresh**: Add refresh button to reload data
3. **Real-time Updates**: Implement WebSocket for live data updates
4. **Filtering**: Add filters to view data by date range, status, etc.
5. **Export Reports**: Add export functionality to generate reports

## API Response Examples

### `/api/admin/all-students`
```json
[
  {
    "id": 1,
    "username": "student1",
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "9876543210",
    "department": "Computer Science",
    "rollNumber": "CS001",
    "roomNumber": "101",
    "fee": 50000
  }
]
```

### `/api/admin/all-rooms`
```json
[
  {
    "id": 1,
    "roomNo": "101",
    "capacity": 2
  }
]
```

### `/api/admin/all-fees`
```json
[
  {
    "id": 1,
    "studentName": "John Doe",
    "amount": 50000,
    "status": "PAID"
  }
]
```

## Troubleshooting

### Dashboard shows "Loading..." indefinitely
- Check browser console for API errors
- Verify JWT token is valid in localStorage
- Check Spring Boot logs for endpoint errors

### Dashboard shows 0 for all values
- Add sample data through the H2 console
- Verify data exists in respective tables:
  - `SELECT * FROM students;`
  - `SELECT * FROM rooms;`
  - `SELECT * FROM fees;`

### 401 Unauthorized Error
- User is not authenticated
- Re-login and ensure token is stored in localStorage
- Check if JWT token has expired

---

**Version**: 1.0  
**Last Updated**: November 11, 2025
