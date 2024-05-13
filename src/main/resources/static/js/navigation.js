function showSection(sectionId) {
    const sections = document.querySelectorAll('section');
    sections.forEach(sec => {
        sec.style.display = 'none';
        sec.style.opacity = 0;
    });

    setTimeout(() => {
        const section = document.getElementById(sectionId);
        section.style.display = 'block';
        setTimeout(() => {
            section.style.opacity = 1;
        }, 10);

        if (sectionId === 'about') {
            document.getElementById('lawyerSearchForm').style.display = 'none';
            document.getElementById('results').style.display = 'none';
            startSlideshow(); // Start the slideshow when "About" section is shown
        } else if (sectionId === 'home') {
            document.getElementById('lawyerSearchForm').style.display = 'block';
            document.getElementById('results').style.display = 'block';
            stopSlideshow(); // Stop the slideshow when "Home" section is shown
        }
    }, 10);
}

// Slideshow functionality
let slideIndex = 0;
let slideshowInterval;

function showSlides() {
    const slides = document.querySelectorAll('.slideshow-text');
    if (slideIndex < slides.length) {
        slides[slideIndex].style.display = 'block';
        slides[slideIndex].classList.add('active');
        slideIndex++;
    }
}

function startSlideshow() {
    const slides = document.querySelectorAll('.slideshow-text');
    slides.forEach(slide => {
        slide.style.display = 'none';
    });
    slideIndex = 0;
    showSlides();
    slideshowInterval = setInterval(showSlides, 3000); // Change slide every 5 seconds
}

function stopSlideshow() {
    clearInterval(slideshowInterval);
}

// Show the home section by default on page load
document.addEventListener('DOMContentLoaded', function() {
    showSection('home');
});
