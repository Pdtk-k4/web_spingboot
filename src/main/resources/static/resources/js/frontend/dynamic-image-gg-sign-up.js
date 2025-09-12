const imageGG_SignUp = document.getElementById('dynamic-image-gg-sign-up');

imageGG_SignUp.addEventListener('mouseover', function () {
    imageGG_SignUp.querySelector('img').src = '/resources/images/frontend/ic_google_light.png';
});

imageGG_SignUp.addEventListener('mouseout', function () {
    imageGG_SignUp.querySelector('img').src = '/resources/images/frontend/ic_google_dark.png';
});