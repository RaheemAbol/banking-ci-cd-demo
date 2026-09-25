# CI/CD: learn it by building a pipeline

You already know how to work on Spring Boot and React. Here you will tell GitHub how to prepare a separate computer, check your application, and keep the build outputs automatically.

The application is the thing being checked. The YAML file is the set of instructions that performs the checking.

## 1. What CI/CD is doing for you

Suppose you change the vendor service and push your code. It may compile on your machine while a dependency or configuration is missing from the repository. Or a change could compile successfully but break expected behavior.

Continuous integration (CI) gives each selected change an automated check in a fresh environment. In this lesson those checks are:

1. Compile the backend and run its unit tests.
2. Install frontend dependencies, run rendering tests, and build production frontend files.
3. Start a temporary MySQL database and test Spring Boot against it.
4. Save the successful build outputs so someone can retrieve them.

GitHub Actions is the tool executing those instructions. CI is the practice the workflow supports. Running the checks does not by itself prevent somebody merging a failed pull request; a branch rule requiring the check supplies that enforcement.

Continuous delivery extends those checks into a repeatable path for releasing a verified version to an environment, potentially with human approval before production. Continuous deployment automatically deploys successful changes to production. Both meanings of CD involve a release/deployment process beyond merely compiling code.

This starter completes CI and saves packages. There is no hosting destination configured, so it does not claim a deployed application or a complete continuous delivery process. Section 9 explains the remaining pieces.

## 2. Put the starter in a fresh repository

Extract the ZIP. Its outer folder is `ci-cd-starter`; the contents of that folder should become the root of the repository.

| Repository path | Contents |
| --- | --- |
| `.github/workflows/ci.yml` | Active GitHub Actions instructions |
| `backend/pom.xml` | Maven configuration |
| `backend/src` | Spring Boot application and tests |
| `frontend/package.json` | Frontend dependencies and commands |
| `frontend/package-lock.json` | Exact dependency versions used by npm ci |
| `sql/schema.sql` | Tables for a new database |
| `workflow-examples` | Inactive examples for successive lesson stages |

GitHub looks for workflow files directly in the repository's `.github/workflows` directory. Putting that directory inside `backend` or `Lesson_04` will not activate the workflow.

The VS Code route: open `ci-cd-starter`, initialize a Git repository from Source Control, stage and commit the files, then use Publish to GitHub to create a fresh repository. Verify that the `.github` directory was included.

Alternatively, create an empty repository named `ci-cd-practice` on GitHub, then run these Git commands from inside the extracted `ci-cd-starter` folder. Replace `YOUR_USERNAME` before running the remote command:

```bash
git init
git add .
git commit -m "Add CI learning starter"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/ci-cd-practice.git
git push -u origin main
```

No Maven or npm commands are needed on your computer for the CI lesson. GitHub runs them. All application source, test files, and dependency files are already included.

## 3. Stage 1: build and test the backend

The active `ci.yml` already contains this:

```yaml
name: CI practice

on:
  workflow_dispatch:

permissions:
  contents: read

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    timeout-minutes: 15

    steps:
      - name: Get the repository code
        uses: actions/checkout@v7

      - name: Set up Java
        uses: actions/setup-java@v6
        with:
          distribution: temurin
          java-version: '17'

      - name: Build and test backend
        working-directory: backend
        run: mvn -B -ntp verify
```

Read it as: “When I ask, give me an Ubuntu computer, copy my repository onto it, select Java 17, then build and test the backend.”

### What each part means

| YAML | Meaning here |
| --- | --- |
| `name: CI practice` | The workflow's display name in the Actions tab |
| `on:` | Events allowed to start this workflow |
| `workflow_dispatch:` | Enable a manual Run workflow button |
| `permissions: contents: read` | Give the workflow's GitHub token permission to read repository contents |
| `jobs:` | Groups of work; this file has one job |
| `build-and-test:` | Your identifier for that job; you can choose another name |
| `runs-on: ubuntu-latest` | Run the job on a GitHub-hosted Ubuntu runner |
| `timeout-minutes: 15` | Stop this job if it runs longer than 15 minutes |
| `steps:` | Tasks to execute in order inside this job |
| `- name:` | A readable label for a step in the logs |
| `uses:` | Execute a reusable action someone has already written |
| `with:` | Input settings passed to that action |
| `working-directory: backend` | Run this step's command inside the backend folder |
| `run:` | Execute a shell command on the runner |

