/**
 * @fileoverview Main client-side script for the Lumin Consultancy public web portal.
 * Handles mobile navigation drawers, CTA event dispatching, form validation,
 * honeypot anti-spam submission, and AJAX communication with the backend REST API.
 *
 * @author Lumin Technical Team
 * @version 1.0.0
 */

document.addEventListener("DOMContentLoaded", () => {
  // =========================================================================
  // Mobile Navigation Drawer Toggle & Accessibility Controls
  // =========================================================================
  const menuBtn = document.getElementById("menu");
  const navMenu = document.getElementById("nav");

  if (menuBtn && navMenu) {
    const menuText = menuBtn.querySelector(".menu-text");

    /**
     * Opens the mobile slide-out navigation menu and updates ARIA states.
     */
    function openMenu() {
      navMenu.classList.add("open");
      navMenu.classList.add("is-open");
      menuBtn.classList.add("is-active");
      menuBtn.setAttribute("aria-expanded", "true");
      if (menuText) menuText.textContent = "Close";
    }

    /**
     * Closes the mobile navigation menu and restores default ARIA states.
     */
    function closeMenu() {
      navMenu.classList.remove("open");
      navMenu.classList.remove("is-open");
      menuBtn.classList.remove("is-active");
      menuBtn.setAttribute("aria-expanded", "false");
      if (menuText) menuText.textContent = "Menu";
    }

    // Toggle menu state on button click
    menuBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      const isOpen = navMenu.classList.contains("open") || navMenu.classList.contains("is-open");
      if (isOpen) {
        closeMenu();
      } else {
        openMenu();
      }
    });

    // Close when clicking any navigation link inside nav
    navMenu.addEventListener("click", (e) => {
      if (e.target.closest("a")) {
        closeMenu();
      }
    });

    // Close when clicking outside of the active menu container
    document.addEventListener("click", (e) => {
      const isOpen = navMenu.classList.contains("open") || navMenu.classList.contains("is-open");
      if (isOpen && !navMenu.contains(e.target) && !menuBtn.contains(e.target)) {
        closeMenu();
      }
    });

    // Close menu when Escape key is pressed
    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape") {
        closeMenu();
      }
    });

    // Reset drawer state when window is resized past mobile threshold
    window.addEventListener("resize", () => {
      if (window.innerWidth > 1024) {
        closeMenu();
      }
    });
  }

  // =========================================================================
  // Analytics & CTA Event Tracking Hook
  // =========================================================================
  /* CTA click tracking hook: wire to your analytics (e.g. gtag) here */
  document.querySelectorAll("[data-cta]").forEach(a => a.addEventListener("click", () => {
    if (window.gtag) gtag("event", "cta_click", { cta: a.dataset.cta });
  }));

  // =========================================================================
  // Consultation Enquiry Form Handling & AJAX Submission
  // =========================================================================
  const form = document.getElementById("enq");
  const msg = document.getElementById("msg");
  const send = document.getElementById("send");

  /**
   * Displays status or validation feedback messages to the user.
   * @param {string} t - Feedback text content.
   * @param {string} c - CSS class name ('ok' for success, 'err' for error).
   */
  const show = (t, c) => { if (msg) { msg.className = c; msg.textContent = t; } };

  /**
   * Safely reads and trims the value of an input element by ID.
   * @param {string} id - The DOM element ID.
   * @returns {string} Trimmed string value.
   */
  const v = id => { const el = document.getElementById(id); return el ? el.value.trim() : ""; };

  if (form) {
    form.addEventListener("submit", async e => {
      e.preventDefault();
      show("", "");

      // Validate required inputs and email format
      const bad = [...form.querySelectorAll("[required]")].find(i => !i.value.trim() || (i.type === "email" && !/^\S+@\S+\.\S+$/.test(i.value)));
      if (bad) {
        bad.focus();
        return show("Please complete the highlighted required fields with valid details.", "err");
      }

      // Prepare request payload including anti-bot honeypot field
      const payload = {
        name: v("name"),
        organization: v("org"),
        designation: v("des"),
        email: v("email"),
        phone: v("phone"),
        sector: v("sector"),
        service: v("svc"),
        status: v("stat"),
        message: v("msgt"),
        preferred: v("pref"),
        website: (form.querySelector('[name="_honey"]') || {}).value || ""
      };

      if (send) {
        send.disabled = true;
        send.textContent = "Sending…";
      }

      try {
        const r = await fetch("/api/enquiry", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });
        const j = await r.json().catch(() => ({}));
        if (!r.ok || !j.success) throw new Error(j.error || "");

        if (window.gtag) gtag("event", "enquiry_submitted");

        form.reset();
        if (send) {
          send.disabled = false;
          send.textContent = "Request a Consultation";
        }
        show("Thank you for your enquiry! We have received your request and our team will contact you shortly.", "ok");
        msg.scrollIntoView({ behavior: "smooth", block: "nearest" });
      } catch (err) {
        if (send) {
          send.disabled = false;
          send.textContent = "Request a Consultation";
        }
        show(err.message || "We could not send your enquiry. Please try again shortly.", "err");
      }
    });
  }
});

