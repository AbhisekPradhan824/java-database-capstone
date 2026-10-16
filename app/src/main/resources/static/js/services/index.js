import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

const ADMIN_API = API_BASE_URL + '/admin';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

window.onload = function () {
    const adminBtn = document.getElementById('adminLogin');
    if (adminBtn) {
        adminBtn.addEventListener('click', () => {
            openModal('adminLogin');
        });
    }

    const doctorBtn = document.getElementById('doctorLogin');
    if (doctorBtn) {
        doctorBtn.addEventListener('click', () => {
            openModal('doctorLogin');
        });
    }
};

/**
 * Handles administrator authentication and session token storage.
 */
window.adminLoginHandler = async function adminLoginHandler() {
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    
    const username = usernameInput ? usernameInput.value : '';
    const password = passwordInput ? passwordInput.value : '';

    const admin = { username, password };

    try {
        const response = await fetch(ADMIN_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(admin)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token || data);
            if (typeof selectRole === "function") {
                selectRole("admin");
            } else {
                window.location.href = "/admin/adminDashboard.html";
            }
        } else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        console.error("Error during admin login:", error);
        alert("An unexpected error occurred.");
    }
};

/**
 * Handles doctor authentication and session token storage.
 */
window.doctorLoginHandler = async function doctorLoginHandler() {
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');

    const email = emailInput ? emailInput.value : '';
    const password = passwordInput ? passwordInput.value : '';

    const doctor = { email, password };

    try {
        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem("token", data.token || data);
            if (typeof selectRole === "function") {
                selectRole("doctor");
            } else {
                window.location.href = "/doctor/doctorDashboard.html";
            }
        } else {
            alert("Invalid credentials!");
        }
    } catch (error) {
        console.error("Error during doctor login:", error);
        alert("An unexpected error occurred.");
    }
};