A **workflow** is the overall automation. A **run** is one execution of that workflow. A **job** is a group of steps executed on a runner. A **runner** is the computer doing that work. An **action** is a reusable component invoked by `uses`.

`actions/checkout@v7` copies the selected repository revision onto the runner. Without checkout, the computer has no application source to build. `@v7` identifies the action's version; it is unrelated to your application's version or Java version.

`actions/setup-java@v6` makes the selected Java installation available. `temurin` is the JDK distribution. `java-version: '17'` selects the application Java version. Maven is preinstalled on this GitHub-hosted Ubuntu runner; setup-java is not an application build command.

`mvn -B -ntp verify` means:

- `mvn`: run Maven using `backend/pom.xml`.
- `-B`: batch mode, appropriate for automation.
- `-ntp`: hide dependency download progress bars.
- `verify`: run the Maven lifecycle through verification, including compilation, configured tests, and packaging.

At this stage, only the two `VendorServiceTest` unit tests run. They use a mocked repository and do not need MySQL. Merely running `verify` does not automatically launch the application, test every endpoint, or connect to a database. Our integration tests have a separate Maven profile introduced in stage 3.

### Run it and inspect it

1. On GitHub, open **Actions**.
2. Select **CI practice** on the left.
3. Select **Run workflow**, choose your default branch, then run it.
4. Open the new run and click **build-and-test**.
5. Expand **Build and test backend**.
6. Find `Tests run: 2, Failures: 0, Errors: 0` and `BUILD SUCCESS`.

The first run can take longer because dependencies must be downloaded. The runner creates `backend/target/ecommerce-security-1.0.0.jar`. It is not downloadable from GitHub yet; we add artifact upload later.

If you cannot see Run workflow, check that the file is on the repository's default branch and Actions is enabled for that repository.

### Exercise: learn a failing step without editing tests

In GitHub, edit only `ci.yml`. Change `working-directory: backend` to `working-directory: backend-wrong`. Commit, then run the workflow manually.

The failure should happen when GitHub tries to start the backend command in a directory that does not exist. Open the red step and read the error. Java source and test behavior have not changed; the pipeline instruction is wrong.

Restore `backend`, commit, and run again. The run should pass. This illustrates why a failed pipeline can be a configuration problem rather than an application bug.

## 4. Stage 2: add React

Replace the contents of `.github/workflows/ci.yml` with `workflow-examples/02-add-frontend.yml`, commit, and run it manually. You can copy the example in your editor or use GitHub's file editor. Replacing the active file avoids accidentally running several workflows.

The new steps appended below the backend step are:

```yaml
      - name: Set up Node
        uses: actions/setup-node@v7
        with:
          node-version: '24'

      - name: Install frontend dependencies
        working-directory: frontend
        run: npm ci

      - name: Test frontend
        working-directory: frontend
        run: npm test

      - name: Build frontend
        working-directory: frontend
        run: npm run build
```

`setup-node` selects Node for the following commands. Node runs npm and Vite. The React code delivered to a user eventually runs in their browser.

`npm ci` installs the locked dependencies in `package-lock.json`. This is suited to repeatable automated builds: it fails if the lockfile does not agree with `package.json`. Commit dependency updates and their regenerated lockfile together.

`npm test` invokes the starter's `test` script, `vitest run`. That command runs three React rendering tests once and exits. A test watcher would wait for edits and is inappropriate for this job.

`npm run build` invokes Vite's production build and creates `frontend/dist`. This proves the frontend can be bundled. It does not prove every browser interaction works, and it does not deploy the files.

Each `run` step starts a new shell. Setting `working-directory` on the backend step does not change the directory of later steps. Files do remain available between steps within this same job.

**Expected result:** two backend tests, three frontend tests, a backend JAR, and a frontend build. There is still no running database or published website.

**Exercise:** rename the YAML step label `Test frontend` to `Check the React catalog`, commit, and run again. Find the changed label in the logs. `name` changes the display; `run` determines what executes.

## 5. Stage 3: add a real MySQL check

Replace the active file's contents with `workflow-examples/03-add-mysql.yml`, commit, and run manually.

