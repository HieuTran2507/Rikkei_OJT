function loadContent(url) {
    console.log(url);
    fetch(url)
        .then(response => response.text())
        .then(html => {
            document.getElementById("main-content").innerHTML = html;
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
        document.getElementById("emailError").innerText = errors.email;
    }

    if(errors.fullName){
        document.getElementById("fullNameError").innerText = errors.fullName;
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
function goToPage(page, mode){

    const keyword = document.getElementById("search").value;
   if (mode === "movies") {
   loadContent(
           "/admin/movies/content?page="
           + page
           + "&keyword="
           + encodeURIComponent(keyword)
       );
   }

   if (mode === "showtimes") {
      loadContent(
              "/admin/showtimes/content?page="
              + page
              + "&keyword="
              + encodeURIComponent(keyword)
          );
      }

}
function setActiveMenu(element){

    document
        .querySelectorAll(".sidebar-link")
        .forEach(link => link.classList.remove("active"));

    element.classList.add("active");

    localStorage.setItem("activeMenu",element.dataset.menu);
}
window.addEventListener("DOMContentLoaded", () => {

    const activeMenu = localStorage.getItem("activeMenu");

    console.log(activeMenu);

    if(activeMenu){

        const menu = document.querySelector(`[data-menu="${activeMenu}"]`);

        if(menu) menu.classList.add("active");

    }else{

        // mặc định sáng Trang chủ
        document.querySelector('[data-menu="dashboard"]')?.classList.add("active");
    }
});
//------------------- THÔNG TIN NGƯỜI DÙNG -----------------------//
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

//------------------- QUẢN LÝ PHIM -----------------------//
// Thêm phim (dùng chung modal cho sửa phim)
function openCreateMovieModal() {
    document.getElementById("movieModalTitle").innerText = "Thêm phim mới";
    document.getElementById("createMovieModal").classList.remove("hidden");
}
function closeCreateMovieModal() {
    document.getElementById("createMovieModal").classList.add("hidden");

    document.getElementById("posterPreview").src = "/posters/default.png";

    document.getElementById("movieId").value = "";

    document.getElementById("movieModalTitle").innerText = "";

    document.getElementById("createMovieForm").reset();
    clearErrors();
}
function showMovieErrors(errors) {

    clearErrors();

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

    document.getElementById("movieModalTitle").innerText = "Chỉnh sửa thông tin phim";

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

        document.getElementById("createMovieModal").classList.remove("hidden");
    });


}

// search phim
function searchMovies() {

    const keyword =document.getElementById("search").value;

    loadContent("/admin/movies/content?keyword=" + encodeURIComponent(keyword));
}

//------------------- QUẢN LÝ XUẤT CHIẾU -----------------------//
// thêm xuất chiếu
function openCreateShowtimeModal(){

    document.getElementById("showtimeModalTitle").innerText = "Thêm xuất chiếu";

    document.getElementById("createShowtimeModal").classList.remove("hidden");
}
function closeCreateShowtimeModal(){

    document.getElementById("createShowtimeModal").classList.add("hidden");
    document.getElementById("showtimeForm").reset();
    clearErrors();
}
function showShowtimeErrors(errors) {
    clearErrors();

    if (errors.movieId)
            document.getElementById("movieError").innerText = errors.movieId;

        if (errors.roomId)
            document.getElementById("roomError").innerText = errors.roomId;

        if (errors.startTime)
            document.getElementById("startTimeError").innerText = errors.startTime;

        if (errors.endTime)
            document.getElementById("endTimeError").innerText = errors.endTime;

        if (errors.ticketPrice)
            document.getElementById("ticketPriceError").innerText = errors.ticketPrice;

        if (errors.status)
            document.getElementById("statusError").innerText = errors.status;
}
function saveShowtime() {
    const formData = new FormData();
    const showtimeId = document.getElementById("showtimeId").value;

    formData.append("showtimeId",showtimeId);
    formData.append("movieId", document.getElementById("movieId").value);
    formData.append("roomId", document.getElementById("roomId").value);
    formData.append("startTime", document.getElementById("startTime").value);
    formData.append("endTime", document.getElementById("endTime").value);
    formData.append("status", document.getElementById("showtimeStatus").value);
    formData.append("ticketPrice", document.getElementById("ticketPrice").value);

    const url = showtimeId ? "/admin/showtimes/update" : "/admin/showtimes/create";

    fetch(url, {
        method: "POST",
        body: formData
    })
    .then(async res => {
        if (!res.ok) {
            const errors = await res.json();
            showShowtimeErrors(errors);
            throw new Error("Validation error");
        }
        return res.text();
    })
    .then(() => {
        closeCreateShowtimeModal();
        // reload list giống profile
       loadContent("/admin/showtimes/content");
    })
    .catch(err => {
        console.log(err);
    });
}

// tính endtime
function calculateEndTime() {

    const movie = document.getElementById("movieId");
    const start = document.getElementById("startTime").value;

    if (!movie.value || !start) {
        document.getElementById("endTime").value = "";
        return;
    }

    const duration = parseInt(movie.options[movie.selectedIndex].dataset.duration);

    const startDate = new Date(start);

    startDate.setMinutes(startDate.getMinutes() + duration);

    const yyyy = startDate.getFullYear();
    const MM = String(startDate.getMonth() + 1).padStart(2,"0");
    const dd = String(startDate.getDate()).padStart(2,"0");
    const hh = String(startDate.getHours()).padStart(2,"0");
    const mm = String(startDate.getMinutes()).padStart(2,"0");

    document.getElementById("endTime").value = `${yyyy}-${MM}-${dd}T${hh}:${mm}`;
}

// xóa xuất chiếu
function deleteShowtime(btn) {

    const showtimeId = btn.dataset.id;

    if (!confirm("Bạn có chắc muốn xóa xuất chiếu này?")) {
        return;
    }

    fetch("/admin/showtimes/delete/" + showtimeId, {
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

        loadContent("/admin/showtimes/content");
    })
    .catch(error => {
        console.log(error);
    });
}

// sửa xuất chiếu
function openEditShowtimeModal(btn){

    document.getElementById("showtimeModalTitle").innerText = "Chỉnh sửa thông tin xuất chiếu";

    const showtimeId = btn.dataset.id;

    fetch("/admin/showtimes/" + showtimeId)
    .then(res => res.json())
    .then(showtime => {

        document.getElementById("showtimeId").value = showtime.showtimeId;

        document.getElementById("movieId").value = showtime.movieId;

        document.getElementById("roomId").value = showtime.roomId;

        document.getElementById("startTime").value = showtime.startTime;

        document.getElementById("endTime").value = showtime.endTime;

        document.getElementById("showtimeStatus").value = showtime.status;

        document.getElementById("ticketPrice").value = showtime.ticketPrice;

        document.getElementById("createShowtimeModal").classList.remove("hidden");
    });


}

// search xuất chiếu bằng tên phòng và tên phim
function searchShowtimes() {

    const keyword = document.getElementById("search").value;

    loadContent("/admin/showtimes/content?keyword=" + encodeURIComponent(keyword));
}


