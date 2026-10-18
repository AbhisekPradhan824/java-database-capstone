# User Story Template

**Title:**
_As a [user role], I want [feature/goal], so that [reason]._

**Acceptance Criteria:**
1. [Criteria 1]
2. [Criteria 2]
3. [Criteria 3]

**Priority:** [High/Medium/Low]
**Story Points:** [Estimated Effort in Points]
**Notes:**
- [Additional information or edge cases]


# User Story 1: Admin Login

**Title:**
_As an admin, I want to log into the portal with my username and password, so that I can manage the platform securely._

**Acceptance Criteria:**
1. Admin can input credentials on the login screen.
2. System validates administrator privileges upon login.
3. Successful login navigates the admin to the administrative dashboard.

**Priority:** High
**Story Points:** 3
**Notes:**
- Ensure elevated privileges and multi-factor authentication if applicable.

---

# User Story 2: Admin Logout

**Title:**
_As an admin, I want to log out of the portal, so that I can protect system access._

**Acceptance Criteria:**
1. Logged-in admin can access a clear "Logout" option.
2. Clicking logout immediately invalidates the administrative session.
3. System redirects the user to the login page.

**Priority:** Medium
**Story Points:** 1
**Notes:**
- Ensure session tokens and admin cache are cleared completely upon logout.

---

# User Story 3: Add Doctor Profile

**Title:**
_As an admin, I want to add doctors to the portal, so that patients can view and book appointments with them._

**Acceptance Criteria:**
1. Admin can access an "Add Doctor" form in the admin panel.
2. Form collects required doctor details (name, specialty, contact info, availability).
3. New doctor profile is saved and immediately visible to patients on the platform.

**Priority:** High
**Story Points:** 5
**Notes:**
- Input validation required for contact and credential fields.

---

# User Story 4: Delete Doctor Profile

**Title:**
_As an admin, I want to delete a doctor's profile from the portal, so that inactive or removed doctors are no longer listed._

**Acceptance Criteria:**
1. Admin can select and delete a doctor from the doctor management list.
2. System prompts a confirmation step before permanent deletion/archival.
3. The doctor profile and future open slots are removed from public view.

**Priority:** High
**Story Points:** 3
**Notes:**
- Check for existing bookings before permitting hard deletion.

---

# User Story 5: Track Monthly Appointment Statistics

**Title:**
_As an admin, I want to run a stored procedure in MySQL CLI to get the number of appointments per month, so that I can track usage statistics._

**Acceptance Criteria:**
1. Stored procedure exists in the MySQL database to aggregate monthly appointments.
2. Admin can execute the procedure via MySQL CLI.
3. Query returns formatted monthly appointment count metrics accurately.

**Priority:** Medium
**Story Points:** 3
**Notes:**
- DB script and execution permissions need to be configured for admin database user.

  # User Story 6: View Doctors List Without Login

**Title:**
_As a patient, I want to view a list of doctors without logging in, so that I can explore options before registering._

**Acceptance Criteria:**
1. Unauthenticated users can access and browse the doctor directory.
2. The doctor list displays essential details (name, specialty, location/availability).
3. Clicking on a doctor prompts the user to log in or register only when attempting to book.

**Priority:** High
**Story Points:** 3
**Notes:**
- Public access should be read-only.

---

# User Story 7: Patient Registration

**Title:**
_As a patient, I want to sign up using my email and password, so that I can book appointments._

**Acceptance Criteria:**
1. Registration form accepts email and password.
2. System validates email format and password strength requirements.
3. Upon successful registration, a confirmation email is sent and the account is created.

**Priority:** High
**Story Points:** 5
**Notes:**
- Ensure secure password hashing and input validation.

---

# User Story 8: Patient Login & Account Management

**Title:**
_As a patient, I want to log into the portal, so that I can manage my bookings._

**Acceptance Criteria:**
1. Patient can authenticate using valid email and password credentials.
2. System navigates to a personal dashboard/bookings overview upon successful login.
3. System displays clear error messages for invalid credentials.

**Priority:** High
**Story Points:** 3
**Notes:**
- Implement session persistence or JWT tokens for authenticated navigation.

---

# User Story 9: Patient Logout

**Title:**
_As a patient, I want to log out of the portal, so that I can secure my account._

