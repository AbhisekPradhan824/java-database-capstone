// render.js

function selectRole(role) {
  setRole(role);
  const token = localStorage.getItem('token');

  if (role === "admin") {
    if (token) {
      window.location.href = `http://localhost:8080/adminDashboard/${token}`;
    } else {
      // ✅ Open Admin Login modal if no token exists yet
      if (typeof openModal === 'function') {
        openModal('adminLogin');
      } else {
        alert("❌ Please log in as Admin first.");
      }
    }
  } 
  else if (role === "patient") {
    window.location.href = "/pages/patientDashboard.html";
  } 
  else if (role === "doctor") {
    if (token) {
      window.location.href = `http://localhost:8080/doctorDashboard/${token}`;
    } else {
      if (typeof openModal === 'function') {
        openModal('doctorLogin');
      } else {
        alert("❌ Please log in as Doctor first.");
      }
    }
  } 
  else if (role === "loggedPatient") {
    window.location.href = "/pages/loggedPatientDashboard.html";
  }
}


function renderContent() {
  const role = getRole();
  if (!role) {
    window.location.href = "/"; // if no role, send to role selection page
    return;
  }

 

  
}