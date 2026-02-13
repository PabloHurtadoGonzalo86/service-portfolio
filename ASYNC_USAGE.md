# Async Portfolio Generation - Usage Examples

## 1. Start Async Portfolio Generation

```bash
curl -X POST http://localhost:8080/api/v1/portfolio/generate/async \
  -H "Content-Type: application/json" \
  -d '{"githubUsername": "octocat"}'
```

Response (202 Accepted):
```json
{
  "jobId": 123,
  "status": "PENDING",
  "message": "Portfolio generation started. Use /api/v1/portfolio/status/123 to check progress."
}
```

## 2. Poll Job Status

```bash
curl http://localhost:8080/api/v1/portfolio/status/123
```

Response while processing:
```json
{
  "jobId": 123,
  "status": "PROCESSING",
  "githubUsername": "octocat",
  "resultId": null,
  "errorMessage": null,
  "createdAt": "2026-02-13T00:30:00Z",
  "updatedAt": "2026-02-13T00:30:15Z"
}
```

Response when completed:
```json
{
  "jobId": 123,
  "status": "COMPLETED",
  "githubUsername": "octocat",
  "resultId": 456,
  "errorMessage": null,
  "createdAt": "2026-02-13T00:30:00Z",
  "updatedAt": "2026-02-13T00:30:42Z"
}
```

## 3. Get Completed Portfolio

```bash
curl http://localhost:8080/api/v1/portfolio/456
```

Response:
```json
{
  "id": 456,
  "githubUsername": "octocat",
  "developerName": "The Octocat",
  "professionalSummary": "...",
  "topSkills": ["JavaScript", "Python", "Ruby"],
  "selectedProjects": [...],
  "skillsByCategory": {...},
  "profileHighlights": [...],
  "totalPublicRepos": 42,
  "createdAt": "2026-02-13T00:30:42Z"
}
```

## Status Flow

```
PENDING (job created, queued)
   ↓
PROCESSING (AI generating portfolio)
   ↓
COMPLETED (portfolio ready, resultId available)
   or
FAILED (error occurred, errorMessage available)
```

## Configuration

Thread pool can be configured via environment variables or application.yaml:

```yaml
app:
  async:
    core-pool-size: 2      # Min threads
    max-pool-size: 5       # Max threads
    queue-capacity: 50     # Queue size
```

## Backward Compatibility

The original synchronous endpoint still works:

```bash
curl -X POST http://localhost:8080/api/v1/portfolio/generate \
  -H "Content-Type: application/json" \
  -d '{"githubUsername": "octocat"}'
```

This blocks until completion and returns the portfolio directly (200 OK).