**Acceptance Criteria:**
1. A clear "Logout" option is visible to logged-in users.
2. Clicking logout terminates the current user session.
3. Redirect user to the home or login page after logging out.

**Priority:** Medium
**Story Points:** 1
**Notes:**
- Clear session cache/tokens on client side upon logout.

---

# User Story 10: Book Doctor Appointment

**Title:**
_As a patient, I want to log in and book an hour-long appointment, so that I can consult with a doctor._

**Acceptance Criteria:**
1. Logged-in patients can select a doctor, available date, and 1-hour time slot.
2. System validates slot availability to prevent double-booking.
3. Confirmation summary/receipt is generated after booking succeeds.

**Priority:** High
**Story Points:** 5
**Notes:**
- Ensure timeslots are locked temporarily during the booking flow.

---

# User Story 11: View Upcoming Appointments

**Title:**
_As a patient, I want to view my upcoming appointments, so that I can prepare accordingly._

**Acceptance Criteria:**
1. Dashboard lists all active/upcoming appointments chronologically.
2. Details include doctor name, specialty, date, and time slot.
3. Empty state is displayed clearly if no upcoming appointments exist.

**Priority:** High
**Story Points:** 3
**Notes:**
- Filter out past/canceled appointments from the primary view.

  # User Story 12: Doctor Login

**Title:**
_As a doctor, I want to log into the portal, so that I can manage my appointments._[cite: 6]

**Acceptance Criteria:**
1. Doctor can input credentials on the login screen.[cite: 6]
2. System verifies doctor role permissions upon authentication.[cite: 6]
3. Successful login navigates the doctor to their appointment dashboard.[cite: 6]

**Priority:** High
**Story Points:** 3
**Notes:**
- Ensure secure authentication and role-based access control.

---

# User Story 13: Doctor Logout

**Title:**
_As a doctor, I want to log out of the portal, so that I can protect my data._[cite: 6]

**Acceptance Criteria:**
1. A clear "Logout" option is visible within the doctor portal.[cite: 6]
2. Clicking logout immediately terminates the current session.[cite: 6]
3. System redirects the user back to the login page.[cite: 6]

**Priority:** Medium
**Story Points:** 1
**Notes:**
- Clear session tokens and cache upon logout.

---

# User Story 14: View Appointment Calendar

**Title:**
_As a doctor, I want to view my appointment calendar, so that I can stay organized._[cite: 6]

**Acceptance Criteria:**
1. Doctor dashboard features an integrated calendar view.[cite: 6]
2. Calendar displays all scheduled bookings clearly by date and time slot.[cite: 6]
3. Doctor can toggle between daily and weekly views.[cite: 6]

**Priority:** High
**Story Points:** 5
**Notes:**
- Pull data dynamically from the appointment database.

---

# User Story 15: Mark Unavailability

**Title:**
_As a doctor, I want to mark my unavailability, so that I can inform patients of only the available slots._[cite: 6]

**Acceptance Criteria:**
1. Doctor can select specific dates and time ranges to block off.[cite: 6]
2. System updates availability status so patients cannot book blocked slots.[cite: 6]
3. Unavailability is visually distinct on the doctor's calendar view.[cite: 6]

**Priority:** High
**Story Points:** 3
**Notes:**
- Prevent overlapping bookings when marking time as unavailable.

---

# User Story 16: Update Profile Information

**Title:**
_As a doctor, I want to update my profile with specialization and contact information, so that patients have up-to-date information._[cite: 6]

**Acceptance Criteria:**
1. Doctor can access an editable profile settings page.[cite: 6]
2. Form fields allow updating specialization, bio, and contact details.[cite: 6]
3. Saved changes reflect immediately on the public patient portal.[cite: 6]

**Priority:** Medium
**Story Points:** 3
**Notes:**
- Validate input formats for contact numbers and email fields.

---

# User Story 17: View Patient Details

**Title:**
_As a doctor, I want to view the patient details for upcoming appointments, so that I can be prepared._[cite: 6]

**Acceptance Criteria:**
1. Doctor can click on any upcoming appointment entry to view participant details.[cite: 6]
2. View displays relevant patient information (such as name, contact info, and notes).[cite: 6]
3. Patient information access is restricted strictly to authorized doctors.[cite: 6]

**Priority:** High
**Story Points:** 3
**Notes:**
- Ensure compliance with privacy and data handling practices.

  
