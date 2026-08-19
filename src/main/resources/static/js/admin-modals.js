/**
 * admin-modals.js
 * Vanilla JS to handle custom Tailwind CSS modals.
 */
document.addEventListener('DOMContentLoaded', () => {
    // Open Modals
    const modalTriggers = document.querySelectorAll('[data-modal-target]');
    modalTriggers.forEach(trigger => {
        trigger.addEventListener('click', (e) => {
            e.preventDefault();
            const targetId = trigger.getAttribute('data-modal-target');
            const modal = document.getElementById(targetId);
            if (modal) {
                modal.classList.remove('hidden');
                modal.classList.add('flex');
                // Optional animation trigger could go here
                setTimeout(() => {
                    modal.querySelector('.modal-content').classList.remove('scale-95', 'opacity-0');
                    modal.querySelector('.modal-content').classList.add('scale-100', 'opacity-100');
                }, 10);
            }
        });
    });

    // Close Modals
    const closeTriggers = document.querySelectorAll('[data-modal-close]');
    closeTriggers.forEach(trigger => {
        trigger.addEventListener('click', (e) => {
            e.preventDefault();
            const modal = trigger.closest('.sakura-modal');
            if (modal) {
                modal.querySelector('.modal-content').classList.remove('scale-100', 'opacity-100');
                modal.querySelector('.modal-content').classList.add('scale-95', 'opacity-0');
                setTimeout(() => {
                    modal.classList.remove('flex');
                    modal.classList.add('hidden');
                }, 200); // Wait for transition
            }
        });
    });

    // Close on overlay click
    const modals = document.querySelectorAll('.sakura-modal');
    modals.forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.querySelector('.modal-content').classList.remove('scale-100', 'opacity-100');
                modal.querySelector('.modal-content').classList.add('scale-95', 'opacity-0');
                setTimeout(() => {
                    modal.classList.remove('flex');
                    modal.classList.add('hidden');
                }, 200);
            }
        });
    });
});
