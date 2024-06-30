function validateForm() {
    // Get form fields by their IDs
    let userId = document.getElementById("userId").value;
    let userFullName = document.getElementById("userFullName").value;
    // Add more fields as needed

    // Perform validation
    if (!userId || !userFullName) {
        // Display an error message or highlight the fields with errors
        alert("Please fill in all required fields.");
        return false;
    }

    // Add more validation logic as needed

    // If all validation passes
    return true;
}

const userTable = document.getElementsByClassName('user_table');
buttonTable = document.querySelectorAll('.admin_navBtn');

function toggleSkills() {
    let itemClass = this.className;

    for (i = 0; i < userTable.length; i++) { 
        userTable[i].className = 'user_table'; 
        buttonTable[i].classList.remove('admin_navBtn_active'); 
    } 
    if (itemClass === 'patientBtn admin_navBtn') { 
        userTable[0].className = "user_table tableActive"; 
        this.classList.add("admin_navBtn_active"); 
    } else if (itemClass === 'doctorBtn admin_navBtn') { 
        userTable[1].className = "user_table tableActive"; 
        this.classList.add("admin_navBtn_active"); 
    } else if (itemClass === 'pharmacistBtn admin_navBtn') { 
        userTable[2].className = "user_table tableActive"; 
        this.classList.add("admin_navBtn_active"); 
    } else if (itemClass === 'adminBtn admin_navBtn') { 
        userTable[3].className = "user_table tableActive"; 
        this.classList.add("admin_navBtn_active"); 
    } else if (itemClass === 'asspatBtn admin_navBtn') { 
        userTable[4].className = "user_table tableActive"; 
        this.classList.add("admin_navBtn_active"); 
    } 
    
}

buttonTable.forEach((el) => {
    el.addEventListener('click', toggleSkills);
});

const userForm = document.getElementsByClassName('extraForm');
const radioFormInput = document.querySelectorAll('input[type=radio][name="role"]');

function toggleForm() {
    let radioClass = this.className;

    for (i = 0; i < userForm.length; i++) {
        userForm[i].className = 'extraForm form-group';
    }

    if (radioClass === 'form-check-input patient') {
        userForm[0].className = "extraForm form-group activeForm";
    } else if (radioClass === 'form-check-input doctor') {
        userForm[1].className = "extraForm form-group activeForm";
    } else if (radioClass === 'form-check-input pharmacist') {
        userForm[2].className = "extraForm form-group activeForm";
    }
}

radioFormInput.forEach((e) => {
    e.addEventListener('click', toggleForm);
});

const confirmAddUserBtn = document.getElementById("confirmAddUserBtn"),
    cancelAddUserBtn = document.getElementById("cancelBtnAddUser"),
    addUserBtn = document.getElementById("addUserBtn"),
    deleteUserBtn = document.querySelectorAll(".deleteUserBtn"),
    confirmDeleteUserBtn = document.getElementById("confirmDeleteUserBtn"),
    cancelDeleteUserBtn = document.getElementById("cancelDeleteUserBtn"),
    editUserBtn = document.querySelectorAll(".editUserBtn");

addUserBtn.addEventListener("click", function () {
    document.getElementsByClassName("add_user_page-title")[0].classList.add("active-title")
    document.getElementsByClassName("add_user_page-title")[1].classList.remove("active-title")
    document.getElementsByClassName("add_user_page")[0].classList.add("user_page_active")
    document.getElementById("action").value = "add"
})

confirmAddUserBtn.addEventListener("click", function () {
    // Perform validation before submitting the form
    if (validateForm()) {
        let form = document.getElementById("adduser");

        // Create a FormData object from the form
        let formData = new FormData(form);

        // Log the form data to the console
        formData.forEach(function (value, key) {
            console.log(key, value);
        });

        // Extract email-related data
        var to = document.getElementById("userEmail").value;
        var password = document.getElementById("userPassword").value;

        var mailStructure = {
            to: to,
            password: password
        };

        // Send the email request
        sendEmailRequest(formData, mailStructure);

        console.log("Before form submission");
        form.submit();
        console.log("After form submission");
    } else {
        // Handle validation errors
        console.error("Validation failed. Please check the form data.");
    }
});

