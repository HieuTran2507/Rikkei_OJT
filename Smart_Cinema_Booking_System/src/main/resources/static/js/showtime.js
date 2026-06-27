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