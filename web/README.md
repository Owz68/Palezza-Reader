# Palezza Reader — version Web (Phase 1)

Application statique : `index.html` (lecteur PDF PDF.js, bibliothèque locale IndexedDB).
- Déploiement Cloudflare : `wrangler.jsonc` à la racine publie le dossier `web/`.
- `config.js` : URL Supabase et clé publique uniquement. Ne jamais y mettre la clé `service_role`.
- Cloud, Palezza ID, abonnements et IA : pas encore connectés dans l'interface.
