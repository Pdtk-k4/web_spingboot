const imageGG_SignIn = document.getElementById('dynamic-image-gg-sign-in');

imageGG_SignIn.addEventListener('mouseover', function () {
    imageGG_SignIn.querySelector('img').src = '/resources/images/frontend/ic_google_light.png';
});

imageGG_SignIn.addEventListener('mouseout', function () {
    imageGG_SignIn.querySelector('img').src = '/resources/images/frontend/ic_google_dark.png';
});
