
/*
 * doctorServices.js
 * Handles all API interactions related to doctor data.
 */

import { API_BASE_URL } from "../config/config.js";

// Base endpoint for doctor-related API operations
const DOCTOR_API = `${API_BASE_URL}/doctor`;

/**
 * Function: getDoctors
 * Purpose: Fetch all doctors from the API.
 *
 * @returns {Promise<Array>} List of doctors, or an empty array on error.
 */
export async function getDoctors() {
    try {
        const response = await fetch(DOCTOR_API, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`Failed to fetch doctors: ${response.status}`);
        }

        const data = await response.json();

        // Return the doctors array from the API response
        return Array.isArray(data.doctors) ? data.doctors : [];

    } catch (error) {
        console.error("Error fetching doctors:", error);
        return [];
    }
}

/**
 * Function: deleteDoctor
 * Purpose: Delete a doctor using their ID and admin token.
 *
 * @param {string|number} id - Unique doctor ID.
 * @param {string} token - Authentication token.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function deleteDoctor(id, token) {
    try {
        const url =
            `${DOCTOR_API}/${encodeURIComponent(id)}/${encodeURIComponent(token)}`;

        const response = await fetch(url, {
            method: "DELETE",
            headers: {
                "Accept": "application/json"
            }
        });

        const data = await response.json();

        if (!response.ok) {
            return {
                success: false,
                message: data.message || "Failed to delete doctor."
            };
        }

        return {
            success: data.success ?? true,
            message: data.message || "Doctor deleted successfully."
        };

    } catch (error) {
        console.error("Error deleting doctor:", error);

        return {
            success: false,
            message: "An error occurred while deleting the doctor."
        };
    }
}

/**
 * Function: saveDoctor
 * Purpose: Add a new doctor using a POST request.
 *
 * @param {Object} doctor - Doctor details.
 * @param {string} token - Admin authentication token.
 * @returns {Promise<{success: boolean, message: string}>}
 */
export async function saveDoctor(doctor, token) {
    try {
        const url =
            `${DOCTOR_API}/${encodeURIComponent(token)}`;

        const response = await fetch(url, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json"
            },
            body: JSON.stringify(doctor)
        });

        const data = await response.json();

        if (!response.ok) {
            return {
                success: false,
                message: data.message || "Failed to save doctor."
            };
        }

        return {
            success: data.success ?? true,
            message: data.message || "Doctor saved successfully."
        };

    } catch (error) {
        console.error("Error saving doctor:", error);

        return {
            success: false,
            message: "An error occurred while saving the doctor."
        };
    }
}

/**
 * Function: filterDoctors
 * Purpose: Fetch doctors matching name, time, and specialty.
 *
 * @param {string} name - Doctor name filter.
 * @param {string} time - Availability/time filter.
 * @param {string} specialty - Medical specialty filter.
 * @returns {Promise<{doctors: Array}>}
 */
export async function filterDoctors(
    name = "",
    time = "",
    specialty = ""
) {
    try {
        // Convert empty or null filters to empty strings.
        const nameParam = encodeURIComponent(name ?? "");
        const timeParam = encodeURIComponent(time ?? "");
        const specialtyParam = encodeURIComponent(specialty ?? "");

        const url =
            `${DOCTOR_API}/filter/${nameParam}/${timeParam}/${specialtyParam}`;

        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(
                `Failed to filter doctors: ${response.status}`
            );
        }

        const data = await response.json();

        return {
            doctors: Array.isArray(data.doctors) ? data.doctors : []
        };

    } catch (error) {
        console.error("Error filtering doctors:", error);

        return {
            doctors: []
        };
    }
}
