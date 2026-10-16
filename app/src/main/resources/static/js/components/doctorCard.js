
import { deleteDoctor } from "/js/services/doctorServices.js";
import { getPatientData } from "/js/services/patientServices.js";

/**
 * Creates a dynamic, reusable doctor card component for dashboards.
  @param {Object} doctor - Doctor object containing info like name, specialty, etc.
 * @returns {HTMLElement} The constructed card element.
 */
export function createDoctorCard(doctor) {
    // 1. Create Main Card Container
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    // 2. Fetch the User's Role
    const role = localStorage.getItem("userRole");

    // 3. Create Doctor Info Section
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name;

    const specialization = document.createElement("p");
    specialization.textContent = `Specialty: ${doctor.specialty}`;

    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email}`;

    const availability = document.createElement("p");
    availability.textContent = `Availability: ${Array.isArray(doctor.availability) ? doctor.availability.join(", ") : doctor.availability}`;

    // Append info elements
    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    // 4. Create Button Container
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // Conditionally Add Buttons Based on Role
    if (role === "admin") {
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        removeBtn.classList.add("action-btn");

        removeBtn.addEventListener("click", async () => {
            // 1. Confirm deletion
            if (confirm(`Are you sure you want to delete Dr. ${doctor.name}?`)) {
                try {
                    // 2. Get token from localStorage
                    const token = localStorage.getItem("token");
                    // 3. Call API to delete
                    const success = await deleteDoctor(doctor.id, token);
                    // 4. On success: remove the card from the DOM
                    if (success) {
                        card.remove();
                    }
                } catch (error) {
                    console.error("Error deleting doctor:", error);
                }
            }
        });

        actionsDiv.appendChild(removeBtn);
    } else if (role === "patient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.classList.add("button");
        bookNow.addEventListener("click", () => {
            alert("Patient needs to login first.");
        });

        actionsDiv.appendChild(bookNow);
    } else if (role === "loggedPatient") {
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.classList.add("button");
        bookNow.addEventListener("click", async (e) => {
            const token = localStorage.getItem("token");
            const patientData = await getPatientData(token);
            showBookingOverlay(e, doctor, patientData);
        });

        actionsDiv.appendChild(bookNow);
    }

    // - Final Assembly: Add created pieces to the card
    card.appendChild(infoDiv);
    card.appendChild(actionsDiv);

    // Return the final card
    return card;
}