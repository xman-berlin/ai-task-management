# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.3.0] - 2026-01-13

### Added
- Modernized UI with gradient header, icon buttons, and color-coded badges
- Bootstrap Icons across task list, forms, pagination, and comment actions
- Reaction badges persisted and displayed under comments

### Fixed
- Comment edit UX: icon-button click handling, editable textarea, and caret visibility
- Pagination controls with clear icon affordances
- Post comment button updated with icon for consistency

### Changed
- Task form/header styling for a cleaner, state-of-the-art theme
- Task list action buttons simplified to icons with improved hover/active states

## [0.2.0] - 2026-01-13

### Added
- Task comments feature with HTMX-based posting
- Activity log display showing task change history (status, priority, due date)
- Auto-seeded 5 sample comments per task on startup
- Comment/ActivityLog domain entities with repositories
- ActivityLogService for logging task changes
- task-activity.html fragment for UI
- Proper test configuration for Spring Boot 4 with profile-based beans
- Test fixtures with @ActiveProfiles and TestAiConfig
- Test resources (application-test.yml configuration)

### Fixed
- Circular dependency between TaskService and ActivityLogService using @Lazy
- JPA entity listener initialization during bean creation
- HTMX comment append behavior (preventing duplication)
- Test bean initialization with allow-circular-references
- Form reset after HTMX submission

### Changed
- StartupDataLoader now seeds comments with sample data
- UiController enhanced with comment management endpoints
- Task detail modal includes activity log section

## [0.1.0] - 2026-01-12

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