async function sendEmailRequest(formData, mailStructure) {
    // Construct the email endpoint URL
    const emailEndpoint = "/mail/send/" + mailStructure.to; // Update the endpoint accordingly

    // Make a fetch request to the email endpoint
    try {
        const response = await fetch(emailEndpoint, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(mailStructure),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();
        console.log("Email sent successfully:", data);
    } catch (error) {
        throw new Error('Error sending email: ${error.message}');
    }
}
// izzati

cancelAddUserBtn.addEventListener("click", function () {
    document.getElementsByClassName("add_user_page")[0].classList.remove("user_page_active")
    document.getElementById("userId").value = null;
    document.getElementById("userFullName").value = null;
    document.getElementById("userPassword").value = null;
    document.getElementById("contact").value = null;
    document.getElementById("address").value = null;
    document.getElementById("emergencyContact").value = null;
    document.getElementById("hospital").value = null;
    document.getElementById("doctorPosition").value = null;
    document.getElementById("sensorId").value = null;
    document.getElementById("userId").readOnly = false;
    document.getElementById("userPassword").readOnly = false;
});

cancelAddUserBtn.addEventListener("click", activeRadioForm);


deleteUserBtn.forEach((e) => {
    e.addEventListener("click", function () {
        document.getElementsByClassName("confirmation_deleteUser_page")[0].classList.add("user_page_active")
        //assigning the id value to the hidden form
        document.getElementById("userIdToBeDelete").value = this.closest("tr").
        getElementsByTagName("td")[0].innerText;
        document.getElementById("userRoleToBeDelete").value = this.closest("tr").
        getElementsByTagName("td")[4].innerText;
    });
});

confirmDeleteUserBtn.addEventListener("click", function () {
//    submit form
    document.getElementById("deleteUserForm").submit();
})
cancelDeleteUserBtn.addEventListener("click", function () {
    document.getElementsByClassName("confirmation_deleteUser_page")[0].classList.remove("user_page_active")
    document.getElementById("userIdToBeDelete").value = ""
})

editUserBtn.forEach((e) => {
    e.addEventListener("click", hideRadioForm);
    e.addEventListener("click", function () {
        for (i = 0; i < userForm.length; i++) {
            userForm[i].className = 'extraForm form-group';
        }

        let editClassName = this.className;
        let row = this.closest("tr");

        // Log the row and its HTML content
        console.log("Row:", row);
        console.log("Row HTML:", row.innerHTML);

        let cells = row.getElementsByTagName("td");

        // Log the cells and their innerText values
        console.log("Cells:", cells);
        for (let j = 0; j < cells.length; j++) {
            console.log(`Cell ${j}:`, cells[j].innerText);
        }

        // Set values based on the selected row
        document.getElementById("userId").value = cells[0].innerText;
        document.getElementById("userId").readOnly = true;
        document.getElementById("userFullName").value = cells[1].innerText;
        document.getElementById("userPassword").readOnly = true;
        document.getElementById("contact").value = cells[2].innerText;
        document.getElementById("userEmail").value = cells[3].innerText;

        // Debugging for the selected editClassName
        console.log("Selected editClassName:", editClassName);

        if (editClassName === 'btn btn-warning editUserBtn editPatient') {
            // Debugging specific info of patient
            console.log("Editing patient");
            userForm[0].className = "extraForm form-group activeForm";
            radioFormInput[1].checked = true;
            document.getElementById("address").value = cells[4].innerText;
            document.getElementById("emergencyContact").value = cells[5].innerText;
            document.getElementById("sensorId").value = cells[6].innerText;
        } else if (editClassName === 'btn btn-warning editUserBtn editDoctor') {
            // Debugging specific info of doctor
            console.log("Editing doctor");
            userForm[1].className = "extraForm form-group activeForm";
            radioFormInput[2].checked = true;
            document.getElementById("doctorHospital").value = cells[5].innerText;
            document.getElementById("doctorPosition").value = cells[6].innerText;
        } else if (editClassName === 'btn btn-warning editUserBtn editPharmacist') {
            // Debugging specific info of to pharmacist
            console.log("Editing pharmacist");
            userForm[2].className = "extraForm form-group activeForm";
            radioFormInput[3].checked = true;
            document.getElementById("pharmacistHospital").value = cells[5].innerText;
            document.getElementById("pharmacistPosition").value = cells[6].innerText;
        } else {
            // Debugging specific other class
            console.log("Editing other role");
            radioFormInput[0].checked = true;
        }

        document.getElementsByClassName("add_user_page-title")[1].classList.add("active-title")
        document.getElementsByClassName("add_user_page-title")[0].classList.remove("active-title")
        document.getElementsByClassName("add_user_page")[0].classList.add("user_page_active")
        document.getElementById("action").value = "update";
    });
});

const radioFormBtn = document.querySelectorAll('.radioBtn')
function hideRadioForm() {

    for (i = 0; i < radioFormBtn.length; i++) {
        radioFormBtn[i].classList.add("radioBtn_hide");
    }
}

function activeRadioForm(){
    for (i = 0; i < radioFormBtn.length; i++) {
        radioFormBtn[i].classList.remove("radioBtn_hide");
    }
}

function handleSearchInput(tableId, query) {
    const tableRows = document.getElementById(tableId).getElementsByTagName('tr');

    for (const row of tableRows) {
        const cells = row.getElementsByTagName('td');
        let found = false;

        for (const cell of cells) {
            const columnName = cell.getAttribute('data-column');
            
            if (columnName) {
                const cellContent = cell.textContent.toLowerCase().replace(/-/g, ''); // Remove dashes for phone numbers
                const searchQuery = query.replace(/-/g, ''); // Remove dashes from the search query
                
                if (cellContent.includes(searchQuery)) {
                    found = true;
                    break;
                }
            }
        }

        if (found) {
            row.style.display = 'table-row';
        } else {
            row.style.display = 'none';
        }
    }
}
// Function to reset the display style for all rows
function resetTableDisplay(tableId) {
    const tableRows = document.getElementById(tableId).getElementsByTagName('tr');

    for (const row of tableRows) {
        row.style.display = 'table-row';
    }
}

const searchPatientInput = document.getElementById('search-input-patient');
searchPatientInput.addEventListener('input', () => {
    console.log('searchPatient is triggered');
    const query = searchPatientInput.value.trim().toLowerCase();
    console.log('entered: ', query);
    
    // Reset display style for all rows
    resetTableDisplay('patientTable');
    
    // Handle the search input for the entire patient list and update pagination
    handleSearchInput('patientTable', query);
});


const searchAdminInput = document.getElementById('search-input-admin');
searchAdminInput.addEventListener('input', () => {
    console.log('searchAdmin is triggered')
    const query = searchAdminInput.value.trim().toLowerCase();
    console.log('entered: ', query);
    resetTableDisplay('adminTable'); // Reset display style for all rows
    handleSearchInput('adminTable', query);
});

const searchDoctorInput = document.getElementById('search-input-doctor');
searchDoctorInput.addEventListener('input', () => {
    console.log('searchDoctor is triggered')
    const query = searchDoctorInput.value.trim().toLowerCase();
    console.log('entered: ', query);
    resetTableDisplay('doctorTable'); // Reset display style for all rows
    handleSearchInput('doctorTable', query);
});

const searchPharmacistInput = document.getElementById('search-input-pharmacist');
searchPharmacistInput.addEventListener('input', () => {
    console.log('searchPharmacist is triggered')
    const query = searchPharmacistInput.value.trim().toLowerCase();
    console.log('entered: ', query);
    resetTableDisplay('pharmacistTable'); // Reset display style for all rows
    handleSearchInput('pharmacistTable', query);
});