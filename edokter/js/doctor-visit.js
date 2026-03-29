// Lightweight progressive enhancement for the doctor visit page.
(function () {
  function ready(fn) {
    if (document.readyState === "loading") {
      document.addEventListener("DOMContentLoaded", fn);
    } else {
      fn();
    }
  }

  ready(function () {
    var root = document.querySelector(".doc-visit");
    if (!root) return;

    var q = root.querySelector("[data-dv-queue-search]");
    var items = Array.prototype.slice.call(root.querySelectorAll("[data-dv-queue-item]"));
    if (q && items.length) {
      q.addEventListener("input", function () {
        var term = (q.value || "").trim().toLowerCase();
        items.forEach(function (el) {
          var hay = (el.getAttribute("data-dv-hay") || "").toLowerCase();
          el.style.display = hay.indexOf(term) !== -1 ? "" : "none";
        });
      });
    }

    // If the page loads with a selected patient, focus the first textarea for fast typing.
    var firstTextarea = root.querySelector("textarea.dv-input");
    if (firstTextarea) {
      setTimeout(function () {
        try {
          firstTextarea.focus();
        } catch (e) {}
      }, 50);
    }
  });
})();

