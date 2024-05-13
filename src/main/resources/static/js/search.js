document.getElementById('lawyerSearchForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const query = document.getElementById('query').value;

    fetch(`/test?Suchworte=${query}`)
        .then(response => response.json())
        .then(data => {
            const resultsDiv = document.getElementById('results');
            resultsDiv.innerHTML = '';

            const lawyerName = data.name || 'N/A';
            const wonCases = data.wonCases || 0;
            const lostCases = data.lostCases || 0;

            const summary = document.createElement('div');
            summary.innerHTML = `<h3>Lawyer: ${lawyerName}</h3><p>Wins: ${wonCases}, Losses: ${lostCases}</p>`;

            const viewCasesButton = document.createElement('button');
            viewCasesButton.innerHTML = "View Cases";
            viewCasesButton.onclick = function() {
                window.location.href = `/cases.html?name=${encodeURIComponent(lawyerName)}`;
            };

            resultsDiv.appendChild(summary);
            resultsDiv.appendChild(viewCasesButton);
        })
        .catch(() => {
            const resultsDiv = document.getElementById('results');
            resultsDiv.innerHTML = '<p>No results found.</p>';
        });
});
