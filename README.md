# Service Portfolio API

[![CI](https://github.com/PabloHurtadoGonzalo86/service-portfolio/actions/workflows/ci.yml/badge.svg)](https://github.com/PabloHurtadoGonzalo86/service-portfolio/actions/workflows/ci.yml)
[![CD](https://github.com/PabloHurtadoGonzalo86/service-portfolio/actions/workflows/cd.yml/badge.svg)](https://github.com/PabloHurtadoGonzalo86/service-portfolio/actions/workflows/cd.yml)

REST API that analyzes GitHub repositories and generates professional developer portfolios using AI.

## What it does

1. **Repo Analysis** - Pass a GitHub repo URL, the API reads the code and generates a professional README with AI
2. **Portfolio Generation** - Pass a GitHub username, the API scans all public repos and generates a complete developer portfolio
3. **README Commit** - Commit the generated README directly to the user's repository via OAuth

## Tech Stack

- **Kotlin** 2.2.21 + **Spring Boot** 4.0.2 + **Java** 24
- **Spring AI** 2.0.0-M2 (OpenAI)
- **hub4j/github-api** for GitHub integration
- **PostgreSQL** for persistence
- **Spring Security** OAuth2 (GitHub OAuth + GitHub App)

## API Endpoints

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/api/v1/repos/analyze` | Public | Analyze a repo and generate README |
| `GET` | `/api/v1/repos/analyses` | Public | List all analyses |
| `GET` | `/api/v1/repos/analyses/{id}` | Public | Get analysis by ID |
| `POST` | `/api/v1/repos/readme/commit` | OAuth | Commit README to repo |
| `POST` | `/api/v1/portfolio/generate` | Public | Generate developer portfolio |
| `GET` | `/api/v1/portfolio/{id}` | Public | Get portfolio by ID |
| `GET` | `/api/v1/portfolio` | Public | List all portfolios |

## Quick Start

### Prerequisites

- Java 24
- PostgreSQL
- GitHub App (for repo reading)
- GitHub OAuth App (for user commits)
- OpenAI API key

### Environment Variables

```bash
OPENAI_API_KEY=your-openai-key
GITHUB_OAUTH_CLIENT_ID=your-oauth-client-id
GITHUB_OAUTH_CLIENT_SECRET=your-oauth-client-secret
GITHUB_APP_CLIENT_ID=your-github-app-id
GITHUB_APP_INSTALLATION_ID=your-installation-id
GITHUB_APP_PRIVATE_KEY_PATH=/path/to/private-key.pem
DB_HOST=localhost
DB_PORT=5432
DB_NAME=serviceportfolio
DB_USERNAME=serviceportfolio
DB_PASSWORD=your-db-password
```

### Run locally

```bash
./gradlew bootRun
```

### Build

```bash
./gradlew build
```

### Docker

```bash
docker build -t service-portfolio .
docker run -p 8080:8080 --env-file .env service-portfolio
```

## Architecture

```
controller/           -> REST endpoints
service/
  GitHubRepoService       -> Read repos with hub4j (GitHub App token)
  AiAnalysisService       -> AI analysis with Spring AI ChatClient
  ReadmeCommitService     -> Commit README with user OAuth token
  PortfolioGenerationService -> Orchestrate portfolio generation
client/
  GitHubAppTokenService   -> GitHub App installation token management
config/
  SecurityConfig          -> Spring Security + OAuth2 + CORS
  AiConfig                -> ChatClient bean
  GitHubConfig            -> hub4j GitHub bean
```

## Deployment

Kubernetes manifests are in `k8s/`. See `k8s/setup-postgres.sh` for setup instructions.

The CI/CD pipeline automatically:
- Runs tests on every PR (CI)
- Builds and pushes Docker image on merge to main (CD)
- Keel auto-deploys new images to the cluster

## License

MIT
