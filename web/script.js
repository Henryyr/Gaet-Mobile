/**
 * Driver Guide Portfolio - Core Logic
 * Handles component loading, Firebase integration, and UI rendering.
 * Optimized for modularity and absolute paths.
 */

document.addEventListener('DOMContentLoaded', async () => {
    // 1. UTILS
    const utils = {
        escape: (str) => String(str || "").replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":"&#039;"}[m])),
        formatCurrency: (num) => new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', maximumFractionDigits: 0 }).format(num || 0),
        getUserId: () => {
            const params = new URLSearchParams(window.location.search);
            const qId = params.get('uid');
            if (qId) return qId;
            const parts = window.location.pathname.split('/').filter(Boolean);
            const last = parts[parts.length - 1];
            return (last && last !== 'index.html' && last !== 'portfolio') ? last : null;
        },
        loadComponent: async (path, targetId) => {
            try {
                const res = await fetch(path);
                if (!res.ok) throw new Error(`Status: ${res.status}`);
                const html = await res.text();
                document.getElementById(targetId).innerHTML += html;
            } catch (err) {
                console.error(`Failed to load component: ${path}`, err);
            }
        }
    };

    // 2. LOAD UI COMPONENTS (Sequential to maintain order)
    // We use absolute paths (starting with /) to ensure they load regardless of URL depth
    await utils.loadComponent('/components/hero.html', 'hero-container');
    await utils.loadComponent('/components/profile.html', 'hero-container');
    await utils.loadComponent('/components/catalog.html', 'catalog-container');
    await utils.loadComponent('/components/footer.html', 'footer-container');

    // UI ELEMENTS (Mapped after components are fully injected)
    const ui = {
        name: document.getElementById('driver-name'),
        location: document.getElementById('driver-location'),
        bio: document.getElementById('driver-bio'),
        loading: document.getElementById('loading-state'),
        grid: document.getElementById('catalog-grid'),
        empty: document.getElementById('empty-state'),
        search: document.getElementById('search-input')
    };

    // 3. WIDGETS
    const components = {
        catalogCard: (item) => {
            const title = utils.escape(item.title || "Special Journey");
            const price = utils.formatCurrency(item.price);
            const desc = utils.escape(item.description || "Personalized travel experience with a local driver guide.");
            const image = item.imageBase64 ? `data:image/jpeg;base64,${item.imageBase64}` : 'https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=1200&q=80';
            return `
                <article class="catalog-card">
                    <div class="catalog-card__media">
                        <img src="${image}" alt="${title}" loading="lazy" />
                        <div class="catalog-card__price">${price}</div>
                    </div>
                    <div class="catalog-card__body">
                        <h3 class="catalog-card__title">${title}</h3>
                        <p class="catalog-card__text">${desc}</p>
                        <button class="catalog-card__btn">Explore Details</button>
                    </div>
                </article>
            `;
        }
    };

    // 4. CORE ACTIONS
    const db = firebase.firestore();
    const actions = {
        renderCatalog: (items) => {
            if (!ui.loading || !ui.grid || !ui.empty) return;
            ui.loading.classList.add('hidden');
            ui.grid.innerHTML = "";
            if (!items || items.length === 0) {
                ui.grid.classList.add('hidden');
                ui.empty.classList.remove('hidden');
                return;
            }
            ui.empty.classList.add('hidden');
            ui.grid.classList.remove('hidden');
            ui.grid.innerHTML = items.map(components.catalogCard).join('');
        },
        handleSearch: () => {
            const key = ui.search.value.toLowerCase().trim();
            const filtered = actions.state.catalog.filter(item =>
                (item.title + " " + item.description).toLowerCase().includes(key)
            );
            actions.renderCatalog(filtered);
        },
        state: { catalog: [] },
        fetchData: async (uid) => {
            try {
                const userDoc = await db.collection("users").doc(uid).get();
                if (userDoc.exists) {
                    const d = userDoc.data();
                    const fullName = `${d.first_name || ''} ${d.last_name || ''}`.trim();
                    if (ui.name) ui.name.innerText = fullName || "Professional Guide";
                    if (ui.bio) ui.bio.innerText = d.bio || "Crafting memorable journeys.";
                    if (ui.location) ui.location.innerHTML = `📍 ${utils.escape(d.location || 'Indonesia')}`;
                    document.title = `${fullName} - Portfolio`;
                }
                const snap = await db.collection("catalog").where("userId", "==", uid).get();
                actions.state.catalog = snap.docs.map(doc => ({ id: doc.id, ...doc.data() }));
                actions.renderCatalog(actions.state.catalog);
            } catch (err) {
                console.error("Data error:", err);
                if (ui.loading) ui.loading.classList.add('hidden');
                if (ui.empty) ui.empty.classList.remove('hidden');
            }
        }
    };

    // 5. START
    const userId = utils.getUserId();
    if (!userId) {
        if (ui.name) ui.name.innerText = "Local Guide Portfolio";
        if (ui.loading) ui.loading.classList.add('hidden');
        if (ui.empty) ui.empty.classList.remove('hidden');
    } else {
        if (ui.search) ui.search.addEventListener('input', actions.handleSearch);
        actions.fetchData(userId);
    }
});
