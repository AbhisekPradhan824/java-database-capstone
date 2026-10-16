import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

// Event binding for the "Add Doctor" button to trigger the modal
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById('addDocBtn').addEventListener('click', () => {
        openModal('addDoctor');
       });
       

    // Load initial doctor cards on page load
    loadDoctorCards();

    // Set up search and filter event listeners
    document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);

});

/**
 * Fetches all doctors from the backend and displays them in the dashboard.
 */
async function loadDoctorCards() {
    try {
        const contentDiv = document.getElementById("content");
        if (!contentDiv) return;
        contentDiv.innerHTML = "";

        const doctors = await getDoctors();
        renderDoctorCards(doctors);
    } catch (error) {
        console.error("Error loading doctor cards:", error);
    }
}

/**
 * Utility function to render a list of doctor cards.
 * Iterates through the doctors array and appends each card to the content container.
 */
function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    if (!contentDiv) return;

    contentDiv.innerHTML = "";

    if (!doctors || doctors.length === 0) {
        contentDiv.innerHTML = "<p>No doctors found.</p>";
        return;
    }

    doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

/**
 * Handles real-time search and filter changes.
 */
async function filterDoctorsOnChange() {
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    const name = searchBar ? searchBar.value : "";
    const time = filterTime ? filterTime.value : "";
    const specialty = filterSpecialty ? filterSpecialty.value : "";

    try {
        const filtered = await filterDoctors(name, time, specialty);
        renderDoctorCards(filtered);
    } catch (error) {
        console.error("Error filtering doctors:", error);
    }
}

/**
 * Handles the "Add Doctor" modal form submission.
 * Collects input data, verifies authorization token, and saves the new doctor.
 */
window.adminAddDoctor = async function adminAddDoctor(event) {
    if (event) event.preventDefault();

    const token = localStorage.getItem("token");
    if (!token) {
        alert("Unauthorized. Please log in as an admin.");
        return;
    }

    const nameInput = document.getElementById("docName");
    const specialtyInput = document.getElementById("docSpecialty");
    const emailInput = document.getElementById("docEmail");
    const passwordInput = document.getElementById("docPassword");
    const mobileInput = document.getElementById("docMobile");
    const timeInput = document.getElementById("docAvailabilityTime");

    const doctor = {
        name: nameInput ? nameInput.value : "",
        specialty: specialtyInput ? specialtyInput.value : "",
        email: emailInput ? emailInput.value : "",
        password: passwordInput ? passwordInput.value : "",
        mobile: mobileInput ? mobileInput.value : "",
        availabilityTime: timeInput ? timeInput.value : ""
    };

    try {
        const result = await saveDoctor(doctor, token);
        if (result.success) {
            alert("Doctor added successfully!");
            loadDoctorCards();
        } else {
            alert("Failed to add doctor: " + result.message);
        }
    } catch (error) {
        console.error("Error saving doctor:", error);
        alert("An unexpected error occurred.");
    }
};