document.getElementById('lawyerSearchForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const query = document.getElementById('query').value;

    fetch(`/test?Suchworte=${query}`)
        .then(response => response.json())
        .then(data => {
            const resultsDiv = document.getElementById('results');
            resultsDiv.innerHTML = '';

            if (data.length > 0) {
                const lawyerName = data[0].attorney.name || 'N/A';
                const wonCases = data.reduce((acc, curr) => acc + curr.attorney.wonCases, 0);
                const lostCases = data.reduce((acc, curr) => acc + curr.attorney.lostCases, 0);

                const summary = document.createElement('div');
                summary.innerHTML = `<h3>Lawyer: ${lawyerName}</h3><p>Wins: ${wonCases}, Losses: ${lostCases}</p>`;
                resultsDiv.appendChild(summary);

                data.forEach(item => {
                    const result = document.createElement('div');
                    result.innerHTML = `<p>Name: ${item.attorney.name || 'N/A'}, Wins: ${item.attorney.wonCases}, Losses: ${item.attorney.lostCases}</p><p>Kopf: ${item.kopf}</p><p>Spruch: ${item.spruch}</p>`;
                    resultsDiv.appendChild(result);
                });
            } else {
                resultsDiv.innerHTML = '<p>No results found.</p>';
            }
        })
        .catch(error => console.error('Error fetching data:', error));
});
