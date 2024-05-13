document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const lawyerName = urlParams.get('name');
    const lawyerNameSpan = document.getElementById('lawyerName');
    const caseList = document.getElementById('caseList');

    if (lawyerName) {
        lawyerNameSpan.textContent = lawyerName;

        fetch(`/cases?name=${encodeURIComponent(lawyerName)}`)
            .then(response => response.json())
            .then(data => {
                caseList.innerHTML = '';
                if (data.length > 0) {
                    data.forEach(link => {
                        const listItem = document.createElement('li');
                        listItem.innerHTML = `<a href="${link}" target="_blank">${link}</a>`;
                        caseList.appendChild(listItem);
                    });
                } else {
                    caseList.innerHTML = '<li>No cases found.</li>';
                }
            })
            .catch(() => {
                caseList.innerHTML = '<li>Error fetching cases.</li>';
            });
    } else {
        caseList.innerHTML = '<li>No lawyer specified.</li>';
    }
});
