# GitHub Actions CI/CD Pipeline for Finance App

### Overview

A CI/CD pipeline automating Docker builds, testing, and query validation for the finance application.

### Features

- **Build Job**: Builds Docker images for stock and capital gains services.
- **Test Job**: Deploys services using Docker Compose and runs automated tests with `pytest`.
- **Query Job**: Runs test queries on the deployed application and records results.
- Stores logs, test reports, and query results as GitHub artifacts.

### Technologies

- GitHub Actions (CI/CD Automation)
- Docker & Docker Compose (Service Deployment)
- Pytest (Automated Testing)

### Workflow Execution

Triggered manually using `workflow_dispatch`.
