document.addEventListener("DOMContentLoaded", function() {
    document.getElementById('lawyerSearchForm').addEventListener('submit', function(event) {
        event.preventDefault();
        const searchQuery = document.getElementById('query').value;
        fetch(`/test?Suchworte=${encodeURIComponent(searchQuery)}`)
            .then(response => response.json())
            .then(data => {
                const resultsContainer = document.getElementById('results');
                resultsContainer.innerHTML = '';
                data.forEach(item => {
                    const div = document.createElement('div');
                    const name = item.attorney ? item.attorney.name : "Unbekannt";
                    const wins = item.kopf || "Keine Daten";
                    const losses = item.spruch || "Keine Daten";
                    div.textContent = `Name: ${name}, Wins: ${wins}, Losses: ${losses}`;
                    resultsContainer.appendChild(div);
                });
            })
            .catch(error => {
                console.error('Fehler beim Abrufen der Daten:', error);
                alert('Es gab ein Problem mit der API-Anfrage. Bitte überprüfe die Konsole für mehr Details.');
            });
    });
});
