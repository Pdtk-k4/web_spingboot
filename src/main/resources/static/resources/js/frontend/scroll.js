const header = document.querySelector('#header');
let lastState = ''; 

window.addEventListener("scroll", function () {
    const x = Math.round(window.scrollY);
    console.log(x);
    if (x > 55 && lastState !== 'fixed2') {
        header.classList.add("fixed2");
        header.classList.remove("fixed1");
        lastState = 'fixed2';
    } else if (x > 3 && x <= 55 && lastState !== 'fixed1') {
        header.classList.add("fixed1");
        header.classList.remove("fixed2");
        lastState = 'fixed1';
    } else if (x < 3 && lastState !== '') {
        header.classList.remove("fixed1", "fixed2");
        lastState = '';
    }
});
