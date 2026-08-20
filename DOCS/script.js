// 1. Scroll Animations (Intersection Observer)
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
                
                // Trigger counter animation if it's the counter card
                const counter = entry.target.querySelector('.counter-number');
                if (counter && !counter.classList.contains('counted')) {
                    animateCounter(counter);
                    counter.classList.add('counted');
                }
                
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    const fadeElements = document.querySelectorAll('.fade-in, .fade-in-up, .bento-card');
    fadeElements.forEach(el => observer.observe(el));

    // Magnetic Hover Effect
    const magneticElements = document.querySelectorAll('.hover-magnetic');
    magneticElements.forEach(el => {
        el.addEventListener('mousemove', (e) => {
            const rect = el.getBoundingClientRect();
            const x = e.clientX - rect.left; // x position within the element.
            const y = e.clientY - rect.top;  // y position within the element.
            
            const centerX = rect.width / 2;
            const centerY = rect.height / 2;
            
            const deltaX = (x - centerX) / 10;
            const deltaY = (y - centerY) / 10;
            
            el.style.transform = `translate(${deltaX}px, ${deltaY}px)`;
        });
        
        el.addEventListener('mouseleave', () => {
            el.style.transform = `translate(0px, 0px)`;
        });
    });
});

// 2. Counter Animation
function animateCounter(el) {
    const target = parseInt(el.getAttribute('data-target'));
    const duration = 2000; // 2 seconds
    const stepTime = Math.abs(Math.floor(duration / target));
    let current = 0;
    
    // Fast increment strategy
    const increment = target / 60; // 60 FPS roughly
    
    const timer = setInterval(() => {
        current += increment;
        if (current >= target) {
            el.innerText = target.toLocaleString() + "+";
            clearInterval(timer);
        } else {
            el.innerText = Math.floor(current).toLocaleString();
        }
    }, 1000 / 60);
}

// 3. Advanced Spectrum Canvas Background
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
        this.reset();
        this.y = Math.random() * height; // initial random spread
    }
    
    reset() {
        this.x = Math.random() * width;
        this.y = height + 50;
        this.size = Math.random() * 2 + 0.5;
        this.speedY = Math.random() * 2 + 0.5;
        this.speedX = (Math.random() - 0.5) * 1;
        const colors = ['rgba(0, 240, 255, 0.5)', 'rgba(255, 0, 85, 0.3)', 'rgba(255, 176, 0, 0.4)'];
        this.color = colors[Math.floor(Math.random() * colors.length)];
        this.sinValue = Math.random() * Math.PI * 2;
    }

    update() {
        this.y -= this.speedY;
        this.sinValue += 0.02;
        this.x += Math.sin(this.sinValue) * 0.8 + this.speedX;

        if (this.y < -50) {
            this.reset();
        }
    }

    draw() {
        ctx.beginPath();
        ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
        ctx.fillStyle = this.color;
        ctx.fill();
        ctx.shadowBlur = 15;
        ctx.shadowColor = this.color;
    }
}

for (let i = 0; i < 80; i++) {
    particles.push(new Particle());
}

let time = 0;

function drawWave(yOffset, color, speed, amp1, freq1, amp2, freq2) {
    ctx.beginPath();
    ctx.moveTo(0, height);
    
    for (let x = 0; x <= width; x += 15) {
        const t = time * speed;
        const wave1 = Math.sin(x * freq1 + t) * amp1;
        const wave2 = Math.cos(x * freq2 - t * 0.8) * amp2;
        
        // Attenuate at edges
        const distanceFromCenter = Math.abs(x - width / 2) / (width / 2);
        const attenuation = Math.max(0, 1 - Math.pow(distanceFromCenter, 2));
        
        const y = height - yOffset + (wave1 + wave2) * attenuation;
        ctx.lineTo(x, y);
    }
    ctx.lineTo(width, height);
    ctx.lineTo(0, height);
    
    ctx.fillStyle = color;
    ctx.fill();
}

function animate() {
    ctx.clearRect(0, 0, width, height);

    // Draw particles
    particles.forEach(p => {
        p.update();
        p.draw();
    });

    // Reset shadow for waves
    ctx.shadowBlur = 0;

    // Deep background wave (Magenta-ish)
    drawWave(40, 'rgba(255, 0, 85, 0.05)', 0.8, 60, 0.005, 40, 0.01);
    
    // Mid wave (Cyan-ish)
    drawWave(60, 'rgba(0, 240, 255, 0.1)', 1.2, 40, 0.01, 30, 0.02);
    
    // Front wave (Cyan bright line)
    ctx.beginPath();
    ctx.moveTo(0, height);
    for (let x = 0; x <= width; x += 10) {
        const t = time * 1.5;
        const noise = Math.sin(x * 0.05 + t) * 15;
        const mainWave = Math.sin(x * 0.015 - t * 0.5) * 50;
        
        const distanceFromCenter = Math.abs(x - width / 2) / (width / 2);
        const attenuation = Math.max(0.1, 1 - Math.pow(distanceFromCenter, 2));
        
        const y = height - 80 + (noise + mainWave) * attenuation;
        ctx.lineTo(x, y);
    }
    ctx.strokeStyle = 'rgba(0, 240, 255, 0.8)';
    ctx.lineWidth = 2;
    ctx.shadowBlur = 15;
    ctx.shadowColor = 'rgba(0, 240, 255, 0.8)';
    ctx.stroke();

    time += 0.03;
    requestAnimationFrame(animate);
}

animate();
