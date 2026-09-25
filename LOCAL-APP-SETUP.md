# Optional: run the application locally

This setup is not needed for the GitHub Actions lesson. GitHub creates its own runner and MySQL service.

Requirements: Java 17 or 21, IntelliJ with Maven support, Node 24/npm, and MySQL 8.4 or a compatible MySQL 8 installation.

## Database

In MySQL Workbench, create a new database for this demo:

```sql
CREATE DATABASE ecommerce_demo;
USE ecommerce_demo;
```

With `ecommerce_demo` selected, run `sql/schema.sql` once, then `sql/sample-data.sql` once. Use a new database: the schema file is an initial schema, not a migration to rerun against an existing installation.

## Backend

Open `backend/pom.xml` as a Maven project in IntelliJ. Select your JDK and let Maven load dependencies. In the run configuration for `com.example.ecommerce.Application`, set:

```text
DB_USERNAME=root
DB_PASSWORD=your-local-mysql-password
```

The default URL is `jdbc:mysql://localhost:3306/ecommerce_demo`. To change host or port, also set `DB_URL`.

Run `Application`. Spring Boot listens on port 8080. Its `ddl-auto=validate` setting checks the tables you created; it does not create them locally.

Do not copy CI schema-initialization variables into your everyday local run configuration. CI initializes a fresh database; your local database persists between starts.

## Frontend

In a terminal inside `frontend`:

```bash
npm ci
npm run dev
```

Visit http://localhost:5173. The Vite development proxy sends `/api` requests to Spring Boot on port 8080.

Register a user through the interface. To try admin features, promote that specific demo account in Workbench:

```sql
USE ecommerce_demo;
UPDATE app_users SET role = 'ADMIN' WHERE email = 'your-demo-email@example.test';
```

Log out and back in after promotion. Registration does not accept a user-supplied admin role.

## Optional local checks

The walkthrough runs these commands on GitHub for you.

| Directory | Command | Result |
| --- | --- | --- |
| `backend` | `mvn -B -ntp verify` | Two unit tests and a JAR; no MySQL needed |
| `frontend` | `npm test` | Three rendering tests; no backend needed |
| `frontend` | `npm run build` | Production files in `dist` |

To run `mvn -B -ntp -Pmysql verify` locally, configure a separate fresh test database and the Spring environment variables in stage 3. Run from `backend` so `file:../sql/schema.sql` resolves. The `mysql` name is a Maven profile that activates integration tests, not a Spring profile.

## Deployment difference

The Vite development proxy is not a server included in the built `dist` folder. A deployed frontend needs a web server that forwards `/api` to Spring Boot, or a deliberately configured API base URL and cross-origin authentication settings. A same-origin reverse proxy suits this app's session cookies and CSRF setup. Database credentials belong on the backend.
