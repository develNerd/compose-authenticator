# Savit Authenticator - Compose Multiplatform

A cross-platform TOTP (Time-based One-Time Password) authenticator app built with Compose Multiplatform, supporting both Android and iOS.

## Features

- ✅ **TOTP Code Generation**: Generate time-based one-time passwords with real-time countdown and smooth progress indicators
- ✅ **QR Code Scanning**: Unified QR code scanning using QRKit Compose Multiplatform with comprehensive URL decoding
- ✅ **Secure Storage**: KSafe-based secure storage for encryption keys across platforms
- ✅ **Cross-Platform**: Single codebase for Android and iOS with shared business logic
- ✅ **Modern UI**: Material 3 design system with custom progress indicators and real-time updates
- ✅ **Multiplatform Crypto**: Platform-agnostic encryption using cryptography-kotlin library

## Understanding TOTP (Time-based One-Time Password)

### What is TOTP?

TOTP is a cryptographic algorithm that generates unique, time-sensitive passwords based on a shared secret and the current time. It's defined in [RFC 6238](https://datatracker.ietf.org/doc/html/rfc6238) and is widely used for two-factor authentication (2FA).

### How TOTP Works

#### Core Algorithm
1. **Shared Secret**: A unique cryptographic key shared between the client (authenticator app) and server
2. **Time Counter**: Current Unix timestamp divided by a time step (usually 30 seconds)
3. **HMAC Generation**: Apply HMAC-SHA1 to the time counter using the shared secret
4. **Dynamic Truncation**: Extract a 6-8 digit code from the HMAC result

#### Mathematical Formula
```
TOTP = HOTP(K, T)
where:
- K = Shared secret key
- T = (Current Unix time - T0) / X
- T0 = Unix time to start counting (usually 0)
- X = Time step interval (usually 30 seconds)
```

#### Step-by-Step Process
1. **Time Calculation**: `T = floor((unixtime - T0) / 30)`
2. **HMAC-SHA1**: `hmac = HMAC-SHA1(secret, T)`
3. **Dynamic Truncation**:
   - Extract 4 bytes starting from `hmac[hmac[-1] & 0xf]`
   - Convert to integer and take modulo 10^6 for 6-digit code
4. **Zero Padding**: Ensure result is exactly 6 digits with leading zeros

### Security Properties

#### Time Synchronization
- **Window Tolerance**: Servers typically accept codes from ±1 time window (±30 seconds)
- **Replay Protection**: Each code is valid only once within its time window
- **Forward Security**: Past codes cannot be used to generate future codes

#### Cryptographic Strength
- **HMAC-SHA1**: Provides cryptographic integrity and authenticity
- **Secret Key Security**: 160-bit (20 byte) shared secrets provide strong entropy
- **Brute Force Resistance**: 10^6 possible codes with 30-second validity makes attacks impractical

### Implementation Details in This App

#### Key Components
- **Secret Storage**: Encrypted using KSafe with platform-specific secure storage
- **Time Handling**: Uses `kotlinx-datetime` for consistent time calculations across platforms
- **HMAC Implementation**: Platform-agnostic using `cryptography-kotlin` library
- **Real-time Updates**: 100ms refresh cycles for smooth countdown and immediate code updates

#### QR Code Format
Standard OTP Auth URI format:
```
otpauth://totp/Example:user@example.com?secret=JBSWY3DPEHPK3PXP&issuer=Example&algorithm=SHA1&digits=6&period=30
```

#### Security Enhancements
- **Secure Key Generation**: 256-bit encryption keys using cryptographically secure random number generation
- **Cross-Platform Encryption**: Unified encryption layer prevents platform-specific vulnerabilities
- **Thread-Safe Operations**: Proper coroutine context handling prevents race conditions in time calculations

### Why TOTP is Effective

1. **Time-Limited**: Codes expire every 30 seconds, limiting attack windows
2. **Offline Capable**: No network connection required for code generation
3. **Standardized**: RFC 6238 ensures interoperability between different implementations
4. **Proven Security**: Widely adopted by major services (Google, GitHub, AWS, etc.)
5. **User Friendly**: Simple 6-digit codes that are easy to enter manually

## Tech Stack

### Core Technologies
- **Compose Multiplatform 1.6.11**: Shared UI across platforms with animation support
- **Kotlin Multiplatform**: Shared business logic and crypto implementations
- **Koin**: Dependency injection with multiplatform support
- **Room Database**: Local data persistence with multiplatform support and expect/actual pattern
- **QRKit 3.1.3**: Unified QR code scanning and generation across platforms
- **KSafe 1.1.1**: Secure storage solution for encryption keys
- **Cryptography-Kotlin 0.5.0**: Platform-agnostic cryptographic operations
- **kotlinx-datetime**: Consistent time handling across platforms

### Libraries & Dependencies
- **Navigation Compose**: Type-safe navigation with back handler support
- **Ktor**: HTTP client for networking capabilities
- **kotlinx-serialization**: JSON serialization for data persistence
- **Material Icons Extended**: Rich icon set for UI components

## Project Structure

