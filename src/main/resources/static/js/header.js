// Script para manejar el header semi transparente al hacer scroll
document.addEventListener('DOMContentLoaded', function() {
    const header = document.querySelector('header');
    let lastScrollTop = 0;

    function handleScroll() {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

        // Si se ha hecho scroll hacia abajo más de 50px, agregar clase scrolled
        if (scrollTop > 50) {
            header.classList.add('scrolled');
        } else {
            header.classList.remove('scrolled');
        }

        lastScrollTop = scrollTop;
    }

    // Usar requestAnimationFrame para optimizar el rendimiento
    let ticking = false;

    window.addEventListener('scroll', function() {
        if (!ticking) {
            requestAnimationFrame(function() {
                handleScroll();
                ticking = false;
            });
            ticking = true;
        }
    });

    // Verificar scroll inicial en caso de que la página ya esté scrolleada
    handleScroll();
});
