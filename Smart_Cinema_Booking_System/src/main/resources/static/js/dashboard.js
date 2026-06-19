function loadContent(url) {
    fetch(url)
        .then(response => response.text())
        .then(html => {
            document
                .getElementById("main-content")
                .innerHTML = html;
        });
}
function loadHeader(){
    fetch("/dashboard/header")
        .then(response => response.text())
        .then(html => {document.getElementById("header-container").innerHTML = html;});
}

function clearErrors(){
    document
        .querySelectorAll(".error-text")
        .forEach(e => {e.innerText = "";});
}


function showProfileErrors(errors){
    clearErrors();
    if(errors.email){
        document
            .getElementById("emailError")
            .innerText = errors.email;
    }

    if(errors.fullName){
        document
            .getElementById("fullNameError")
            .innerText = errors.fullName;
    }

    if(errors.phone){
        document
            .getElementById("phoneError")
            .innerText =  errors.phone;
    }

    if(errors.address){
        document
            .getElementById("addressError")
            .innerText = errors.address;
    }
}

function showCredentialErrors(errors){
    clearErrors();
    if(errors.currentUsername){
        document
            .getElementById("currentUsernameError")
            .innerText =errors.currentUsername;
    }

    if(errors.currentPassword){
        document.getElementById("currentPasswordError")
            .innerText =　errors.currentPassword;
    }

    if(errors.newUsername){
        document
            .getElementById("newUsernameError")
            .innerText =　errors.newUsername;
    }

    if(errors.newPassword){
        document.getElementById("newPasswordError")
            .innerText =errors.newPassword;
    }
}

// chỉnh sửa thông tin cá nhân
function openProfileModal(){
    document
        .getElementById("profileModal")
        .classList
        .remove("hidden");
}

function closeProfileModal(){
    document
        .getElementById("profileModal")
        .classList
        .add("hidden");

    document
            .getElementById("profileForm")
            .reset();

    clearErrors();
}

function updateProfile(){
    const data = {
        email: document.getElementById("editEmail").value,
        fullName: document.getElementById("editFullName").value,
        phone: document.getElementById("editPhone").value,
        address: document.getElementById("editAddress").value
    };
//  console.log(data);

    fetch("/profile/update",{
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(data)})
    .then(async response => {
        if(!response.ok){
            const errors = await response.json();
            showProfileErrors(errors);
            throw new Error();
        }
        return response.text();
    })
    .then(() => {
        closeProfileModal();
        loadHeader();
        loadContent("/profile/content");
    });
}

// chỉnh sửa thông tin đăng nhập
function openCredentialModal(){
    document
        .getElementById("credentialModal")
        .classList
        .remove("hidden");
}

function closeCredentialModal(){
    document
        .getElementById("credentialModal")
        .classList
        .add("hidden");

    document
        .getElementById("credentialForm")
        .reset();

    clearErrors();
}

function updateCredential(){
    const data = {
        currentUsername: document.getElementById("currentUsername").value,
        currentPassword: document.getElementById("currentPassword").value,
        newUsername: document.getElementById("newUsername").value,
        newPassword: document.getElementById("newPassword").value
    };

    fetch("/profile/update-credential",{
        method:"POST",
        headers:{"Content-Type":"application/json"},
        body:JSON.stringify(data)
    })
    .then(async response => {
        if(!response.ok){
            const errors =　await response.json();
            showCredentialErrors(errors);
            throw new Error();
        }
        return response.text();
    })
    .then(() => {
        alert("Đã cập nhật thông tin đăng nhập. Vui lòng đăng nhập lại.");
        window.location.href = "/login";
    })
    .catch(error => {
//        alert(error.message);
    });
}