const menu = document.getElementById("menu"), nav = document.getElementById("nav");
    menu.onclick = () => { const o = nav.classList.toggle("open"); menu.setAttribute("aria-expanded", o); };
    nav.addEventListener("click", e => { if (e.target.tagName === "A") { nav.classList.remove("open"); menu.setAttribute("aria-expanded", false); } });

    /* CTA click tracking hook: wire to your analytics (e.g. gtag) here */
    document.querySelectorAll("[data-cta]").forEach(a => a.addEventListener("click", () => {
      if (window.gtag) gtag("event", "cta_click", { cta: a.dataset.cta });
    }));

    const form = document.getElementById("enq"), msg = document.getElementById("msg"), send = document.getElementById("send");
    const show = (t, c) => { msg.className = c; msg.textContent = t; };
    const v = id => document.getElementById(id).value.trim();

    form.addEventListener("submit", async e => {
      e.preventDefault();
      show("", "");
      const bad = [...form.querySelectorAll("[required]")].find(i => !i.value.trim() || (i.type === "email" && !/^\S+@\S+\.\S+$/.test(i.value)));
      if (bad) { bad.focus(); return show("Please complete the highlighted required fields with valid details.", "err"); }
      const payload = {
        name: v("name"), organization: v("org"), designation: v("des"), email: v("email"), phone: v("phone"),
        sector: v("sector"), service: v("svc"), status: v("stat"), message: v("msgt"), preferred: v("pref"),
        website: form.querySelector('[name="_honey"]').value
      };
      send.disabled = true; send.textContent = "Sending…";
      try {
        const r = await fetch("/api/enquiry", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(payload) });
        const j = await r.json().catch(() => ({}));
        if (!r.ok || !j.success) throw new Error(j.error || "");
        if (window.gtag) gtag("event", "enquiry_submitted");
        window.location.href = "/thank-you.html";
      } catch (err) {
        send.disabled = false; send.textContent = "Request a Consultation";
        show(err.message || "We could not send your enquiry. Please try again shortly.", "err");
      }
    });
