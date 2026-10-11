/**
 * Lumin Consultancy - Gallery Script
 */

document.addEventListener('DOMContentLoaded', () => {
  // Initialize Particles on Hero
  if (typeof particlesJS !== 'undefined') {
    particlesJS('particles-js', {
      particles: {
        number: { value: 50, density: { enable: true, value_area: 800 } },
        color: { value: ['#3b82f6', '#60a5fa'] },
        shape: { type: 'circle' },
        opacity: { value: 0.4, random: false },
        size: { value: 3, random: true },
        line_linked: { enable: true, distance: 150, color: '#3b82f6', opacity: 0.25, width: 1 },
        move: { enable: true, speed: 1.2, direction: 'none', random: true, out_mode: 'out' }
      },
      interactivity: {
        detect_on: 'canvas',
        events: { onhover: { enable: true, mode: 'grab' }, resize: true }
      },
      retina_detect: true
    });
  }

  // Mobile Menu Toggle
  const menuBtn = document.getElementById('menu');
  const navMenu = document.getElementById('nav');
  if (menuBtn && navMenu) {
    menuBtn.addEventListener('click', () => {
      const expanded = menuBtn.getAttribute('aria-expanded') === 'true';
      menuBtn.setAttribute('aria-expanded', !expanded);
      navMenu.classList.toggle('is-open');
    });
  }

  // Lightbox Functionality
  const lightbox = document.getElementById('lightbox');
  const lightboxImg = document.getElementById('lightbox-img');
  const lightboxClose = document.getElementById('lightbox-close');
  const cards = document.querySelectorAll('.gallery-card');

  cards.forEach(card => {
    card.addEventListener('click', () => {
      const img = card.getAttribute('data-img');
      if (img && lightboxImg && lightbox) {
        lightboxImg.src = img;
        lightbox.classList.add('is-open');
        document.body.style.overflow = 'hidden';
      }
    });
  });

  function closeLightbox() {
    if (lightbox) {
      lightbox.classList.remove('is-open');
      document.body.style.overflow = '';
      if (lightboxImg) {
        lightboxImg.src = '';
      }
    }
  }

  if (lightboxClose) {
    lightboxClose.addEventListener('click', closeLightbox);
  }

  if (lightbox) {
    lightbox.addEventListener('click', (e) => {
      if (e.target === lightbox || e.target.classList.contains('lightbox-content')) {
        closeLightbox();
      }
    });
  }

  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && lightbox && lightbox.classList.contains('is-open')) {
      closeLightbox();
    }
  });
});
