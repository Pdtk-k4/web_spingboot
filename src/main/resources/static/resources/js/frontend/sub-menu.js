const sidebar = document.querySelector('.sidebar-menu');

function toggleSubMenu(a) {
    const submenu = a.nextElementSibling;
    if (submenu) {
        submenu.classList.add('show');
        span.classList.add('rotate');
    }
}

function closeAllSubMenu(except = null) {
    sidebar.querySelectorAll('.show').forEach(submenu => {
        if (submenu !== except) {
            submenu.classList.remove('show');
            submenu.previousElementSibling.classList.remove('rotate');
        }
    });
}

// Hover mở menu
sidebar.querySelectorAll('.has-child').forEach(item => {
    const span = item.querySelector('span');
    const submenu = item.querySelector('ul');

    if (span && submenu) {
        item.addEventListener('mouseenter', () => {
            closeAllSubMenu(submenu);
            toggleSubMenu(span);
        });

        item.addEventListener('mouseleave', () => {
            submenu.classList.remove('show');
            span.classList.remove('rotate');
        });
    }
});
