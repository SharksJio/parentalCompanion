# Contributing to Parental Companion

Thank you for your interest in contributing to Parental Companion! This document provides guidelines for contributing to the project.

## Code of Conduct

- Be respectful and inclusive
- Focus on constructive feedback
- Help maintain a positive community

## How to Contribute

### Reporting Bugs

If you find a bug, please create an issue with:
- Clear description of the bug
- Steps to reproduce
- Expected vs actual behavior
- Screenshots if applicable
- Device information (OS version, device model)

### Suggesting Features

Feature suggestions are welcome! Please create an issue with:
- Clear description of the feature
- Use case and benefits
- Any implementation ideas

### Pull Requests

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
   - Follow the existing code style
   - Write clear commit messages
   - Add tests if applicable
   - Update documentation

4. **Test your changes**
   - Build both apps
   - Test on multiple devices if possible
   - Ensure no regressions

5. **Commit your changes**
   ```bash
   git commit -m "feat: add new feature"
   ```
   Follow conventional commits format:
   - `feat:` for new features
   - `fix:` for bug fixes
   - `docs:` for documentation
   - `style:` for formatting
   - `refactor:` for code restructuring
   - `test:` for tests
   - `chore:` for maintenance

6. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

7. **Create a Pull Request**
   - Provide a clear description
   - Reference any related issues
   - Explain what was changed and why

## Development Setup

1. **Install Prerequisites**
   - Android Studio Arctic Fox or later
   - JDK 17
   - Android SDK (API 24+)

2. **Clone the Repository**
   ```bash
   git clone https://github.com/SharksJio/parentalCompanion.git
   cd parentalCompanion
   ```

3. **Setup Firebase**
   - Create a Firebase project
   - Add Android apps for both parent and child
   - Download and replace `google-services.json` files

4. **Build the Project**
   ```bash
   ./gradlew build
   ```

## Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Keep functions small and focused
- Use MVVM architecture pattern

### Kotlin Style Guide

```kotlin
// Class names: PascalCase
class MyViewModel : ViewModel()

// Function names: camelCase
fun observeData() { }

// Variables: camelCase
val deviceId = "123"

// Constants: UPPER_SNAKE_CASE
const val MAX_RETRIES = 3

// Private properties: prefix with underscore
private val _data = MutableLiveData<String>()
val data: LiveData<String> = _data
```

## Project Structure

- Follow existing package structure
- Keep related files together
- Use appropriate layers (data, ui, service, etc.)

## Testing

- Write unit tests for ViewModels
- Write integration tests for Repositories
- Test on multiple Android versions
- Test both apps independently and together

## Documentation

- Update README.md if needed
- Update IMPLEMENTATION.md for major changes
- Add KDoc comments to public APIs
- Update inline comments as needed

## Security

- Never commit sensitive data (API keys, passwords)
- Use placeholder values for configuration files
- Report security issues privately via email
- Follow Android security best practices

## Review Process

All contributions go through code review:
- Code quality and style
- Functionality and correctness
- Test coverage
- Documentation completeness

## Questions?

If you have questions:
- Check existing documentation
- Search existing issues
- Create a new issue for discussion

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

Thank you for contributing to Parental Companion!
