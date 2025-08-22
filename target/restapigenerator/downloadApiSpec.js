function downloadApiSpec() {
    const url = "apiSpecFile";
    const filename = "apiSpec.json";
    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error("Could not download the API spec");
            return response.text();
        })
        .then(text => {
            const blob = new Blob([text], { type: "application/json" });
            const link = document.createElement("a");
            link.href = URL.createObjectURL(blob);
            link.download = filename;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
            URL.revokeObjectURL(link.href);
        })
        .catch(error => {
            alert("Failed to download the API spec: " + error);
        });
}

