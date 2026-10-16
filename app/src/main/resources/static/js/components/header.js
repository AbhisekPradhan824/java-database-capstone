/**
 * Renders the dynamic header component based on the current page context,
 * user role, and authentication token stored in localStorage.
 */
function renderHeader() {
    const headerDiv = document.getElementById("header");
    if (!headerDiv) return;

    // Check if the current page is the homepage; do not show the role-based header there
    if (window.location.pathname.endsWith("/")) {
        localStorage.removeItem("userRole");
        headerDiv.innerHTML = `
          <header class="header">
            <div class="logo-section">
              <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
              <span class="logo-title">Hospital CMS</span>
            </div>
          </header>`;
        return;
      }

    // Look up the user's role and login token in localStorage
    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");

    let headerContent = `<header class="header">
         <div class="logo-section">
           <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
           <span class="logo-title">Hospital CMS</span>
         </div>
         <nav>`;

    // Check for invalid session or missing credentials
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
      }
      else if (role === "admin") {
        headerContent += `
          <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
          <a href="#" onclick="logout()">Logout</a>`;
      } else if (role === "doctor") {
        headerContent += `
          <button class="adminBtn"  onclick="selectRole('doctor')">Home</button>
          <a href="#" onclick="logout()">Logout</a>`;
      } else if (role === "patient") {
        headerContent += `
          <button id="patientLogin" class="adminBtn">Login</button>
          <button id="patientSignup" class="adminBtn">Sign Up</button>`;
      } else if (role === "loggedPatient") {
        headerContent += `
          <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
          <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
          <a href="#" onclick="logoutPatient()">Logout</a>`;
      }

      headerContent += `</nav></header>`;

    // Finalize header injection into the DOM
    headerDiv.innerHTML = headerContent;
    attachHeaderButtonListeners();
}

/**
 * Attaches event listeners dynamically to elements rendered inside the header.
 */
function attachHeaderButtonListeners() {
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener("click", () => {
            // Modal trigger handled via inline or listener
        });
    }
}

/**
 * Clears session tokens and redirects back to the homepage.
 */
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    window.location.href = "/";
}

/**
 * Clears patient session token and resets role back to standard patient view.
 */
function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    window.location.href = "/patient/patientDashboard.html";
}

// Automatically execute header rendering when DOM content is loaded
document.addEventListener("DOMContentLoaded", renderHeader);
