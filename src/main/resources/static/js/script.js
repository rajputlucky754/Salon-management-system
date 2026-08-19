document.addEventListener("DOMContentLoaded", function () {

    // =========================
    // Fade-in on load
    // =========================
    const animatedElements = document.querySelectorAll(
        ".review-card, .stat-card, .empty-box, .footer-cta, .about-box, .feature-box, .contact-box, .table-responsive"
    );

    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (entry.isIntersecting) {
                entry.target.classList.add("show-element");
            }
        });
    }, {
        threshold: 0.15
    });

    animatedElements.forEach((el) => {
        el.classList.add("hidden-element");
        observer.observe(el);
    });

    // =========================
    // Button click effect
    // =========================
    const buttons = document.querySelectorAll(".btn");

    buttons.forEach((button) => {
        button.addEventListener("click", function () {
            this.classList.add("btn-clicked");

            setTimeout(() => {
                this.classList.remove("btn-clicked");
            }, 180);
        });
    });

    // =========================
    // Auto focus first input on forms
    // =========================
    const firstInput = document.querySelector("form input, form select, form textarea");
    if (firstInput) {
        firstInput.focus();
    }

    // =========================
    // Confirm delete actions
    // =========================
    const deleteLinks = document.querySelectorAll(
        'a[href*="/delete/"], a[href*="/reject/"]'
    );

    deleteLinks.forEach((link) => {
        link.addEventListener("click", function (event) {
            const confirmAction = confirm("Are you sure you want to continue?");
            if (!confirmAction) {
                event.preventDefault();
            }
        });
    });

    // =========================
    // Success alert for booking success page
    // =========================
    const successTitle = document.querySelector(".hero-title");
    if (successTitle && successTitle.textContent.toLowerCase().includes("successful")) {
        console.log("Booking success page loaded.");
    }

    // =========================
    // Simple form validation polish
    // =========================
    const forms = document.querySelectorAll("form");

    forms.forEach((form) => {
        form.addEventListener("submit", function (event) {
            const requiredFields = form.querySelectorAll("[required]");
            let isValid = true;

            requiredFields.forEach((field) => {
                if (field.value.trim() === "") {
                    isValid = false;
                    field.classList.add("is-invalid");
                } else {
                    field.classList.remove("is-invalid");
                }
            });

            if (!isValid) {
                event.preventDefault();
                alert("Please fill in all required fields.");
            }
        });
    });

    // =========================
    // Remove invalid class on typing
    // =========================
    const inputs = document.querySelectorAll("input, textarea, select");

    inputs.forEach((input) => {
        input.addEventListener("input", function () {
            if (this.value.trim() !== "") {
                this.classList.remove("is-invalid");
            }
        });
    });

});