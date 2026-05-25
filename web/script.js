const db = firebase.firestore();

const urlParams = new URLSearchParams(window.location.search);
let userId = urlParams.get("uid");

if (!userId) {
  const pathParts = window.location.pathname.split("/").filter(Boolean);
  userId = pathParts[pathParts.length - 1];
}

const state = {
  catalog: [],
  filtered: []
};

const el = {
  name: document.getElementById("driver-name"),
  location: document.getElementById("driver-location"),
  bio: document.getElementById("driver-bio"),
  loading: document.getElementById("loading-state"),
  grid: document.getElementById("catalog-grid"),
  empty: document.getElementById("empty-state"),
  search: document.getElementById("search-input")
};

function escapeHtml(value) {
  return String(value || "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function formatPrice(price) {
  const number = Number(price || 0);

  return new Intl.NumberFormat("id-ID", {
    style: "currency",
    currency: "IDR",
    maximumFractionDigits: 0
  }).format(number);
}

function imageSrc(item) {
  if (item.imageBase64) return `data:image/jpeg;base64,${item.imageBase64}`;
  if (item.imageUrl) return item.imageUrl;

  return "https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=1000&q=80";
}

function renderCatalog(items) {
  el.loading.classList.add("hidden");
  el.grid.innerHTML = "";

  if (!items.length) {
    el.grid.classList.add("hidden");
    el.empty.classList.remove("hidden");
    return;
  }

  el.empty.classList.add("hidden");
  el.grid.classList.remove("hidden");

  el.grid.innerHTML = items.map((item) => {
    const title = escapeHtml(item.title || "Local Adventure");
    const description = escapeHtml(
      item.description || "A comfortable and flexible driver guide service for your journey."
    );
    const price = formatPrice(item.price);
    const img = escapeHtml(imageSrc(item));
    const duration = escapeHtml(item.duration || "Flexible trip");
    const area = escapeHtml(item.area || item.location || "Local route");

    return `
      <article class="catalog-card">
        <div class="catalog-card__media">
          <img src="${img}" alt="${title}" class="catalog-card__image" loading="lazy" />
          <div class="price-pill">${price}</div>
        </div>

        <div class="catalog-card__body">
          <div class="catalog-card__tags">
            <span class="tag tag--green">${area}</span>
            <span class="tag tag--orange">${duration}</span>
          </div>

          <h3 class="catalog-card__title">${title}</h3>
          <p class="catalog-card__description">${description}</p>

          <button type="button" class="catalog-card__button">View Trip Details</button>
        </div>
      </article>
    `;
  }).join("");
}

function applySearch() {
  const keyword = el.search.value.trim().toLowerCase();

  state.filtered = state.catalog.filter((item) => {
    const haystack = [item.title, item.description, item.area, item.location, item.duration]
      .join(" ")
      .toLowerCase();

    return haystack.includes(keyword);
  });

  renderCatalog(state.filtered);
}

async function loadProfile(uid) {
  const doc = await db.collection("users").doc(uid).get();

  if (!doc.exists) {
    el.name.innerText = "Guide Not Found";
    el.bio.innerText = "This profile is not available or the link is invalid.";
    el.location.innerHTML = `<span>Indonesia</span>`;
    return;
  }

  const data = doc.data();
  const fullName = `${data.first_name || ""} ${data.last_name || ""}`.trim() || "Local Guide";

  el.name.innerText = fullName;
  el.bio.innerText = data.bio ||
    "Ready to take you through the best places with a comfortable, safe, and personal journey.";

  el.location.innerHTML = `
    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path>
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path>
    </svg>
    <span>${escapeHtml(data.location || "Indonesia")}</span>
  `;

  document.title = `${fullName} - Driver Guide Portfolio`;
}

async function loadCatalog(uid) {
  const snapshot = await db.collection("catalog").where("userId", "==", uid).get();

  state.catalog = snapshot.docs.map((doc) => ({
    id: doc.id,
    ...doc.data()
  }));

  state.filtered = state.catalog;
  renderCatalog(state.filtered);
}

function showMissingUserState() {
  el.name.innerText = "Guide Link Missing";
  el.bio.innerText = "Add ?uid=USER_ID to the URL or use /USER_ID in the path.";
  el.location.innerHTML = `<span>Indonesia</span>`;
  el.loading.classList.add("hidden");
  el.empty.classList.remove("hidden");
}

function showErrorState(error) {
  console.error(error);
  el.loading.classList.add("hidden");
  el.empty.classList.remove("hidden");
  el.empty.innerHTML = `
    <h3>Unable to load data.</h3>
    <p>Please check your Firebase connection or catalog structure.</p>
  `;
}

async function init() {
  if (!userId || userId === "index.html") {
    showMissingUserState();
    return;
  }

  try {
    await Promise.all([
      loadProfile(userId),
      loadCatalog(userId)
    ]);
  } catch (error) {
    showErrorState(error);
  }
}

el.search.addEventListener("input", applySearch);
init();