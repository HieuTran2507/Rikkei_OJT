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