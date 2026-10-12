document.addEventListener("DOMContentLoaded", () => {
  // Mobile Menu Toggle & Controls
  const menuBtn = document.getElementById("menu");
  const navMenu = document.getElementById("nav");
  if (menuBtn && navMenu) {
    const menuText = menuBtn.querySelector(".menu-text");

    function openMenu() {
      navMenu.classList.add("open");
      navMenu.classList.add("is-open");
      menuBtn.classList.add("is-active");
      menuBtn.setAttribute("aria-expanded", "true");
      if (menuText) menuText.textContent = "Close";
    }

    function closeMenu() {
      navMenu.classList.remove("open");
      navMenu.classList.remove("is-open");
      menuBtn.classList.remove("is-active");
      menuBtn.setAttribute("aria-expanded", "false");
      if (menuText) menuText.textContent = "Menu";
    }

    menuBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      const isOpen = navMenu.classList.contains("open") || navMenu.classList.contains("is-open");
      if (isOpen) {
        closeMenu();
      } else {
        openMenu();
      }
    });

    // Close when clicking any link inside nav
    navMenu.addEventListener("click", (e) => {
      if (e.target.closest("a")) {
        closeMenu();
      }
    });

    // Close when clicking outside
    document.addEventListener("click", (e) => {
      const isOpen = navMenu.classList.contains("open") || navMenu.classList.contains("is-open");
      if (isOpen && !navMenu.contains(e.target) && !menuBtn.contains(e.target)) {
        closeMenu();
      }
    });

    // Close on Escape key
    document.addEventListener("keydown", (e) => {
      if (e.key === "Escape") {
        closeMenu();
      }
    });

    // Reset when resizing to desktop view
    window.addEventListener("resize", () => {
      if (window.innerWidth > 1024) {
        closeMenu();
      }
    });
  }

  /* CTA click tracking hook: wire to your analytics (e.g. gtag) here */
  document.querySelectorAll("[data-cta]").forEach(a => a.addEventListener("click", () => {
    if (window.gtag) gtag("event", "cta_click", { cta: a.dataset.cta });
  }));

  const form = document.getElementById("enq"), msg = document.getElementById("msg"), send = document.getElementById("send");
  const show = (t, c) => { if (msg) { msg.className = c; msg.textContent = t; } };
  const v = id => { const el = document.getElementById(id); return el ? el.value.trim() : ""; };

  if (form) {
    form.addEventListener("submit", async e => {
      e.preventDefault();
      show("", "");
      const bad = [...form.querySelectorAll("[required]")].find(i => !i.value.trim() || (i.type === "email" && !/^\S+@\S+\.\S+$/.test(i.value)));
      if (bad) { bad.focus(); return show("Please complete the highlighted required fields with valid details.", "err"); }
      const payload = {
        name: v("name"), organization: v("org"), designation: v("des"), email: v("email"), phone: v("phone"),
        sector: v("sector"), service: v("svc"), status: v("stat"), message: v("msgt"), preferred: v("pref"),
        website: (form.querySelector('[name="_honey"]') || {}).value || ""
      };
      if (send) { send.disabled = true; send.textContent = "Sending…"; }
      try {
        const r = await fetch("/api/enquiry", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload) });
        const j = await r.json().catch(() => ({}));
        if (!r.ok || !j.success) throw new Error(j.error || "");
        if (window.gtag) gtag("event", "enquiry_submitted");
        window.location.href = "/thank-you.html";
      } catch (err) {
        if (send) { send.disabled = false; send.textContent = "Request a Consultation"; }
        show(err.message || "We could not send your enquiry. Please try again shortly.", "err");
      }
    });
  }
});
