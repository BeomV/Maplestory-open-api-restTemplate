function submitForm() {
    document.getElementById('mapleStoryForm').submit();
}
window.onload = function() {
    var poGrades = document.getElementsByClassName("po_grade");
        for (var i = 0; i < poGrades.length; i++) {
        if (poGrades[i].textContent === "에픽") {
            poGrades[i].style.color = "purple";
        }
        if (poGrades[i].textContent === "유니크") {
            poGrades[i].style.color = "orange";
        }
        if (poGrades[i].textContent === "에픽") {
            poGrades[i].style.color = "purple";
        }
    }
}

window.onload = function (){
    var stars = document.getElementsByClassName("star_force");

    for (var i = 0; i < stars.length; i++) {
        if (stars[i].textContent == null || stars[i].textContent.trim() === '') {
            stars[i].style.display = "none";
        }
    }


}