The backend unit tests work without a database. They cannot tell you whether the Spring configuration, SQL schema, entity mappings, and actual MySQL queries work together. That is what the two added integration tests check.

### A temporary database for this job

The job now includes:

```yaml
    services:
      mysql:
        image: mysql:8.4
        env:
          MYSQL_ROOT_PASSWORD: ci_root_password
          MYSQL_DATABASE: ecommerce_ci
          MYSQL_USER: ci_user
          MYSQL_PASSWORD: ci_password
        ports:
          - 3306:3306
        options: >-
          --health-cmd="mysqladmin ping -h 127.0.0.1 --silent"
          --health-interval=10s
          --health-timeout=5s
          --health-retries=10
```

`services` belongs to the job, at the same indentation level as `steps`. GitHub starts this service before running the steps. Here a service is a supporting process for testing, not a permanent hosted database.

`image: mysql:8.4` tells GitHub to start the MySQL 8.4 Docker image. A container is a packaged way to run that database on the runner. You do not need to install Docker on your own computer for this exercise.

The variables under the service's `env` configure the new MySQL instance:

| Variable | Meaning |
| --- | --- |
| `MYSQL_ROOT_PASSWORD` | Password for the disposable database administrator |
| `MYSQL_DATABASE` | Create a database named `ecommerce_ci` |
| `MYSQL_USER` | Create a regular database account named `ci_user` |
| `MYSQL_PASSWORD` | Password for that account |

These are intentionally disposable demo credentials, not credentials for your local or production database. No repository secrets are required for this exercise.

`3306:3306` maps port 3306 on the runner to port 3306 inside the MySQL container. Because this job runs directly on the runner, Spring connects to `127.0.0.1:3306`. If the job itself were configured to run inside a container, service networking would be different; the hostname would normally be the service name `mysql`.

The health options make GitHub wait until MySQL responds. `>-` lets YAML fold several lines into one option string. `mysqladmin ping` checks that the server responds, not that your application credentials or tables are correct. The integration tests perform those deeper checks.

### Tell Spring Boot where that database is

The job also includes:

```yaml
    env:
      SPRING_DATASOURCE_URL: jdbc:mysql://127.0.0.1:3306/ecommerce_ci?allowPublicKeyRetrieval=true&useSSL=false
      SPRING_DATASOURCE_USERNAME: ci_user
      SPRING_DATASOURCE_PASSWORD: ci_password
      SPRING_SQL_INIT_MODE: always
      SPRING_SQL_INIT_SCHEMA_LOCATIONS: file:../sql/schema.sql
```

This `env` block is at job level, separate from the `env` block inside the MySQL service. It supplies variables to the job's steps and their processes.

The two blocks have different jobs: `MYSQL_*` creates/configures the database; `SPRING_*` configures the application connecting to it. Their database name, username, and password must agree.

Spring Boot understands these environment-variable names and uses them to override the corresponding application properties for this process:

| Environment variable | Spring property |
| --- | --- |
| `SPRING_DATASOURCE_URL` | `spring.datasource.url` |
| `SPRING_DATASOURCE_USERNAME` | `spring.datasource.username` |
| `SPRING_DATASOURCE_PASSWORD` | `spring.datasource.password` |
| `SPRING_SQL_INIT_MODE` | `spring.sql.init.mode` |
| `SPRING_SQL_INIT_SCHEMA_LOCATIONS` | `spring.sql.init.schema-locations` |

The datasource URL's extra parameters are for this disposable local test connection. A production database should use its provider's TLS and authentication settings.

The database starts empty. `SPRING_SQL_INIT_MODE: always` enables the supplied SQL initialization for MySQL. The schema location tells Spring where to find the tables to create. Because Maven runs from `backend`, `../sql/schema.sql` means the `sql` folder one level up. Spring runs that initialization before Hibernate validates the schema.

This script is for a fresh database. A real deployed application needs controlled schema migrations; it should not run a fresh-create script on every release.

### Turn on the integration tests

The Maven command changes to:

```yaml
        run: mvn -B -ntp -Pmysql verify
```

`-Pmysql` enables the `mysql` Maven profile supplied in `pom.xml`. That profile activates Maven Failsafe to run tests ending in `IT`, including `VendorApiIT`. This is a Maven profile, not `spring.profiles.active`.

