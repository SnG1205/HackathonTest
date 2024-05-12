document.getElementById('lawyerSearchForm').addEventListener('submit', function (e) {
    e.preventDefault();
    const query = document.getElementById('query').value;

    fetch(`/test?Suchworte='${query}'`)
        .then(response => response.json())
        .then(data => {
            const resultsDiv = document.getElementById('results');
            resultsDiv.innerHTML = '';

            const lawyerName = data.name || 'N/A';
            const wonCases = data.wonCases//data.reduce((acc, curr) => acc + curr.wonCases, 0);
            const lostCases = data.lostCases//data.reduce((acc, curr) => acc + curr.lostCases, 0);

            const summary = document.createElement('div');
            summary.innerHTML = `<h3>Lawyer: ${lawyerName}</h3><p>Wins: ${wonCases}, Losses: ${lostCases}</p>`;
            resultsDiv.appendChild(summary);
        })
        .catch(() => {
            const resultsDiv = document.getElementById('results');
            resultsDiv.innerHTML = '<p>No results found.</p>';
        });
});
