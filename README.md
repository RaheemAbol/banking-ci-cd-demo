# Learn CI/CD with Spring Boot, React, and MySQL

Start with `WALKTHROUGH.md`. You can complete the GitHub Actions exercises without running Maven, Node, or MySQL on your computer.

This starter adapts RaheemAbol/ecommerce_w-spring-security, Lesson_04. It preserves the application and session-based Spring Security setup, adds tests, and simplifies the directory structure.

## Start here

1. Extract this ZIP and open the `ci-cd-starter` folder in VS Code.
2. In Source Control, initialize a repository, stage the files, and commit.
3. Use Publish to GitHub to create a new repository named `ci-cd-practice`. Choose public or private as you prefer. Alternatively, use the Git instructions in `WALKTHROUGH.md`.
4. Verify that `.github`, `backend`, `frontend`, and `sql` are at the repository root. Do not upload the ZIP or put everything one extra folder down.
5. In GitHub, select Actions → CI practice → Run workflow.

The first workflow is manual. Simply uploading it will not run it. It must be on the default branch for GitHub to offer its manual Run workflow button.

## Files to use

| File | Purpose |
| --- | --- |
| `WALKTHROUGH.md` | YAML explanations, exercises, troubleshooting, and where CD fits |
| `.github/workflows/ci.yml` | Active workflow; starts with backend only |
| `workflow-examples/01-backend.yml` | Stage 1: backend tests and JAR |
| `workflow-examples/02-add-frontend.yml` | Stage 2: add React tests and build |
| `workflow-examples/03-add-mysql.yml` | Stage 3: add real MySQL integration tests |
| `workflow-examples/04-complete.yml` | Stage 4: push/PR triggers and artifacts |
| `LOCAL-APP-SETUP.md` | Optional instructions for running the app locally |
| `VERIFICATION.md` | What was checked before delivery |

Only `.github/workflows/ci.yml` is active. Examples outside `.github/workflows` do not run. At each stage, replace the contents of `ci.yml` with the next example; keep the same active file.

## Included tests

| Test | Count | What it checks |
| --- | ---: | --- |
| `VendorServiceTest` | 2 | Valid vendor creation; rejection of a blank name |
| `ProductList.test.jsx` | 3 | Product/price rendering; guest restrictions; admin controls |
| `VendorApiIT` | 2 | Admin API writes/reads real MySQL; ordinary users cannot create vendors |

The integration tests exercise Spring request handling using MockMvc and a real database. They do not open a TCP HTTP port or automate a browser. React tests render components and inspect HTML; they do not click through the app. These are starter checks, not exhaustive coverage.

No test edits are required. Exercises change YAML and observe GitHub Actions behavior. The Mockito test configuration uses interface proxy mocking for the repository interface, avoiding JVM agent attachment requirements.

## Versions and source

- Spring Boot 3.5.16 and Java 17 target, matching the source. JDK 21 can also compile this target; the workflow explicitly selects 17.
- React 19.3.0 and Vite 8.3.0, preserved from the source. Workflow uses Node 24.
- Vitest 5.0.1 added for React tests; MySQL 8.4 service for integration.
- Source commit: `383739be83112c70408e3e9e61aa5b3dbab022cc`.
- Source: [Original Lesson_04](https://github.com/RaheemAbol/ecommerce_w-spring-security/tree/383739be83112c70408e3e9e61aa5b3dbab022cc/Lesson_04)

The starter separates schema and sample data and uses one local database name (`ecommerce_demo`). CI uses a disposable `ecommerce_ci` database. The source SQL database name did not match the source application properties; that mismatch is corrected here.

This is CI with packaged build outputs. It does not deploy a public website. Hosting, runtime configuration, and a persistent database are the next steps for CD.
