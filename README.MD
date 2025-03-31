# Screen Saver App

A modern digital clock screen saver built with Compose Multiplatform for desktop platforms.

## Introduction
Screen Saver App is a Compose Multiplatform application that provides a digital clock screen saver for desktop platforms. The application is built using Kotlin and Jetpack Compose, allowing for a modern, declarative UI approach. This app was created with JetBrains Junie.

## Features
- Animated digital clock display
- Modern UI using Material Design
- Cross-platform support through Compose Multiplatform
- MVVM + clean architecture
- Pure Kotlin libraries only

## Project Structure
The project follows a standard Compose Multiplatform structure:

- **composeApp/** - Main application module
  - **src/commonMain/** - Shared code for all platforms
    - **kotlin/com/droidslife/screensaver/** - Main package containing application code
      - **App.kt** - Main application entry point
      - **DigitalClockApp.kt** - Implementation of the digital clock screen saver
      - **digitalClock/** - Original digital clock implementation
      - **digitalClock2/** - Alternative digital clock implementation
      - **theme/** - UI theming and styling
  - **src/jvmMain/** - JVM-specific code for desktop platforms
  - **desktopAppIcons/** - Icons for the desktop application

## Development
### Running the Application

To run the desktop application:
```
./gradlew :composeApp:run
```

For hot reload:
```
./gradlew composeApp:runHot
```

### Testing
To run desktop UI tests:
```
./gradlew :composeApp:jvmTest
```

## Technology Stack
- **Kotlin** - Primary programming language
- **Compose Multiplatform** - UI framework
- **kotlinx.datetime** - Date and time handling
- **Gradle** - Build system

## Architecture
The application follows a simple architecture:
1. The main `App` composable sets up the UI container and theme
2. The `DigitalClockApp` composable manages the state and updates of the clock
3. The `DigitalClock` and `DigitalDigit` composables handle the rendering of individual clock components

## Future Enhancements
Potential areas for enhancement include:
- Additional screen saver styles beyond the digital clock
- Customization options for colors and animations
- Support for additional platforms (iOS, Web, etc.)

## Screenshots

### Main Clock Display
![Main Clock Display](screenshots/Screenshot%202025-03-31%20163012.png)

### Alternative Clock Style
![Alternative Clock Style](screenshots/Screenshot%202025-03-31%20163210.png)

### Clock with Dark Theme
![Clock with Dark Theme](screenshots/Screenshot%202025-03-31%20163321.png)

### Clock Animation Frame
![Clock Animation Frame](screenshots/Screenshot%202025-03-31%20163347.png)

### Clock with Settings Panel
![Clock with Settings Panel](screenshots/Screenshot%202025-03-31%20163530.png)

### Full Screen Mode
![Full Screen Mode](screenshots/Screenshot%202025-03-31%20163721.png)

### Application Overview
![Application Overview](screenshots/Screenshot%202025-03-31%20164121.png)
