function loadContent(url) {
    fetch(url)
        .then(response => response.text())
        .then(html => {
            document
                .getElementById("main-content")
                .innerHTML = html;
        });
}

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
    .then(response => response.text())
    .then(() => {
        closeProfileModal();
        loadHeader();
        loadContent("/profile/content");
    });
}

function loadHeader(){
    fetch("/dashboard/header")
        .then(response => response.text())
        .then(html => {document.getElementById("header-container").innerHTML = html;});
}