function downloadFile(url, filename) {
     fetch(url)
        .then(response => {
            if (!response.ok) throw new Error(filename);
            return response.blob();
        })
        .then(blob => {
            const link = document.createElement("a");
            link.href = URL.createObjectURL(blob);
            link.download = filename;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(link.href);
        })
        .catch(error => {
            alert("Download failed: " + error);
        });
}

