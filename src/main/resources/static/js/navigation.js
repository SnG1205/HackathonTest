// Funktionen zur Navigation
function showHome() {
    const homeSection = document.getElementById('home');
    const aboutSection = document.getElementById('about');
    homeSection.style.opacity = 0;
    aboutSection.style.opacity = 0;
    setTimeout(() => {
        homeSection.style.opacity = 1;
        aboutSection.style.display = 'none';
        homeSection.style.display = 'block';
    }, 10);
}

function showAbout() {
    const homeSection = document.getElementById('home');
    const aboutSection = document.getElementById('about');
    const searchForm = document.getElementById('lawyerSearchForm');
    const resultsContainer = document.getElementById('results');

    // Elemente ausblenden
    homeSection.style.opacity = 0;
    searchForm.style.display = 'none';
    resultsContainer.style.display = 'none';

    // Überprüfe, ob die Suchergebnisse vorhanden sind, und setze das Ergebnisfeld zurück
    if (resultsContainer.firstChild) {
        resultsContainer.innerHTML = '';
    }

    // Text für "About"-Abschnitt anzeigen
    aboutSection.style.opacity = 0;
    setTimeout(() => {
        aboutSection.style.opacity = 1;
        homeSection.style.display = 'none';
        aboutSection.style.display = 'block';
    }, 10);
}

// Zentrale Funktion zur Anzeige der Abschnitte
function showSection(sectionId) {
    const sections = document.querySelectorAll('section');
    sections.forEach(sec => {
        sec.style.display = 'none'; // Alle Sektionen verstecken
        sec.style.opacity = 0; // Alle Sektionen ausblenden
    });

    setTimeout(() => {
        const section = document.getElementById(sectionId);
        section.style.display = 'block'; // Nur die gewählte Sektion anzeigen
        setTimeout(() => {
            section.style.opacity = 1;
        }, 10);
    }, 10);
}
