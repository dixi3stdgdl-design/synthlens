// 1. Mouse Spotlight Tracker
document.addEventListener('mousemove', (e) => {
    // Update CSS variables for the spotlight gradient
    document.body.style.setProperty('--mouse-x', `${e.clientX}px`);
    document.body.style.setProperty('--mouse-y', `${e.clientY}px`);
});

// 2. Scroll Animations (Intersection Observer)
document.addEventListener('DOMContentLoaded', () => {
    const observerOptions = {
        root: null,
        rootMargin: '0px',
        threshold: 0.15
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

    const fadeElements = document.querySelectorAll('.fade-in, .fade-in-up');
    fadeElements.forEach(el => observer.observe(el));

    // 3. Magnetic Hover Effect (Smoother)
    const magneticElements = document.querySelectorAll('.hover-magnetic');
    magneticElements.forEach(el => {
        el.addEventListener('mousemove', (e) => {
            const rect = el.getBoundingClientRect();
            const x = e.clientX - rect.left; 
            const y = e.clientY - rect.top;  
            
            const centerX = rect.width / 2;
            const centerY = rect.height / 2;
            
            // Calculate distance from center (divided by a factor for subtlety)
            const deltaX = (x - centerX) / 12;
            const deltaY = (y - centerY) / 12;
            
            el.style.transform = `translate(${deltaX}px, ${deltaY}px) scale(1.02)`;
        });
        
        el.addEventListener('mouseleave', () => {
            el.style.transform = `translate(0px, 0px) scale(1)`;
        });
    });
});

// 4. Counter Animation
function animateCounter(el) {
    const target = parseInt(el.getAttribute('data-target'));
    const duration = 2500; // 2.5 seconds
    let current = 0;
    
    const increment = target / (duration / 16); // 60 FPS roughly
    
    const timer = setInterval(() => {
        current += increment;
        if (current >= target) {
            el.innerText = target.toLocaleString() + "+";
            clearInterval(timer);
        } else {
            el.innerText = Math.floor(current).toLocaleString();
        }
    }, 16);
}

// 5. Advanced Spectrum Canvas Background (Restricted to bottom)
const canvas = document.getElementById('spectrumCanvas');
const ctx = canvas.getContext('2d');

let width, height;
let particles = [];

function resizeCanvas() {
    width = window.innerWidth;
    // We only want the canvas to cover the bottom 60vh
    height = window.innerHeight * 0.6;
    canvas.width = width;
    canvas.height = height;
}

window.addEventListener('resize', resizeCanvas);
resizeCanvas();

class Particle {
    constructor() {
        this.reset();
        this.y = Math.random() * height;
    }
    
    reset() {
        this.x = Math.random() * width;
        this.y = height + 50;
        this.size = Math.random() * 2 + 0.5;
        this.speedY = Math.random() * 1.5 + 0.5;
        this.speedX = (Math.random() - 0.5) * 1;
        const colors = ['rgba(0, 240, 255, 0.4)', 'rgba(255, 0, 85, 0.2)', 'rgba(255, 176, 0, 0.3)'];
        this.color = colors[Math.floor(Math.random() * colors.length)];
        this.sinValue = Math.random() * Math.PI * 2;
    }

    update() {
        this.y -= this.speedY;
        this.sinValue += 0.01;
        this.x += Math.sin(this.sinValue) * 0.8 + this.speedX;

        // Reset if it goes above the canvas
        if (this.y < -50) {
            this.reset();
        }
    }

    draw() {
        ctx.beginPath();
        ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
        ctx.fillStyle = this.color;
        ctx.fill();
        ctx.shadowBlur = 10;
        ctx.shadowColor = this.color;
    }
}

for (let i = 0; i < 60; i++) {
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

    ctx.shadowBlur = 0;

    // Background waves
    drawWave(20, 'rgba(255, 0, 85, 0.05)', 0.8, 40, 0.005, 30, 0.01);
    drawWave(40, 'rgba(0, 240, 255, 0.08)', 1.2, 30, 0.01, 20, 0.02);
    
    // Front glowing wave
    ctx.beginPath();
    ctx.moveTo(0, height);
    for (let x = 0; x <= width; x += 10) {
        const t = time * 1.5;
        const noise = Math.sin(x * 0.05 + t) * 10;
        const mainWave = Math.sin(x * 0.015 - t * 0.5) * 40;
        
        const distanceFromCenter = Math.abs(x - width / 2) / (width / 2);
        const attenuation = Math.max(0.1, 1 - Math.pow(distanceFromCenter, 2));
        
        const y = height - 60 + (noise + mainWave) * attenuation;
        ctx.lineTo(x, y);
    }
    ctx.strokeStyle = 'rgba(0, 240, 255, 0.6)';
    ctx.lineWidth = 2;
    ctx.shadowBlur = 10;
    ctx.shadowColor = 'rgba(0, 240, 255, 0.8)';
    ctx.stroke();

    time += 0.03;
    requestAnimationFrame(animate);
}

animate();

// 6. Global Click Ripple Effect
document.addEventListener('mousedown', function (e) {
    const ripple = document.createElement('div');
    ripple.classList.add('ripple');
    
    // Size of the ripple
    const size = 150;
    ripple.style.width = ripple.style.height = `${size}px`;
    
    // Position fixed relative to viewport
    ripple.style.left = `${e.clientX - size / 2}px`;
    ripple.style.top = `${e.clientY - size / 2}px`;
    
    document.body.appendChild(ripple);
    
    // Clean up after animation completes
    setTimeout(() => {
        ripple.remove();
    }, 600);
});