```
composeauthenticator/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/kotlin/          # Shared code
│   │   │   ├── data/                   # Database, models, DAOs with Room multiplatform
│   │   │   ├── di/                     # Dependency injection modules
│   │   │   ├── navigation/             # Navigation setup with thread-safe operations
│   │   │   ├── platform/               # Platform service interfaces and KSafe integration
│   │   │   ├── presentation/           # UI screens, ViewModels, and components
│   │   │   │   ├── ui/screen/          # Compose screens (Main, AddAccount, QRScanner)
│   │   │   │   ├── ui/components/      # Reusable UI components (AccountCard, CustomProgressBar)
│   │   │   │   └── viewmodel/          # ViewModels for business logic
│   │   │   └── utils/                  # Utilities (TOTP, crypto, QR parsing)
│   │   ├── androidMain/kotlin/         # Android-specific implementations
│   │   │   ├── di/                     # Android DI module
│   │   │   ├── data/database/          # Android Room database builder
│   │   │   └── platform/android/       # Android platform services
│   │   └── iosMain/kotlin/             # iOS-specific implementations
│   │       ├── di/                     # iOS DI module
│   │       ├── data/database/          # iOS Room database builder
│   │       └── platform/ios/           # iOS platform services
│   └── build.gradle.kts
├── iosApp/                             # iOS app configuration
│   ├── iosApp/
│   │   ├── ContentView.swift           # SwiftUI integration with Compose
│   │   ├── Info.plist                  # iOS app configuration
│   │   └── iOSApp.swift               # iOS app entry point
│   └── iosApp.xcodeproj/              # Xcode project (manual framework integration)
└── README.md
```

## Building and Running

### Prerequisites

- **Android Studio Hedgehog (2023.1.1) or newer** with Kotlin Multiplatform plugin
- **Xcode 15.0 or newer** (for iOS builds, macOS only)
- **JDK 11 or newer**

### Android

1. Open the project in Android Studio
2. Sync the project to download dependencies
3. Run the `composeApp` configuration on an Android device or emulator

### iOS

The project uses manual framework integration (CocoaPods has been removed for better compatibility):

1. From Android Studio:
   - Select the `iosApp` run configuration
   - Choose your target device/simulator
   - Run the app

2. From Terminal:
   ```bash
   # Build the iOS framework
   ./gradlew :composeApp:linkPodDebugFrameworkIosSimulatorArm64
   
   # Open Xcode project
   open iosApp/iosApp.xcodeproj
   ```

3. In Xcode:
   - Build and run on iOS device or simulator
   - Framework is automatically linked via build phases

## Key Features Implemented

### 🔐 Security & Storage
- **KSafe Integration**: Unified secure storage across Android and iOS platforms
- **Multiplatform Cryptography**: AES-GCM encryption using cryptography-kotlin library
- **Key Generation**: Secure random key generation with proper entropy
- **Thread-Safe Operations**: Main thread enforcement for UI operations on iOS

### 📱 QR Code Scanning & Processing
- **QRKit Multiplatform**: Unified QR scanning library for both platforms
- **Comprehensive URL Decoding**: Proper handling of encoded characters (%20, %40, etc.)
- **Issuer:Account Format Support**: Parse complex OTP URI formats correctly
- **Automatic Format Detection**: Support for standard TOTP URI format `otpauth://totp/...`

### 🎨 User Interface & Experience
- **Real-Time TOTP Updates**: 100ms refresh cycles for smooth countdown
- **Custom Progress Indicators**: Three-phase color progression (Red→Orange→Green)
- **Material 3 Design**: Modern UI components with proper theming
- **Thread-Safe Navigation**: Proper coroutine context handling across platforms

### 💾 Data Persistence
- **Room Multiplatform**: Shared database schema with platform-specific builders
- **Expect/Actual Pattern**: Clean separation of platform implementations
- **Reactive Data Flow**: Kotlin Flow integration with Compose State
- **Koin DI**: Comprehensive dependency injection across all layers

### ⚡ Performance & Compatibility
- **Native Framework Integration**: Manual linking for better iOS compatibility
- **Compose Multiplatform 1.6.11**: Latest stable version with animation support
- **kotlin.native.cacheKind=none**: Optimized for Compose Multiplatform linker


## Development Commands

```bash
# Build for Android
./gradlew assembleDebug

# Build iOS framework for simulator (Apple Silicon)
./gradlew :composeApp:linkPodDebugFrameworkIosSimulatorArm64

# Build iOS framework for device
./gradlew :composeApp:linkPodDebugFrameworkIosArm64

# Full build (all platforms)
./gradlew build

# Run tests
./gradlew check

# Clean build
./gradlew clean

# Generate Room schema files
./gradlew kspCommonMainKotlinMetadata
```

## Configuration Notes

### Gradle Properties
- `kotlin.native.cacheKind=none` - Required for Compose Multiplatform 1.6.11+ compatibility
- Force compatible androidx.core version for QRKit integration

### iOS Considerations
- Manual framework integration (no CocoaPods dependency)
- Requires `Dispatchers.Main.immediate` for thread-safe navigation
- SwiftUI integration via `Main_iosKt.MainViewController()`

### Key Libraries
- **QRKit 3.1.3**: `network.chaintech:qr-kit`
- **KSafe 1.1.1**: `eu.anifantakis:ksafe`
- **Cryptography-Kotlin 0.5.0**: `dev.whyoleg.cryptography`
- **kotlinx-datetime 0.7.1**: Consistent time handling

## License

This project is licensed under the MIT License - see the LICENSE file for details.