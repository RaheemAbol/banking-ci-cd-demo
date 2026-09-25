# React frontend

See `../WALKTHROUGH.md` for GitHub Actions or `../LOCAL-APP-SETUP.md` to run locally.

Use Node 24. `npm ci` installs dependencies from the committed lockfile, `npm test` runs three rendering tests once, and `npm run build` writes production files to `dist`.

Development: `npm run dev`, then open http://localhost:5173 with Spring Boot running on port 8080. Vite proxies `/api` to the backend during development. Deployment must separately provide API routing; see the walkthrough's CD section.
