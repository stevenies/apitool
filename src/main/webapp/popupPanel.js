const GAP = 12; // between click and popup
const PAD = 8;  // viewport padding

const contentByKey = {
    regionA: `
        The first step in the API design process is to create a business domain model using the
        <a href="https://staruml.io/" target="StarUML">StarUML</a> modeling tool.  A
        <a onclick="return openWindow('overviewDomainModel.html', 'DomainModel');">business domain model</a>
        is a conceptual blueprint of an organization's problem space identifying the key business concepts
        (entities), their attributes, and their relationships to other entities.  It provides a shared
        vocabulary for business stakeholders and technical teams to bridge the gap between business
        requirements and technical implementation.  The business domain model is a key design artifact
        for defining the API's client interface specification from a business perspective.
    `,
    regionB: `
        Once the business domain model has been created the second step is to import it into the REST API Generator.  The tool
        analyzes the domain model's entities, attributes, and relationships and then automatically generates a corresponding API
        client interface specification.  The result is a well-structured OpenAPI specification that defines the API's endpoints,
        request/response formats, status codes, and domain model schemas.  The generated specification adheres to industry best
        practices for API design ensuring that the API's interface is easy to understand and use by clients.
    `,
    regionC: `
        The final step is to import the API's client interface specification into the REST API Generator to
        automatically generate code implementing the API's skeleton infrastructure. The skeleton implementation
        is a fully functional API that can receive requests from the client and return default responses but contains
        stubs for the business logic. The API skeleton code provides API developers with an initial "quick start"
        code base, thus significantly reducing the time and effort required to develop a REST API. 
    `
};

function hidePopup() {
    popup.style.display = 'none';
    popup.setAttribute('aria-hidden', 'true');
}

function showPopup(e) {
    e.preventDefault();
    const key = e.target.dataset.key;
    const html = contentByKey[key] ?? `<div>No content for ${key}</div>`;
    showPopupAt(e.clientX, e.clientY, html);
    return false; 
}

function showPopupAt(clientX, clientY, html) {
    console.log("clientX:", clientX, " clientY:", clientY);
    popup.innerHTML = html;
    popup.style.display = 'block';
    popup.setAttribute('aria-hidden', 'false');

    // measure after display:block
    const pw = popup.offsetWidth;
    const ph = popup.offsetHeight;

    // prefer right; flip to left if it won't fit
    let left = clientX + GAP;
    if (left + pw + PAD > window.innerWidth) {
        left = clientX - GAP - pw;
    }
    left = Math.max(PAD, Math.min(left, window.innerWidth - pw - PAD));

    // keep vertically within viewport
    let top = clientY;
    if (top + ph + PAD > window.innerHeight) {
        top = window.innerHeight - ph - PAD;
    }
    top = Math.max(PAD, top);

    popup.style.left = left + 'px';
    popup.style.top  = top + 'px';
}

window.onload = function() {

    const popup = document.getElementById('popup');

    // Close on outside click
    document.addEventListener('mousedown', (e) => {
        if (popup.style.display === 'block' && !popup.contains(e.target)) hidePopup();
    });
    popup.addEventListener('mousedown', (e) => {e.stopPropagation();});

    // Optional: hide popup on scroll/resize so it doesn't drift
    window.addEventListener('scroll', hidePopup, true);
    window.addEventListener('resize', hidePopup);
};
