// Lucide icons initialization (after dom load)
document.addEventListener('DOMContentLoaded', () => {
    if (window.lucide) {
        lucide.createIcons();
    }
    
    initDashboard();
});

function initDashboard() {
    let currentMode = 'moog'; // moog, ai, mic, file
    let isAnalyzing = true;
    
    const btnMoog = document.getElementById('btnMoog');
    const btnAi = document.getElementById('btnAi');
    const btnMic = document.getElementById('btnMic');
    const micIcon = document.getElementById('micIcon');
    const fileUpload = document.getElementById('fileUpload');
    const authFileName = document.getElementById('authFileName');
    const authFileNameText = document.getElementById('authFileNameText');
    
    const authPanel = document.getElementById('authPanel');
    const authIcon = document.getElementById('authIcon');
    const authIconWrap = document.getElementById('authIconWrap');
    const authResultBox = document.getElementById('authResultBox');
    const authDesc = document.getElementById('authDesc');
    
    // Metrics
    const valRolloff = document.getElementById('valRolloff');
    const barRolloff = document.getElementById('barRolloff');
    const valPhase = document.getElementById('valPhase');
    const barPhase = document.getElementById('barPhase');
    const valTransient = document.getElementById('valTransient');
    const barTransient = document.getElementById('barTransient');
    const badgeWatermark = document.getElementById('badgeWatermark');
    
    const dashTabs = document.querySelectorAll('.dash-tab-btn');
    const tabPanes = document.querySelectorAll('.tab-pane');
    
    const specCanvas = document.getElementById('dashSpectrumCanvas');
    const transCanvas = document.getElementById('dashTransientCanvas');
    const waterCanvas = document.getElementById('dashWatermarkCanvas');
    
    let specCtx, transCtx, waterCtx;
    if (specCanvas) specCtx = specCanvas.getContext('2d');
    if (transCanvas) transCtx = transCanvas.getContext('2d');
    if (waterCanvas) waterCtx = waterCanvas.getContext('2d');
    
    function resizeCanvases() {
        if (!specCanvas) return;
        const width = specCanvas.parentElement.clientWidth;
        const height = 256;
        
        specCanvas.width = width; specCanvas.height = height;
        transCanvas.width = width; transCanvas.height = height;
        waterCanvas.width = width; waterCanvas.height = height;
    }
    window.addEventListener('resize', resizeCanvases);
    resizeCanvases();
    
    // Tab switching
    dashTabs.forEach(btn => {
        btn.addEventListener('click', () => {
            dashTabs.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            
            const target = btn.getAttribute('data-tab');
            tabPanes.forEach(pane => {
                pane.style.display = pane.id === `tab-${target}` ? 'block' : 'none';
            });
            // Force redraw immediately when tab changes
            drawCanvases();
        });
    });
    
    function updateMetrics(mode) {
        currentMode = mode;
        
        btnMoog.classList.remove('active-moog');
        btnAi.classList.remove('active-ai');
        btnMic.classList.remove('active-mic');
        
        if (mode === 'moog') btnMoog.classList.add('active-moog');
        if (mode === 'ai') btnAi.classList.add('active-ai');
        if (mode === 'mic') btnMic.classList.add('active-mic');
        
        authFileName.style.display = 'none';
        
        if (mode === 'ai') {
            authPanel.className = 'auth-panel state-ai';
            authIconWrap.innerHTML = '<i data-lucide="shield-alert" class="w-10 h-10"></i>';
            authResultBox.innerHTML = '<span>[AI DETECTED: 98.4%]</span>';
            authDesc.innerText = 'Corte espectral abrupto en 16kHz & artefactos de difuminado en fase detectados.';
            
            valRolloff.innerText = '15.5 kHz';
            valRolloff.className = 'metric-val val-red';
            barRolloff.style.width = '70%';
            barRolloff.className = 'metric-bar-fill fill-red';
            
            valPhase.innerText = '42.1%';
            valPhase.className = 'metric-val val-amber';
            barPhase.style.width = '42.1%';
            barPhase.className = 'metric-bar-fill fill-cyan';
            
            valTransient.innerText = '38.2%';
            valTransient.className = 'metric-val val-red';
            barTransient.style.width = '38.2%';
            barTransient.className = 'metric-bar-fill fill-red';
            
            badgeWatermark.innerText = 'DETECTED (96.8%)';
            badgeWatermark.className = 'watermark-badge detected';
            
            document.getElementById('transientHeaderLabel').innerText = 'Ataque Difuminado (IA)';
            document.getElementById('transientHeaderLabel').style.color = '#f43f5e';
            document.getElementById('watermarkHeaderLabel').innerText = 'Patrón Detectado';
            document.getElementById('watermarkHeaderLabel').style.color = '#c084fc';
            
        } else {
            authPanel.className = 'auth-panel state-moog';
            authIconWrap.innerHTML = '<i data-lucide="shield-check" class="w-10 h-10"></i>';
            authResultBox.innerHTML = '<span>[ANALOG AUTHENTIC: 98.2%]</span>';
            authDesc.innerText = 'Respuesta armónica pura continua hasta altas frecuencias sin filtros de difusión.';
            
            valRolloff.innerText = '22.0 kHz';
            valRolloff.className = 'metric-val val-green';
            barRolloff.style.width = '100%';
            barRolloff.className = 'metric-bar-fill fill-green';
            
            valPhase.innerText = '98.4%';
            valPhase.className = 'metric-val val-green';
            barPhase.style.width = '98.4%';
            barPhase.className = 'metric-bar-fill fill-cyan';
            
            valTransient.innerText = '94.2%';
            valTransient.className = 'metric-val val-green';
            barTransient.style.width = '94.2%';
            barTransient.className = 'metric-bar-fill fill-green';
            
            badgeWatermark.innerText = 'NOT FOUND';
            badgeWatermark.className = 'watermark-badge none';
            
            document.getElementById('transientHeaderLabel').innerText = 'Ataque Nítido';
            document.getElementById('transientHeaderLabel').style.color = '#34d399';
            document.getElementById('watermarkHeaderLabel').innerText = 'Sin Patrón';
            document.getElementById('watermarkHeaderLabel').style.color = '#64748b';
        }
        
        if (window.lucide) lucide.createIcons();
    }
    
    btnMoog.addEventListener('click', () => updateMetrics('moog'));
    btnAi.addEventListener('click', () => updateMetrics('ai'));
    btnMic.addEventListener('click', () => {
        updateMetrics('mic');
        // We simulate mic input as authentic analog for now
    });
    
    fileUpload.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (!file) return;
        updateMetrics('ai');
        authFileNameText.innerText = file.name;
        authFileName.style.display = 'block';
    });
    
    let frameId;
    function drawCanvases() {
        if (!specCanvas) return;
        const w = specCanvas.width;
        const h = specCanvas.height;
        const isAI = currentMode === 'ai';
        const t = Date.now() * 0.003;
        
        // 1. Spectrum
        if (document.getElementById('tab-spectrum').style.display === 'block') {
            specCtx.fillStyle = '#020617';
            specCtx.fillRect(0, 0, w, h);
            
            const numBars = 128;
            const barW = w / numBars;
            
            for (let i = 0; i < numBars; i++) {
                // procedural frequency data
                const baseVal = Math.max(0, Math.sin(i * 0.05 + t) * 0.5 + 0.5);
                // simulate high freq cutoff for AI
                const cutoff = isAI && i > 90 ? 0.1 : 1.0;
                const noisy = baseVal * cutoff * (Math.random() * 0.2 + 0.8);
                const barH = noisy * h;
                
                const x = i * barW;
                specCtx.fillStyle = (isAI && i > 90) ? '#ef4444' : '#06b6d4';
                specCtx.fillRect(x, h - barH, barW - 1, barH);
            }
            
            // Draw Cutoff Line
            const cutoffX = w * (90 / 128);
            specCtx.strokeStyle = isAI ? '#ef4444' : '#3b82f6';
            specCtx.lineWidth = 2;
            specCtx.setLineDash([4, 4]);
            specCtx.beginPath();
            specCtx.moveTo(cutoffX, 0);
            specCtx.lineTo(cutoffX, h);
            specCtx.stroke();
            specCtx.setLineDash([]);
            
            specCtx.fillStyle = isAI ? '#f87171' : '#60a5fa';
            specCtx.font = '10px monospace';
            specCtx.fillText('16 kHz Cutoff', cutoffX - 80, 20);
        }
        
        // 2. Transients
        if (document.getElementById('tab-transients').style.display === 'block') {
            transCtx.fillStyle = '#020617';
            transCtx.fillRect(0, 0, w, h);
            
            transCtx.beginPath();
            transCtx.strokeStyle = isAI ? '#f43f5e' : '#10b981';
            transCtx.lineWidth = 2;
            
            const numPoints = 100;
            const step = w / numPoints;
            for (let i = 0; i < numPoints; i++) {
                // simulate sharp spikes vs smeared
                const timeFactor = (i * 0.1) + (Date.now() * 0.005);
                const isSpike = Math.sin(timeFactor) > 0.9;
                
                let y = h / 2;
                if (isSpike) {
                    const spikeHeight = isAI ? (h * 0.3) : (h * 0.8);
                    y -= spikeHeight * Math.cos(timeFactor * 10);
                } else {
                    y -= (Math.random() * h * 0.1);
                }
                
                if (i === 0) transCtx.moveTo(i * step, y);
                else transCtx.lineTo(i * step, y);
            }
            transCtx.stroke();
        }
        
        // 3. Watermark
        if (document.getElementById('tab-watermark').style.display === 'block') {
            waterCtx.fillStyle = '#020617';
            waterCtx.fillRect(0, 0, w, h);
            
            const cols = 32;
            const rows = 12;
            const cw = w / cols;
            const ch = h / rows;
            
            for (let r = 0; r < rows; r++) {
                for (let c = 0; c < cols; c++) {
                    // if AI, we reveal a specific pattern block
                    const isPattern = isAI && (Math.sin(c*0.5 + r*0.3 + t*0.5) > 0.5);
                    const alpha = isPattern ? (Math.random() * 0.5 + 0.3) : (Math.random() * 0.1);
                    
                    waterCtx.fillStyle = isAI ? `rgba(239, 68, 68, ${alpha})` : `rgba(59, 130, 246, ${alpha})`;
                    waterCtx.fillRect(c * cw + 1, r * ch + 1, cw - 2, ch - 2);
                }
            }
        }
        
        frameId = requestAnimationFrame(drawCanvases);
    }
    drawCanvases();
}
