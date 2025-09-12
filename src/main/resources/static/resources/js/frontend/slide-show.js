let slider = document.querySelector('#home-slider .list');
let items = document.querySelectorAll('#home-slider .list .item');
let next = document.getElementById('next');
let prev = document.getElementById('prev');
let dots = document.querySelectorAll('.owl-dots .owl-dot span');

// Tạo bản sao các mục đầu và cuối
const firstClone = items[0].cloneNode(true);
const lastClone = items[items.length - 1].cloneNode(true);

// Thêm bản sao vào đầu và cuối danh sách
slider.appendChild(firstClone);
slider.insertBefore(lastClone, items[0]);

// Cập nhật danh sách mục sau khi clone
items = document.querySelectorAll('#home-slider .list .item');

let active = 1; // Bắt đầu từ mục thứ hai (vì mục đầu tiên đã được nhân bản)
const lengthItems = items.length - 2; // Loại bỏ các mục nhân bản khỏi tính toán

// Cập nhật vị trí slider khởi đầu
slider.style.transform = `translateX(-${items[active].offsetLeft}px)`;

// Tự động chuyển ảnh
let refreshInterval = null;
function startAutoSlide() {
    refreshInterval = setInterval(() => next.click(), 6000);
}
function stopAutoSlide() {
    clearInterval(refreshInterval);
}

// Hàm di chuyển slider
function reloadSlider() {
    slider.style.transition = 'transform 1s ease-in-out'; // Hiệu ứng chuyển động
    slider.style.transform = `translateX(-${items[active].offsetLeft}px)`;

    // Cập nhật trạng thái dot
    document.querySelector('.owl-dots .owl-dot span.active')?.classList.remove('active');
    if (active === 0) {
        dots[lengthItems - 1].classList.add('active'); // Dot của ảnh cuối
    } else if (active === lengthItems + 1) {
        dots[0].classList.add('active'); // Dot của ảnh đầu
    } else {
        dots[active - 1].classList.add('active');
    }

    // Xử lý sự kiện `transitionend`
    const handleTransitionEnd = () => {
        if (active === 0) {
            slider.style.transition = 'none'; // Tắt hiệu ứng để tránh nhấp nháy
            active = lengthItems;
            slider.style.transform = `translateX(-${items[active].offsetLeft}px)`;
        }
        if (active === lengthItems + 1) {
            slider.style.transition = 'none';
            active = 1;
            slider.style.transform = `translateX(-${items[active].offsetLeft}px)`;
        }

        // Xóa sự kiện sau khi hoàn thành
        slider.removeEventListener('transitionend', handleTransitionEnd);
    };
    slider.addEventListener('transitionend', handleTransitionEnd);
}

// Debounce để ngăn việc nhấn nút liên tiếp quá nhanh
let debounce = false;

// Sự kiện cho nút "Next"
next.onclick = function () {
    if (!debounce) {
        debounce = true;
        stopAutoSlide();
        if (active < items.length - 1) {
            active++;
            reloadSlider();
        }
        startAutoSlide();
        setTimeout(() => (debounce = false), 1000); // Thời gian debounce bằng với `transition`
    }
};

// Sự kiện cho nút "Prev"
prev.onclick = function () {
    if (!debounce) {
        debounce = true;
        stopAutoSlide();
        if (active > 0) {
            active--;
            reloadSlider();
        }
        startAutoSlide();
        setTimeout(() => (debounce = false), 1000);
    }
};

// Sự kiện cho dots
dots.forEach((dot, key) => {
    dot.addEventListener('click', () => {
        stopAutoSlide();
        active = key + 1; // Vì có thêm mục nhân bản
        reloadSlider();
        startAutoSlide();
    });
});

// Làm mới slider khi thay đổi kích thước màn hình
window.onresize = function () {
    slider.style.transform = `translateX(-${items[active].offsetLeft}px)`;
};

// Bắt đầu tự động chuyển slide khi trang tải xong
document.addEventListener('DOMContentLoaded', () => {
    startAutoSlide();
});
