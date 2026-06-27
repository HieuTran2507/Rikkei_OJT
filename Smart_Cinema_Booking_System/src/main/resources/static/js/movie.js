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