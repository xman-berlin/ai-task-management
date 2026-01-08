# Contributing to AI Task Management System

Thank you for your interest in contributing! 🎉

## 📋 Table of Contents
- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [How to Contribute](#how-to-contribute)
- [Development Process](#development-process)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)

## 🤝 Code of Conduct

By participating in this project, you agree to maintain a respectful and inclusive environment for everyone.

## 🚀 Getting Started

1. **Fork the repository**
2. **Clone your fork**
   ```bash
   git clone https://github.com/YOUR-USERNAME/ai-task-management.git
   cd ai-task-management
   ```
3. **Set up upstream**
   ```bash
   git remote add upstream https://github.com/xman-berlin/ai-task-management.git
   ```
4. **Install dependencies and run tests**
   ```bash
   ./mvnw clean install
   ./mvnw test
   ```

## 💡 How to Contribute

### Reporting Bugs
- Use the **Bug Report** template
- Include detailed steps to reproduce
- Provide screenshots if applicable
- Mention your environment details

### Suggesting Features
- Use the **Feature Request** template
- Explain the problem you're solving
- Describe your proposed solution
- Consider alternatives

### AI Agent Enhancements
- Use the **AI Agent Enhancement** template
- Describe current behavior vs. proposed improvement
- Include success metrics
- Consider testing strategy

## 🔄 Development Process

1. **Create a branch**
   ```bash
   git checkout -b feature/your-feature-name
   # or
   git checkout -b fix/your-bug-fix
   ```

2. **Make your changes**
   - Follow coding standards
   - Add/update tests
   - Update documentation

3. **Commit your changes**
   ```bash
   git add .
   git commit -m "feat: add amazing feature"
   ```
   
   **Commit Message Format:**
   - `feat:` New feature
   - `fix:` Bug fix
   - `docs:` Documentation changes
   - `style:` Code style changes (formatting, etc.)
   - `refactor:` Code refactoring
   - `test:` Adding or updating tests
   - `chore:` Maintenance tasks

4. **Keep your branch updated**
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

5. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

## 🔍 Pull Request Process

1. **Ensure all tests pass**
   ```bash
   ./mvnw clean test
   ```

2. **Update documentation** if needed

3. **Fill out the PR template** completely

4. **Request review** from maintainers

5. **Address review comments** promptly

6. **Squash commits** if requested

### PR Requirements
- ✅ All tests pass
- ✅ Code coverage maintained or improved
- ✅ No merge conflicts
- ✅ Follows coding standards
- ✅ Documentation updated
- ✅ Commits follow conventional format

## 📏 Coding Standards

### Java
- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful variable and method names
- Add Javadoc for public APIs
- Keep methods focused and small

### Testing
- Write unit tests for new features
- Maintain minimum 80% code coverage
- Use meaningful test names
- Follow Arrange-Act-Assert pattern

### AI Agents
- Document prompt templates
- Include evaluation metrics
- Add edge case handling
- Consider rate limiting

## 🏷️ Labels

- `bug` - Something isn't working
- `enhancement` - New feature or request
- `ai` - AI agent related
- `documentation` - Documentation improvements
- `good first issue` - Good for newcomers
- `help wanted` - Extra attention needed
- `dependencies` - Dependency updates

## ❓ Questions?

Feel free to:
- Open a discussion on GitHub
- Create an issue with the `question` label
- Contact maintainers

## 📄 License

By contributing, you agree that your contributions will be licensed under the project's license.

---

Thank you for contributing! 🚀

