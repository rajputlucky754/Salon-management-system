/* Sakura Petal Animation — Interactive Canvas */
(function() {
    const canvas = document.getElementById('sakura-canvas');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    let width = canvas.width = window.innerWidth;
    let height = canvas.height = window.innerHeight;

    window.addEventListener('resize', () => {
        width = canvas.width = window.innerWidth;
        height = canvas.height = window.innerHeight;
    });

    const petals = [];
    const numPetals = 100;
    let mouseX = -1000;
    let mouseY = -1000;

    window.addEventListener('mousemove', (e) => {
        mouseX = e.clientX;
        mouseY = e.clientY;
    });

    class Petal {
        constructor() {
            this.x = Math.random() * width;
            this.y = Math.random() * height - height;
            this.size = Math.random() * 12 + 8;
            this.speedY = Math.random() * 1.2 + 0.4;
            this.speedX = Math.random() * 1.5 - 0.75;
            this.angle = Math.random() * 360;
            this.spin = Math.random() * 0.04 - 0.02;
            this.opacity = Math.random() * 0.5 + 0.15;
        }

        update() {
            this.y += this.speedY;
            this.x += this.speedX;
            this.angle += this.spin;

            const dx = mouseX - this.x;
            const dy = mouseY - this.y;
            const dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < 120) {
                const force = (120 - dist) / 120;
                this.x -= (dx / dist) * force * 3;
                this.y -= (dy / dist) * force * 3;
            }

            if (this.y > height + this.size) {
                this.y = -this.size;
                this.x = Math.random() * width;
            }
            if (this.x > width + this.size) this.x = -this.size;
            if (this.x < -this.size) this.x = width + this.size;
        }

        draw() {
            ctx.save();
            ctx.translate(this.x, this.y);
            ctx.rotate(this.angle);
            ctx.fillStyle = `rgba(255, 183, 197, ${this.opacity})`;
            ctx.beginPath();
            ctx.moveTo(0, 0);
            ctx.quadraticCurveTo(this.size * 0.8, -this.size * 0.8, this.size * 1.2, 0);
            ctx.quadraticCurveTo(this.size * 0.8, this.size * 0.8, 0, 0);
            ctx.fill();
            ctx.restore();
        }
    }

    for (let i = 0; i < numPetals; i++) {
        petals.push(new Petal());
    }

    function animate() {
        ctx.clearRect(0, 0, width, height);
        petals.forEach(petal => {
            petal.update();
            petal.draw();
        });
        requestAnimationFrame(animate);
    }

    animate();
})();
