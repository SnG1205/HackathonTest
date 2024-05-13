

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
