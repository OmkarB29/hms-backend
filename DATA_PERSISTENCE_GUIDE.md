# Data Persistence Configuration 💾

## Problem Solved
Previously, data was lost every time the backend was restarted because the application was using an **in-memory H2 database** (`jdbc:h2:mem:testdb`).

## Solution Applied
Switched to **file-based H2 database** that persists data on disk.

## Configuration Changes

### Before (In-Memory):
```properties
spring.datasource.url=jdbc:h2:mem:testdb
```
- Data stored only in RAM
- Lost when application restarts
- Good for testing, bad for production

### After (File-Based - Persistent):
```properties
spring.datasource.url=jdbc:h2:file:./data/hms
```
- Data stored on disk at `./data/hms`
- Persists across application restarts
- Perfect for development and production

## Database Location
The database files are stored in:
```
demo/data/hms.mv.db
demo/data/hms.trace.db
```

These files are created automatically on first run and contain all your data.

## Application Startup Log Evidence
```
HikariPool-1 - Added connection conn0: url=jdbc:h2:file:./data/hms user=SA
```
✅ Confirms file-based database is active

## Data Persistence Workflow

### First Run (Fresh Start):
1. Application starts
2. H2 creates database files in `./data/` directory
3. Hibernate creates all tables (users, students, rooms, fees, complaints, etc.)
4. Application ready for data entry

### Subsequent Runs:
1. Application starts
2. H2 opens existing `./data/hms` database
3. All previous data is loaded from disk
4. Tables already exist, no schema recreation needed
5. Your registered students, rooms, fees all appear!

### Registration Persistence Flow:
```
User Registration Form
         ↓
   /api/auth/register
         ↓
  Create User + Student records
         ↓
   Save to H2 Database File
         ↓
   Data persists on disk
         ↓
  Application restart
         ↓
Data still available! ✅
```

## How to Verify Data Persistence

### Method 1: Using H2 Console
1. Open browser: `http://localhost:8080/h2-console`
2. Login: User=`sa`, Password=(empty)
3. Connection String: `jdbc:h2:file:./data/hms`
4. Query: `SELECT * FROM users;` or `SELECT * FROM students;`
5. Restart backend
6. Query again - **data will still be there!**

### Method 2: Using File System
1. Check demo folder for `data/` directory
2. Should contain: `hms.mv.db` and `hms.trace.db`
3. These files contain all your data
4. Don't delete these files if you want to keep data!

## Key Differences

| Feature | In-Memory (Old) | File-Based (New) |
|---------|-----------------|------------------|
| Database Location | RAM Only | Disk at `./data/hms` |
| Data Persistence | ❌ Lost on restart | ✅ Persists forever |
| Speed | ⚡ Faster | ✅ Slightly slower (negligible) |
| Storage | Limited by RAM | Limited by disk space |
| Use Case | Testing only | Development + Production |

## Testing Data Persistence

### Step 1: Register a Student
1. Start backend: `mvn spring-boot:run`
2. Open React frontend: `http://localhost:3000`
3. Click Register
4. Fill in student details:
   - Username: `testuser`
   - Password: `test123`
   - Email: `test@example.com`
   - Name: `Test User`
   - Phone: `9876543210`
   - Enrollment No: `CS001`
5. Click Register → Should see success message

### Step 2: Stop Backend
- In terminal: Press `Ctrl+C` to stop `mvn spring-boot:run`
- Wait for graceful shutdown

### Step 3: Restart Backend
- Run: `mvn spring-boot:run` again
- Application restarts with fresh connections to database file

### Step 4: Login with Same Credentials
1. Open React frontend: `http://localhost:3000`
2. Click Login
3. Use same credentials:
   - Username: `testuser`
   - Password: `test123`
4. Click Login → **Should login successfully!**
5. Data persisted! ✅

## H2 Console Details

### Access Point:
```
http://localhost:8080/h2-console
```

### Login Credentials:
- **User**: `sa`
- **Password**: (leave empty)
- **Connection URL**: `jdbc:h2:file:./data/hms`

### Important Queries:
```sql
-- View all users
SELECT * FROM users;

-- View all students
SELECT * FROM students;

-- View all rooms
SELECT * FROM rooms;

-- View all fees
SELECT * FROM fees;

-- Count total students
SELECT COUNT(*) FROM students;

-- View specific student details
SELECT * FROM students WHERE username = 'testuser';
```

## Important Notes

### ✅ Do's:
- Let H2 manage the database files automatically
- Keep `./data/` directory intact
- Backup the `./data/` folder before major changes
- Use H2 console for data inspection only

### ❌ Don'ts:
- Don't delete `./data/` folder unless you want to lose all data
- Don't manually edit database files
- Don't move the application to different directory (path changes)
- Don't share database files directly (create backups instead)

## If You Want to Reset Data

If you want to start fresh (clear all data):

### Option 1: Delete Database Files
```bash
# Delete the data folder
rm -rf ./data/
# or on Windows: del /s /q data\
```
Next startup will create fresh database with empty tables.

### Option 2: Keep Database but Clear Tables
Use H2 Console:
```sql
DELETE FROM students;
DELETE FROM users;
DELETE FROM rooms;
DELETE FROM fees;
DELETE FROM complaints;
DELETE FROM notices;
DELETE FROM room_changes;
DELETE FROM room_change_request;
```

## Future Enhancements

1. **Database Backup**: Add scheduled backups of `./data/` folder
2. **MySQL Migration**: Switch to MySQL for production
3. **Data Export**: Add features to export data to CSV/Excel
4. **Database Versioning**: Implement Flyway for schema versioning

## Troubleshooting

### Issue: Application won't start
**Solution**: Check if `./data/` folder has correct permissions. Delete it and restart.

### Issue: Data looks corrupted
**Solution**: Check H2 logs. If needed, delete `./data/` and restart (loses data).

### Issue: Want to move project to different folder
**Solution**: Move entire project folder (including `./data/`) together. Don't copy just source code.

---

**Configuration File**: `src/main/resources/application.properties`
**Database Driver**: H2
**Connection Type**: File-Based (Persistent)
**Data Location**: `./demo/data/hms.mv.db`
**Last Updated**: November 11, 2025
