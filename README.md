# Water Air and You - Customer Portal

Customer companion app for Water Air and You, a water and air quality service business. View equipment status, upcoming appointments, invoices, water test reports, and request service - all from your phone.

Built with Kotlin Multiplatform. Runs on Android and iOS.

## Features

- Dashboard with equipment health overview, next appointment, invoice balance, and water testing status
- Equipment tracking with service due dates, sub-components, and service history
- Invoice viewing with credit card payment
- Appointment scheduling (view upcoming, request service)
- Water test report viewing
- Push notifications for appointment confirmations, invoice status, and equipment due reminders
- Salt delivery ordering
- Theme switcher (Metro flat or glassmorphic gel UI) with light and dark modes

## Tech Stack

| Layer | Technology |
|---|---|
| Shared logic & UI | Kotlin Multiplatform + Compose Multiplatform |
| HTTP client | Ktor 3.x (OkHttp engine on Android, Darwin on iOS) |
| Serialization | kotlinx.serialization |
| Persistence | multiplatform-settings |
| Architecture | ViewModel + StateFlow, no DI framework |

## Requirements

- Android SDK 35 (API 35), minSdk 24
- iOS 16.0+
- JDK 17+
- Xcode 15+ (for iOS builds)

## Setup

```
git clone https://github.com/Juls-by-Strong/Water-Air-and-You.git
cd WaterAirandYou
```

## Build

```bash
# Android debug
./gradlew composeApp:assembleDebug

# Android release
./gradlew composeApp:assembleRelease

# iOS framework (called from Xcode project)
./gradlew composeApp:embedAndSignAppleFrameworkForXcode
```

The iOS project is in `iosApp/` and uses XcodeGen (`project.yml`). Open Xcode workspace after running `xcodegen` in that directory.

## Configuration

- **API**: `https://waterairandyoumvp.myusa.cloud/api/public/` (set in `ApiService.kt`)
- **Android DNS fallback**: Hard-coded IP `172.241.164.34` for the API hostname (system DNS sometimes fails on some networks)
- **Contact**: `309-643-1342`, `info@waterairandyou.com`

## Project Structure

```
composeApp/src/
  commonMain/kotlin/com/crotsertech/waterairandyoumvp/
    App.kt                    -- Root composable, navigation, theme setup
    data/api/                 -- ApiService, TokenRepository, HttpClientFactory
    data/model/               -- Request/response models
    ui/screens/               -- LoginScreen, DashboardScreen, EquipmentScreen, etc.
    ui/screens/modals/        -- ServiceHistory, SaltDelivery, InvoiceDetail modals
    ui/navigation/            -- Screen routes, BottomNavBar, AppNavHost
    ui/viewmodel/             -- ViewModels for each screen
    ui/components/            -- Shared UI components (NeumorphicCard, WayModal, etc.)
    theme/                    -- Theme system (Metro/Glassmorphic, light/dark)
    platform/                 -- expect/actual for URL opening, notifications
    notification/             -- Polling scheduler and dedup tracker
  androidMain/                -- MainActivity, foreground service, OkHttp engine
  iosMain/                    -- MainViewController, Darwin engine
```

## Versioning

Version is auto-generated at build time from the timestamp (`YYMMDD.HHmm`) and displayed on the Settings screen and login screen.

## License

All Rights Reserved. Copyright (c) 2026 Juls by Strong.

For licensing inquiries, contact Judy Strong, President, Juls by Strong.
