# Verification

Verified before packaging:

- Backend: `mvn -B -ntp verify` passed on Java 17, with 2 passing unit tests and a Spring Boot executable JAR produced.
- Integration-test sources compiled as part of the backend build.
- Frontend: a fresh `npm ci` completed against the included lockfile.
- Frontend: `npm test` passed all 3 React rendering tests on Node 24.
- Frontend: `npm run build` produced the Vite production bundle.
- All five YAML files parsed, each step had a valid run/uses shape, and working-directory paths matched the starter.
- POM XML parsed and frontend dependency declarations matched the lockfile.
- ZIP includes the active `.github/workflows/ci.yml` and excludes dependencies, build outputs, local tooling, and local credentials.

Not executed here:

- The 2 real MySQL integration tests. A local MySQL server could initialize, but this execution environment prohibited opening its UNIX socket. Confirm these tests in the stage 3/4 GitHub Actions run, which supplies a MySQL service container.
- A hosted GitHub Actions workflow run, artifact upload, or deployment. No remote repository was changed or created.
- A full browser checkout/login flow or exhaustive application regression test.

The final example is configured to run 2 backend unit tests, 2 MySQL integration tests, and 3 frontend tests. This is the expected hosted result, not a claim that all seven ran here.
