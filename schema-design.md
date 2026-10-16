## MySQL Database Design

### Table: patients
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), Not Null
- email: VARCHAR(100), Unique, Not Null
- phone: VARCHAR(20)

### Table: doctors
- id: INT, Primary Key, Auto Increment
- name: VARCHAR(100), Not Null
- specialization: VARCHAR(100), Not Null
- email: VARCHAR(100), Unique, Not Null

### Table: appointments
- id: INT, Primary Key, Auto Increment[cite: 14]
- doctor_id: INT, Foreign Key -> doctors(id)[cite: 14]
- patient_id: INT, Foreign Key -> patients(id)[cite: 14]
- appointment_time: DATETIME, Not Null

  ### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "patientName": "John Smith",
  "appointmentId": 51,
  "medication": "Paracetamol",
  "dosage": "500mg",
  "doctorNotes": "Take 1 tablet every 6 hours.",
  "refillCount": 2,
  "pharmacy": {
    "name": "Walgreens SF",
    "location": "Market Street"
  }
}
