const imageContainer = document.getElementById('dynamic-image');
const image = imageContainer.querySelector('img');


imageContainer.addEventListener('mouseover', function () {
    image.src = '/resources/images/frontend/logo_shopee_light.png';
});

imageContainer.addEventListener('mouseout', function () {
    image.src = '/resources/images/frontend/logo_shopee_dark.png';
});

