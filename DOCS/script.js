// Scroll Animations
document.addEventListener('DOMContentLoaded', () => {
    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.1
    };

    const observer = new IntersectionObserver((entries, observer) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    const fadeElements = document.querySelectorAll('.fade-in, .fade-in-up');
    fadeElements.forEach(el => observer.observe(el));
});

// Spectrum Canvas Background
const canvas = document.getElementById('spectrumCanvas');
const ctx = canvas.getContext('2d');

let width, height;
let particles = [];

function resizeCanvas() {
    width = window.innerWidth;
    height = window.innerHeight;
    canvas.width = width;
    canvas.height = height;
}

window.addEventListener('resize', resizeCanvas);
resizeCanvas();

class Particle {
    constructor() {
        this.x = Math.random() * width;
        this.y = height + Math.random() * 200;
        this.size = Math.random() * 2 + 1;
        this.speedY = Math.random() * 1 + 0.5;
        this.speedX = (Math.random() - 0.5) * 0.5;
        // SynthLens Colors
        const colors = ['rgba(0, 240, 255, 0.4)', 'rgba(255, 0, 85, 0.4)', 'rgba(255, 176, 0, 0.4)', 'rgba(0, 255, 102, 0.4)'];
        this.color = colors[Math.floor(Math.random() * colors.length)];
        this.sinValue = Math.random() * Math.PI * 2;
    }

    update() {
        this.y -= this.speedY;
        this.sinValue += 0.02;
        this.x += Math.sin(this.sinValue) * 0.5 + this.speedX;

        if (this.y < -50) {
            this.y = height + 50;
            this.x = Math.random() * width;
        }
    }

    draw() {
        ctx.beginPath();
        ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
        ctx.fillStyle = this.color;
        ctx.fill();
        
        // Glow effect
        ctx.shadowBlur = 10;
        ctx.shadowColor = this.color;
    }
}

// Create particles
for (let i = 0; i < 50; i++) {
    particles.push(new Particle());
}

// Spectrum wave
let time = 0;

function animate() {
    ctx.clearRect(0, 0, width, height);

    // Draw particles
    particles.forEach(p => {
        p.update();
        p.draw();
    });

    // Draw spectrum wave at the bottom
    ctx.shadowBlur = 15;
    ctx.shadowColor = 'rgba(0, 240, 255, 0.5)';
    
    ctx.beginPath();
    ctx.moveTo(0, height);
    
    for (let x = 0; x <= width; x += 10) {
        // Complex wave function simulating audio signal
        const noise = Math.sin(x * 0.05 + time) * 10;
        const mainWave = Math.sin(x * 0.01 - time * 0.5) * 50;
        const secondaryWave = Math.cos(x * 0.02 + time * 0.8) * 30;
        
        // Attenuate at edges
        const distanceFromCenter = Math.abs(x - width / 2) / (width / 2);
        const attenuation = Math.max(0, 1 - Math.pow(distanceFromCenter, 2));
        
        const y = height - 50 + (noise + mainWave + secondaryWave) * attenuation;
        
        ctx.lineTo(x, y);
    }
    
    ctx.lineTo(width, height);
    ctx.lineTo(0, height);
    
    const gradient = ctx.createLinearGradient(0, height - 150, 0, height);
    gradient.addColorStop(0, 'rgba(0, 240, 255, 0.0)');
    gradient.addColorStop(1, 'rgba(0, 240, 255, 0.2)');
    
    ctx.fillStyle = gradient;
    ctx.fill();
    
    ctx.strokeStyle = 'rgba(0, 240, 255, 0.8)';
    ctx.lineWidth = 2;
    ctx.stroke();
    
    time += 0.05;

    requestAnimationFrame(animate);
}

animate();
