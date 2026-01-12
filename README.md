# AI Task Management System

A modern task management application with AI-powered features built with Spring Boot 4 and HTMX.

## Features

- ✅ Task CRUD operations (Create, Read, Update, Delete)
- 🤖 AI service stubs for intelligent task management:
  - Task prioritization
  - Task decomposition into subtasks
  - Deadline prediction
  - Smart notifications
- 🎨 Responsive UI with Thymeleaf + HTMX + Bootstrap 5
- 📊 Real-time updates via WebSocket (scaffolded)
- 💾 H2 in-memory database for development
- 🚀 Auto-seeded with 10 sample AI startup tasks

## Tech Stack

- **Backend**: Spring Boot 4.0.1 (Java 17)
- **Frontend**: Thymeleaf, HTMX 2.0, Bootstrap 5.3
- **Database**: H2 (dev), PostgreSQL (production-ready)
- **Build**: Maven
- **AI**: Service stubs ready for LangChain4j + OpenAI integration

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+

### Run Locally

```bash
# Clone the repository
git clone <your-repo-url>
cd spring-boot-4-test

# Run with dev profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Open http://localhost:18080

### Run Tests

```bash
./mvnw test -q
```

## Project Structure

```
src/main/java/at/geise/test/springboot4test/
├── config/          # Startup configuration & data loaders
├── controller/      # REST & UI controllers
├── domain/          # JPA entities
├── dto/             # Data transfer objects
├── repository/      # Spring Data repositories
└── service/         # Business logic & AI services

src/main/resources/
├── templates/       # Thymeleaf HTML templates
│   └── fragments/   # Reusable UI fragments
├── application.yml          # Main config
└── application-dev.yml      # Dev profile config
```

## API Endpoints

### REST API
- `GET /api/tasks` - List all tasks (with pagination)
- `GET /api/tasks/{id}` - Get task details
- `POST /api/tasks` - Create new task
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task

### AI Endpoints
- `POST /api/ai/prioritize` - Get AI priority suggestion
- `POST /api/ai/decompose` - Decompose task into subtasks
- `POST /api/ai/deadline` - Predict realistic deadline

### UI Endpoints
- `GET /tasks` - Task management dashboard
- `GET /tasks/list` - Task list fragment (HTMX)
- `GET /tasks/new` - New task form (HTMX)
- `GET /tasks/{id}` - Edit task form (HTMX)

## Configuration

### Development Profile (`application-dev.yml`)
- Port: 18080
- Database: H2 in-memory
- H2 Console: http://localhost:18080/h2-console

### Environment Variables
- `OPENAI_API_KEY` - (optional) For AI features when integrated

## Roadmap

See [PLAN.md](PLAN.md) for detailed project plan and roadmap.

### GitHub Project Board

Track development progress and upcoming features on our [Project Board](https://github.com/users/xman-berlin/projects/1).

**Current Features in Development:**
- 📝 [Comments & Activity Log](https://github.com/xman-berlin/ai-task-management/issues/9) - Enable team collaboration with commenting and audit trail
- 🌳 [Subtasks & Task Hierarchy](https://github.com/xman-berlin/ai-task-management/issues/10) - Break down complex tasks with AI-powered decomposition
- 🏷️ [Tags & Smart Filtering](https://github.com/xman-berlin/ai-task-management/issues/11) - Flexible categorization with AI auto-tagging

## Contributing

We welcome contributions! Here's how to get started:

1. Check the [Project Board](https://github.com/users/xman-berlin/projects/1) for open issues
2. Look for issues labeled `good first issue` or `help wanted`
3. Fork the repository and create a feature branch
4. Make your changes and add tests
5. Submit a pull request referencing the issue

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

## License

[Add your license here]