The command runs the original two unit tests and then two MySQL integration tests. The integration tests load Spring, initialize/validate the real schema, and exercise the vendor API through MockMvc. One verifies an admin request creates a row that can be read from MySQL. The other verifies a non-admin request is denied and inserts no row. The admin identity is supplied by the test; this does not test the login form itself. Each test transaction rolls back its changes.

**Expected result:** a passing two-test unit summary, a separate passing two-test integration summary, three passing frontend tests, and successful builds.

**Exercise:** change only the job-level `SPRING_DATASOURCE_PASSWORD` to `wrong_password`. Leave the MySQL service password unchanged. Commit and run. MySQL can start, but the backend cannot authenticate to it; inspect the database connection error. Restore `ci_password` and rerun.

This demonstrates that “database container is running” and “application can use the database” are separate checks. Normal later steps will be skipped when the backend command fails.

At job completion, GitHub destroys the service and runner. The database is not yours to keep and does not contain your local MySQL data.

## 6. Stage 4: run automatically and save the results

Replace the active workflow contents with `workflow-examples/04-complete.yml` and commit to `main` or `master`. This commit should start a run automatically.

### Triggers

```yaml
on:
  push:
    branches: [main, master]
  pull_request:
    branches: [main, master]
  workflow_dispatch:
```

- `push`: run when a commit is pushed to either listed branch, including a web-editor commit on that branch.
- `pull_request`: run for relevant pull-request events targeting either listed base branch, such as opening the PR or pushing updates to it.
- `workflow_dispatch`: retain the manual run button.

If your default branch has another name, update both filters. A push to an unrelated feature branch alone does not match this push filter. Opening a PR from that branch into `main` does match the PR filter. Merging it produces a new push event on `main`, so another run is expected.

### Artifacts

A build output that you keep after the runner is removed is an artifact. The complete workflow saves two application outputs:

```yaml
      - name: Save backend JAR
        uses: actions/upload-artifact@v7
        with:
          name: backend-jar
          path: backend/target/*.jar
          if-no-files-found: error
          retention-days: 7

      - name: Save frontend build
        uses: actions/upload-artifact@v7
        with:
          name: frontend-dist
          path: frontend/dist/
          if-no-files-found: error
          retention-days: 7
```

`name` under `with` is the artifact label. The step's own `name` is its label in the job log. `path` selects the files to upload relative to the repository workspace. These action paths are not affected by another step's `working-directory`.

`if-no-files-found: error` prevents a missing build output from being silently treated as success. `retention-days: 7` requests that GitHub retain the outputs for seven days, subject to repository policy.

These normal steps run only when previous normal steps succeed. Thus a failed test prevents publishing those application artifacts. A later report-upload step uses `if: always()` to preserve any available backend test reports even after a failure. It uses `if-no-files-found: warn` because an earlier setup failure may mean no test reports exist.

To retrieve outputs, open the completed workflow run's summary and find **Artifacts**. Download `backend-jar` or `frontend-dist`. Artifact download is not a deployment. GitHub is storing files; it is not continuously running your application.

**Exercise A:** change the display name of a workflow step and commit directly to the default branch. Confirm a new run starts without pressing Run workflow.

**Exercise B:** create a branch named `practice-workflow`, change a step label in `ci.yml`, and open a PR into the default branch. Watch the check appear on the PR. Close it or merge it after reviewing the result.

## 7. YAML rules you actually need

| Syntax | Meaning |
| --- | --- |
| `key: value` | A setting and its value |
| Indentation | Parent/child relationships; use spaces, not tabs |
| `-` | An item in a list, such as a step |
| `#` | A comment |
| `with:` | Inputs to a `uses` action |
| `env:` | Environment variables for its scope |
| `run: |` | A multi-line shell script with line breaks preserved |
| `options: >-` | Fold lines into a single string |
| `${{ ... }}` | A GitHub Actions expression evaluated in a supported context |

For example, a future deployment could use `${{ secrets.DEPLOY_TOKEN }}` to read a saved secret. A value such as `${{ github.sha }}` refers to the run's commit SHA. You do not need either expression for the initial workflow.

Shell variables and GitHub expressions are different: `$VARIABLE` in an Ubuntu `run` command is interpreted by the shell; `${{ ... }}` is Actions expression syntax.

