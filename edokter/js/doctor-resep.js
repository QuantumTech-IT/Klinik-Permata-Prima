// Progressive enhancement for the prescription page.
(function () {
  function ready(fn) {
    if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", fn);
    else fn();
  }

  function el(tag, attrs, text) {
    var n = document.createElement(tag);
    if (attrs) {
      Object.keys(attrs).forEach(function (k) {
        n.setAttribute(k, attrs[k]);
      });
    }
    if (typeof text === "string") n.textContent = text;
    return n;
  }

  ready(function () {
    var root = document.querySelector(".doc-resep");
    if (!root) return;

    var search = root.querySelector("[data-rx-search]");
    var results = root.querySelector("[data-rx-results]");
    var rows = root.querySelector("[data-rx-rows]");
    if (!rows) return;

    function addRow(item) {
      var wrap = el("div", { class: "rx-row" });
      wrap.appendChild(el("input", { type: "hidden", name: "kode_brng[]", value: item.kode || "" }));

      var name = el("div", { class: "rx-row__name" });
      name.appendChild(el("div", { class: "rx-row__title" }, item.nama || ""));
      name.appendChild(el("div", { class: "rx-row__sub" }, (item.kode || "") + (item.sat ? " · " + item.sat : "")));
      wrap.appendChild(name);

      var qty = el("input", { class: "dv-input rx-row__qty", type: "number", step: "0.01", min: "0", name: "jml[]", placeholder: "Jumlah" });
      wrap.appendChild(qty);

      var signa = el("input", { class: "dv-input rx-row__signa", type: "text", name: "aturan_pakai[]", placeholder: "Aturan pakai" });
      wrap.appendChild(signa);

      var del = el("button", { class: "dv-btn dv-btn--danger rx-row__del", type: "button", title: "Hapus" }, "Hapus");
      del.addEventListener("click", function () {
        wrap.parentNode.removeChild(wrap);
      });
      wrap.appendChild(del);

      rows.appendChild(wrap);
      qty.focus();
    }

    async function fetchJSON(url) {
      var r = await fetch(url, { credentials: "same-origin" });
      if (!r.ok) return null;
      return await r.json();
    }

    var lastAbort = null;
    async function doSearch(term) {
      if (!results) return;
      results.innerHTML = "";
      term = (term || "").trim();
      if (term.length < 2) return;

      if (lastAbort) lastAbort.abort();
      lastAbort = new AbortController();

      var url = "index.php?act=Resep&ajax=obat&q=" + encodeURIComponent(term);
      var data = null;
      try {
        var r = await fetch(url, { credentials: "same-origin", signal: lastAbort.signal });
        if (!r.ok) return;
        data = await r.json();
      } catch (e) {
        return;
      }
      if (!Array.isArray(data)) return;

      data.forEach(function (it) {
        var btn = el("button", { type: "button", class: "rx-hit" });
        btn.appendChild(el("div", { class: "rx-hit__name" }, it.nama || ""));
        btn.appendChild(el("div", { class: "rx-hit__meta" }, (it.kode || "") + (it.sat ? " · " + it.sat : "")));
        btn.addEventListener("click", function () {
          addRow(it);
          if (search) search.value = "";
          results.innerHTML = "";
        });
        results.appendChild(btn);
      });
    }

    if (search) {
      var t = null;
      search.addEventListener("input", function () {
        clearTimeout(t);
        t = setTimeout(function () {
          doSearch(search.value);
        }, 200);
      });
    }

    var addManual = root.querySelector("[data-rx-add-manual]");
    if (addManual) {
      addManual.addEventListener("click", function () {
        addRow({ kode: "", nama: "Item Manual", sat: "" });
      });
    }
  });
})();

