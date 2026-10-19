import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

// Global variables
let patientTableBody = null;
let selectedDate = new Date().toISOString().split('T')[0]; // Today's date in YYYY-MM-DD format
let token = localStorage.getItem('token');
let patientName = null;

document.addEventListener('DOMContentLoaded', () => {
    patientTableBody = document.getElementById('patientTableBody');
    const datePicker = document.getElementById('datePicker');
    const todayButton = document.getElementById('todayButton');
    const searchBar = document.getElementById('searchBar');

    // Initialize date picker with today's date
    if (datePicker) {
        datePicker.value = selectedDate;
        datePicker.addEventListener('change', (e) => {
            selectedDate = e.target.value;
            loadAppointments();
        });
    }

    // "Today's Appointments" button event listener
    if (todayButton) {
        todayButton.addEventListener('click', () => {
            selectedDate = new Date().toISOString().split('T')[0];
            if (datePicker) {
                datePicker.value = selectedDate;
            }
            loadAppointments();
        });
    }

    // Search bar event listener for filtering by patient name
    if (searchBar) {
        searchBar.addEventListener('input', (e) => {
            const value = e.target.value.trim();
            patientName = value === '' ? null : value;
            loadAppointments();
        });
    }

    // Initial render on page load
    loadAppointments();
});

/**
 * Fetches appointments using selected date, patient name, and token, 
 * then renders them into the appointment table body.
 */
async function loadAppointments() {
    if (!patientTableBody) return;

    // Clear existing content in the table
    patientTableBody.innerHTML = '';

    try {
        const appointments = await getAllAppointments(selectedDate, patientName, token);

        if (!appointments || appointments.length === 0) {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td colspan="5" class="text-center">No Appointments found for today</td>`;
            patientTableBody.appendChild(tr);
            return;
        }

        // Iterate through results and append each row using createPatientRow()
        appointments.forEach(appointment => {
            const row = createPatientRow(appointment);
            patientTableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error loading appointments:', error);
        // Fallback error message row in the table
        const tr = document.createElement('tr');
        tr.innerHTML = `<td colspan="5" class="text-center text-danger">Failed to load appointments. Please try again later.</td>`;
        patientTableBody.appendChild(tr);
    }
}