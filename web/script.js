/**
 * Driver Guide Portfolio - Core Logic
 * Handles component loading, Firebase integration, and UI rendering.
 * Updated to support dynamic onboarding data (tagline, services, vehicle).
 */

document.addEventListener('DOMContentLoaded', async () => {
    // 1. UTILS
    const utils = {
        escape: (str) => String(str || "").replace(/[&<>"']/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":"&#039;"}[m])),
        formatCurrency: (num) => new Intl.NumberFormat('id-ID', { style: 'currency', currency: 'IDR', maximumFractionDigits: 0 }).format(num || 0),
        formatDate: (timestamp) => {
            if (!timestamp) return "";
            const date = timestamp.toDate ? timestamp.toDate() : new Date(timestamp.seconds * 1000);
            return date.toLocaleDateString('id-ID', { day: 'numeric', month: 'short', year: 'numeric' });
        },
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
    const loadDefaultLayout = async () => {
        await utils.loadComponent('/components/hero.html', 'hero-container');
        await utils.loadComponent('/components/profile.html', 'hero-container');
        await utils.loadComponent('/components/catalog.html', 'catalog-container');
        await utils.loadComponent('/components/footer.html', 'footer-container');
    };

    // UI ELEMENTS (Mapped after components are fully injected)
    const getUiElements = () => ({
        name: document.getElementById('driver-name'),
        location: document.getElementById('driver-location'),
        bio: document.getElementById('driver-bio'),
        loading: document.getElementById('loading-state'),
        grid: document.getElementById('catalog-grid'),
        empty: document.getElementById('empty-state'),
        search: document.getElementById('search-input'),

        // Portfolio Specifics
        tagline: document.getElementById('web-tagline'),
        vehicle: document.getElementById('web-vehicle-info'),
        services: document.getElementById('web-services-list')
    });

    let ui = {};

    // 3. WIDGETS
    const components = {
        catalogCard: (item) => {
            const title = utils.escape(item.title || "Trip");
            const dateStr = utils.formatDate(item.created_at);
            const price = utils.formatCurrency(item.price);
            const desc = utils.escape(item.description || "Personalized travel experience.");
            const image = item.imageBase64 ? `data:image/jpeg;base64,${item.imageBase64}` : 'https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=1200&q=80';

            return `
                <article class="catalog-card">
                    <div class="catalog-card__media">
                        <img src="${image}" alt="${title}" loading="lazy" />
                        <div class="catalog-card__price">${price}</div>
                    </div>
                    <div class="catalog-card__body">
                        <div style="display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 0.5rem;">
                            <h3 class="catalog-card__title" style="margin: 0;">${title}</h3>
                            <span style="font-size: 0.75rem; color: #888;">${dateStr}</span>
                        </div>
                        <p class="catalog-card__text">${desc}</p>
                        <button class="catalog-card__btn">Explore Details</button>
                    </div>
                </article>
            `;
        },

        serviceItem: (name, index) => {
            const colors = ['dot--green', 'dot--orange', 'dot--brown'];
            const color = colors[index % colors.length];
            return `
                <div class="profile-list__item">
                    <span class="dot ${color}"></span>
                    <div>
                        <h3>${utils.escape(name)}</h3>
                    </div>
                </div>
            `;
        }
    };

    // 4. CORE ACTIONS
    const db = firebase.firestore();
    const actions = {
        renderCatalog: (items) => {
            if (!ui.grid) return;
            if (ui.loading) ui.loading.classList.add('hidden');
            ui.grid.innerHTML = "";
            if (!items || items.length === 0) {
                if (ui.empty) ui.empty.classList.remove('hidden');
                return;
            }
            if (ui.empty) ui.empty.classList.add('hidden');
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
                // Fetch User Profile
                const userDoc = await db.collection("users").doc(uid).get();
                if (userDoc.exists) {
                    const d = userDoc.data();

                    // Check for custom HTML override
                    if (d.custom_html && d.custom_html.trim() !== "") {
                        document.body.innerHTML = d.custom_html;
                    } else {
                        await loadDefaultLayout();
                    }

                    // Re-bind UI elements after injection
                    ui = getUiElements();

                    const fullName = `${d.first_name || ''} ${d.last_name || ''}`.trim();
                    if (ui.name) ui.name.innerText = fullName || "Professional Guide";
                    if (ui.bio) ui.bio.innerText = d.bio || "Crafting memorable journeys.";
                    if (ui.location) ui.location.innerHTML = `📍 ${utils.escape(d.location || 'Indonesia')}`;
                    document.title = `${fullName} - Portfolio`;

                    // Update Onboarding-defined content
                    if (ui.tagline) ui.tagline.innerText = d.tagline || "Flexible private driver guide.";
                    if (ui.vehicle) ui.vehicle.innerText = d.vehicle ? `Vehicle: ${d.vehicle}` : "Flexible transportation options.";

                    if (ui.services && d.services && d.services.length > 0) {
                        ui.services.innerHTML = d.services.map((s, i) => components.serviceItem(s, i)).join('');
                    } else if (ui.services) {
                        ui.services.innerHTML = ["Flexible Route", "Local Assistance"].map((s, i) => components.serviceItem(s, i)).join('');
                    }

                    if (ui.search) ui.search.addEventListener('input', actions.handleSearch);
                }

                // Fetch Catalog Items
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
        await loadDefaultLayout();
        ui = getUiElements();
        if (ui.name) ui.name.innerText = "Local Guide Portfolio";
        if (ui.loading) ui.loading.classList.add('hidden');
        if (ui.empty) ui.empty.classList.remove('hidden');
    } else {
        actions.fetchData(userId);
    }
});