## 8. Read failures in the right order

Start with the first failed step and its first meaningful error. Later skipped steps are often consequences of that failure.

| Symptom | Check |
| --- | --- |
| No workflow appears | `.github/workflows/ci.yml` is at the repository root and committed |
| No manual button | Workflow is on the default branch and includes `workflow_dispatch` |
| No automatic run | You are still on stages 1–3, or the branch does not match stage 4 filters |
| Maven cannot find a POM | `working-directory: backend` and the repository folder layout |
| npm ci rejects the lockfile | `package.json` and `package-lock.json` were committed together |
| MySQL connection refused | Service initialization, host, port mapping, and health logs |
| MySQL access denied | Service credentials and Spring credentials agree |
| Missing tables or schema validation fails | Schema initialization variables and relative file path |
| Java code compiles but tests fail | Compilation checks valid Java; tests check expected behavior |
| Unit tests pass, integration fails | Database/configuration/API integration, not just isolated service logic |
| Artifact not found | Build succeeded and upload `path` points at its actual output |

A green run proves the configured checks passed. It does not prove every feature works or that production is healthy.

### Why start with one job?

The steps share one runner and run in order, which makes the first workflow easier to follow. MySQL is available to the backend step in that job. Backend and frontend could later become separate jobs and run in parallel. A deployment job would typically declare `needs: [backend, frontend]` so it waits for both to succeed. Separate jobs use separate runners, so files must be passed using artifacts; they are not automatically shared.

Caching is another later improvement. A cache speeds up repeat dependency downloads. An artifact preserves a result you want to retrieve or deploy. They serve different purposes.

## 9. What remains for CD?

Your verified application has three different deployment needs:

| Part | What deployment needs |
| --- | --- |
| React | A server/static host to serve `dist` and the chosen API routing |
| Spring Boot | A Java runtime or container host that runs the JAR continuously |
| MySQL | A persistent database with protected credentials and migration procedures |

A practical next stage would:

1. Wait for all CI checks to pass.
2. Download the artifacts built and tested in that same run.
3. Authenticate to the selected hosting provider using its recommended identity mechanism.
4. Apply reviewed database migrations against the persistent database.
5. Deploy the backend JAR and frontend files with the right environment configuration.
6. Check an application health endpoint and decide what to do if deployment is unhealthy.

A GitHub environment can supply deployment-specific settings and, where your repository/plan supports it, require human approval before deploying. Once approved, the workflow can deploy the verified version. With automatic production deployment, successful changes proceed without that manual gate.

Deployment should normally be restricted to approved branches/events; an ordinary PR check should not deploy unreviewed code to production. Provider setup determines the actual YAML actions, credentials, and rollout steps. There is no single generic deploy command that works for every host.

For this application specifically, the frontend makes relative `/api` requests and uses session cookies with CSRF protection. A deployment with one public origin can serve React and reverse-proxy `/api` to Spring Boot. Vite's development proxy does not become a production server when you run `npm run build`.

MySQL in `services` is only the test database. Do not confuse it with the persistent MySQL database your deployed backend would use. Similarly, starting Spring with `java -jar` inside an Actions job and leaving it there is not permanent hosting: it ends with the job.

You can learn and use all four stages in this ZIP before choosing a hosting provider. The outputs and passing checks establish the verified build that a deployment stage should consume.

## Official references

- Workflow concepts: https://docs.github.com/en/actions/concepts/workflows-and-actions/workflows
- Workflow syntax: https://docs.github.com/en/actions/reference/workflows-and-actions/workflow-syntax
- Service containers and networking: https://docs.github.com/en/actions/tutorials/use-containerized-services/use-docker-service-containers
- Artifacts: https://docs.github.com/en/actions/tutorials/store-and-share-data
- checkout: https://github.com/actions/checkout
- setup-java: https://github.com/actions/setup-java
- setup-node: https://github.com/actions/setup-node
- upload-artifact: https://github.com/actions/upload-artifact
- Spring configuration overrides: https://docs.spring.io/spring-boot/reference/features/external-config.html
- Maven integration-test lifecycle: https://maven.apache.org/surefire/maven-failsafe-plugin/usage.html
- Vitest: https://vitest.dev/guide/
