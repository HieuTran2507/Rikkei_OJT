function loadContent(url) {
    console.log(url);
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
function goToPage(page){

    const keyword = document.getElementById("search").value;

    loadContent(
        "/admin/movies/content?page="
        + page
        + "&keyword="
        + encodeURIComponent(keyword)
    );
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
        console.log(error.message);
    });
}

// Thêm, sửa phim
function openCreateMovieModal() {
    document.getElementById("createMovieModal")
        .classList.remove("hidden");
}
function closeCreateMovieModal() {
    document.getElementById("createMovieModal")
        .classList.add("hidden");

    document.getElementById("createMovieForm").reset();
    clearMovieErrors();
}
function clearMovieErrors() {
    document.querySelectorAll(".error-text")
        .forEach(e => e.innerText = "");
}
function showMovieErrors(errors) {

    clearMovieErrors();

    if (errors.title)
        document.getElementById("titleError").innerText = errors.title;

    if (errors.description)
        document.getElementById("descriptionError").innerText = errors.description;

    if (errors.duration)
        document.getElementById("durationError").innerText = errors.duration;

    if (errors.releaseDate)
        document.getElementById("releaseDateError").innerText = errors.releaseDate;

    if (errors.genreIds)
        document.getElementById("genreError").innerText = errors.genreIds;

    if (errors.poster)
        document.getElementById("posterError").innerText = errors.poster;
}
function saveMovie() {

    const formData = new FormData();
    const movieId = document.getElementById("movieId").value;

    formData.append("movieId",movieId);
    formData.append("title", document.getElementById("title").value);
    formData.append("description", document.getElementById("description").value);
    formData.append("duration", document.getElementById("duration").value);
    formData.append("releaseDate", document.getElementById("releaseDate").value);
    formData.append("language", document.getElementById("language").value);
    formData.append("status", document.getElementById("status").value);

    // poster
    const poster = document.getElementById("poster").files[0];
    if (poster) {
        formData.append("poster", poster);
    }

    // genres
    document.querySelectorAll("input[name='genreIds']:checked")
        .forEach(cb => formData.append("genreIds", cb.value));

    const url = movieId ? "/admin/movies/update" : "/admin/movies/create";

    fetch(url, {
        method: "POST",
        body: formData
    })
    .then(async res => {
        if (!res.ok) {
            const errors = await res.json();
            showMovieErrors(errors);
            throw new Error("Validation error");
        }
        return res.text();
    })
    .then(() => {
        closeCreateMovieModal();
        // reload list giống profile
       loadContent("/admin/movies/content");

    })
    .catch(err => {
        console.log(err);
    });
}
function previewPoster(input) {

    const file = input.files[0];

    if (!file) {
        document.getElementById("posterPreview").src ="/posters/default.png";
        return;
    }

    const reader = new FileReader();

    reader.onload = function(e) {
        document.getElementById("posterPreview").src = e.target.result;
    };

    reader.readAsDataURL(file);
}

// xóa phim
function deleteMovie(btn) {

    const movieId = btn.dataset.id;

    if (!confirm("Bạn có chắc muốn xóa phim này?")) {
        return;
    }

    fetch("/admin/movies/delete/" + movieId, {
        method: "POST"
    })
    .then(async response => {

        if (!response.ok) {

            const message = await response.text();

            alert(message);

            throw new Error();
        }

        return response.text();
    })
    .then(() => {

        alert("Xóa thành công");

        loadContent("/admin/movies/content");
    })
    .catch(error => {
        console.log(error);
    });
}

// sửa phim
function openEditMovieModal(btn){

    const movieId = btn.dataset.id;

    fetch("/admin/movies/" + movieId)
    .then(res => res.json())
    .then(movie => {

        document.getElementById("movieId").value = movie.movieId;

        document.getElementById("title").value = movie.title;

        document.getElementById("description").value = movie.description || "";

        document.getElementById("duration").value = movie.duration;

        document.getElementById("releaseDate").value = movie.releaseDate;

        document.getElementById("language").value = movie.language || "";

        document.getElementById("status").value = movie.status;

        document.getElementById("posterPreview").src = movie.posterUrl || "/posters/default.png";

        document.querySelectorAll("input[name='genreIds']").forEach(cb => cb.checked = false);

        movie.genres.forEach(g => {
            const checkbox =document.querySelector(`input[name='genreIds'][value='${g.genreId}']`);
            if(checkbox)checkbox.checked = true;
        });

        openCreateMovieModal();
    });


}

// search phim
function searchMovies() {

    const keyword =document.getElementById("search").value;

    loadContent("/admin/movies/content?keyword=" + encodeURIComponent(keyword));
}


