# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial MVP release
- Task CRUD operations via REST API
- Responsive web UI with Thymeleaf + HTMX + Bootstrap
- AI service stubs (prioritization, decomposition, deadline prediction)
- H2 in-memory database for development
- PostgreSQL support for production
- Auto-seeded sample data (10 AI startup tasks)
- WebSocket support (scaffolded)
- CI/CD workflows
- Issue and PR templates
- Dependabot configuration
- Security scanning workflows

### Changed
- Server port set to 18080 for dev profile
- Switched to Java 17 for compatibility

### Fixed
- HTMX form submission for create/edit/delete operations
- Modal closing behavior after save
- Priority and status dropdown binding in edit form
- Due date display and input formatting
- Delete button URL generation

## [0.0.1] - 2026-01-08

### Added
- Initial project setup
- Spring Boot 4.0.1 with Java 17
- Basic project structure
- Maven configuration

---

## Version Format

- **MAJOR**: Incompatible API changes
- **MINOR**: Backwards-compatible new features
- **PATCH**: Backwards-compatible bug fixes

## Categories

- `Added` for new features
- `Changed` for changes in existing functionality
- `Deprecated` for soon-to-be removed features
- `Removed` for removed features
- `Fixed` for bug fixes
- `Security` for vulnerability fixes

