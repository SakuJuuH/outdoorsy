# Outdoorsy 🏕️

**Outdoorsy** is a modern Android outdoor activity planner that helps users plan activities based on
time, location, and weather conditions. The app leverages AI to analyze weather forecasts and
provide intelligent recommendations about activity suitability, clothing tips, and more.

![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-purple?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-blue?logo=android)
![Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture%20+%20MVVM-green)

---

## ✨ Features

### 🌤️ Weather Dashboard

- View current weather conditions for multiple saved locations
- GPS-based automatic location detection
- 5-day weather forecasts with detailed daily breakdowns
- Weather details including temperature, humidity, wind speed, and conditions
- Search and add new locations with recent search history
- Support for metric (°C) and imperial (°F) temperature units

### 🏃 Activity Planning

- Plan outdoor activities (hiking, gardening, camping, etc.)
- Select location, date, and time range for your activity
- **AI-powered analysis** that evaluates weather suitability
- Receive suitability scores and labels (Excellent, Very Good, Good, Fair, Bad)
- Get personalized clothing recommendations based on weather
- Weather-specific tips for your chosen activity

### 📜 Activity History

- View history of all planned activities
- Track activity details including location, time, and conditions
- Search through past activities
- Activity-specific icons for visual clarity

### 🛒 Smart Shopping

- Browse outdoor gear recommendations (hiking boots, camping tents, jackets, backpacks)
- **AI-powered personalized recommendations** based on your planned activities
- Real-time currency conversion (USD, EUR, GBP)
- Integration with eBay's Browse API for product listings

### ⚙️ Settings & Customization

- Temperature unit toggle (Metric/Imperial)
- App theme selection (Light/Dark/System)
- Language support (English/Finnish)
- Currency preference for shopping

### 📱 Home Screen Widget

- Glance-based weather widget for quick access
- Displays current location weather at a glance
- Auto-updates via WorkManager
- Tap to open the full app

---

## 🏗️ Architecture

Outdoorsy follows **Clean Architecture** principles combined with the **MVVM (Model-View-ViewModel)
** pattern, ensuring separation of concerns, testability, and maintainability.

```
┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Screens   │  │  ViewModels │  │   UI Components     │  │
│  │  (Compose)  │  │   (State)   │  │   (Reusable)        │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       Domain Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Models    │  │  Use Cases  │  │ Repository Interfaces│  │
│  │  (Entities) │  │  (Business) │  │   (Contracts)       │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        Data Layer                            │
│  ┌─────────────────────┐  ┌─────────────────────────────┐   │
│  │      Remote         │  │           Local             │   │
│  │  ┌───────────────┐  │  │  ┌───────────────────────┐  │   │
│  │  │ Retrofit APIs │  │  │  │    Room Database      │  │   │
│  │  │ - Weather     │  │  │  │  - Locations          │  │   │
│  │  │ - Forecast    │  │  │  │  - Activities         │  │   │
│  │  │ - eBay        │  │  │  │  - Activity Logs      │  │   │
│  │  │ - Currency    │  │  │  ├───────────────────────┤  │   │
│  │  │ - AI Assist   │  │  │  │   DataStore Prefs     │  │   │
│  │  └───────────────┘  │  │  │  - Settings           │  │   │
│  └─────────────────────┘  │  │  - Search History     │  │   │
│                           │  └───────────────────────┘  │   │
│                           └─────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
app/src/main/java/com/example/outdoorsy/
├── data/                          # Data Layer
│   ├── local/                     # Local data sources
│   │   ├── AppDatabase.kt         # Room database configuration
│   │   ├── dao/                   # Data Access Objects
│   │   │   ├── ActivityDao.kt
│   │   │   ├── ActivityLogDao.kt
│   │   │   └── LocationDao.kt
│   │   ├── datastore/             # DataStore preferences
│   │   │   └── SearchHistoryRepository.kt
│   │   └── entity/                # Room entities
│   │       ├── ActivityEntity.kt
│   │       ├── ActivityLogEntity.kt
│   │       └── LocationEntity.kt
│   ├── remote/                    # Remote data sources
│   │   ├── AiAssistantApiService.kt
│   │   ├── CurrencyApiService.kt
│   │   ├── EbayApiService.kt
│   │   ├── EbayAuthService.kt
│   │   ├── ForecastApiService.kt
│   │   ├── WeatherApiService.kt
│   │   └── dto/                   # Data Transfer Objects
│   │       ├── assistant/
│   │       ├── currency/
│   │       ├── ebay/
│   │       ├── geocoding/
│   │       └── weather/
│   ├── repository/                # Repository implementations
│   │   ├── ActivityLogRepositoryImpl.kt
│   │   ├── ActivityRepositoryImpl.kt
│   │   ├── AssistantRepositoryImpl.kt
│   │   ├── CurrencyRepositoryImpl.kt
│   │   ├── EbayRepositoryImpl.kt
│   │   ├── ForecastRepositoryImpl.kt
│   │   ├── LocationRepositoryImpl.kt
│   │   ├── SettingsRepository.kt
│   │   └── WeatherRepositoryImpl.kt
│   └── test/                      # Test data
│       └── ActivitiesData.kt
│
├── di/                            # Dependency Injection
│   ├── EbayTokenHolder.kt
│   ├── NetworkQualifiers.kt
│   ├── interceptor/               # OkHttp Interceptors
│   │   ├── CurrencyApiAuthInterceptor.kt
│   │   ├── EbayAccessAuthInterceptor.kt
│   │   ├── EbayAuthInterceptor.kt
│   │   └── OpenWeatherInterceptor.kt
│   └── module/                    # Hilt Modules
│       ├── DatabaseModule.kt
│       ├── DataStoreModule.kt
│       ├── LocationModule.kt
│       ├── NetworkModule.kt
│       └── RepositoryModule.kt
│
├── domain/                        # Domain Layer
│   ├── model/                     # Domain models
│   │   ├── Activity.kt
│   │   ├── ActivityLog.kt
│   │   ├── Location.kt
│   │   ├── ebay/                  # eBay domain models
│   │   │   ├── Category.kt
│   │   │   ├── EbayItem.kt
│   │   │   └── Price.kt
│   │   └── weather/               # Weather domain models
│   │       ├── City.kt
│   │       ├── Clouds.kt
│   │       ├── Coord.kt
│   │       ├── ForecastItem.kt
│   │       ├── ForecastResponse.kt
│   │       ├── Main.kt
│   │       ├── Weather.kt
│   │       ├── WeatherResponse.kt
│   │       └── Wind.kt
│   ├── repository/                # Repository interfaces
│   │   ├── ActivityLogRepository.kt
│   │   ├── ActivityRepository.kt
│   │   ├── AssistantRepository.kt
│   │   ├── CurrencyRepository.kt
│   │   ├── EbayRepository.kt
│   │   ├── ForecastRepository.kt
│   │   ├── LocationRepository.kt
│   │   └── WeatherRepository.kt
│   └── usecase/                   # Use cases
│       ├── GetAiAssistant.kt
│       ├── GetCurrentWeather.kt
│       └── GetForecast.kt
│
├── ui/                            # Presentation Layer
│   ├── activity/                  # Activity Planning Screen
│   │   ├── ActivityScreen.kt
│   │   ├── ActivityUiState.kt
│   │   ├── ActivityViewModel.kt
│   │   └── components/
│   │       ├── EditableFilteringInput.kt
│   │       ├── RecommendationCard.kt
│   │       └── TimePickerField.kt
│   ├── components/                # Shared UI components
│   │   ├── AppBottomNavBar.kt
│   │   ├── Button.kt
│   │   ├── ScreenTitle.kt
│   │   └── SectionTitle.kt
│   ├── history/                   # History Screen
│   │   ├── HistoryScreen.kt
│   │   ├── HistoryViewModel.kt
│   │   ├── components/
│   │   │   ├── ActivityHistoryCard.kt
│   │   │   └── ConditionRatingPill.kt
│   │   └── model/
│   │       └── ActivityHistoryItem.kt
│   ├── main/                      # Main App Container
│   │   ├── AppContainer.kt
│   │   └── MainViewModel.kt
│   ├── navigation/                # Navigation
│   │   ├── AppNavHost.kt
│   │   └── Screen.kt
│   ├── search/                    # Search Screen
│   │   └── SearchScreen.kt
│   ├── settings/                  # Settings Screen
│   │   ├── SettingsScreen.kt
│   │   ├── SettingsUiState.kt
│   │   ├── SettingsViewModel.kt
│   │   └── components/
│   │       ├── BaseSettingsItem.kt
│   │       ├── SettingsItem.kt
│   │       ├── SettingsItemWithSwitch.kt
│   │       └── SingleChoiceDialog.kt
│   ├── shopping/                  # Shopping Screen
│   │   ├── ShoppingScreen.kt
│   │   ├── ShoppingUiState.kt
│   │   ├── ShoppingViewModel.kt
│   │   └── components/
│   │       ├── ProductCard.kt
│   │       └── RecommendedItemsSection.kt
│   ├── theme/                     # App Theme
│   │   ├── Color.kt
│   │   ├── Spacing.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── weather/                   # Weather Screen
│   │   ├── WeatherScreen.kt
│   │   ├── WeatherViewModel.kt
│   │   ├── components/
│   │   │   ├── ForecastCard.kt
│   │   │   ├── ForecastDayItem.kt
│   │   │   ├── WeatherCard.kt
│   │   │   ├── WeatherDetailCard.kt
│   │   │   ├── WeatherDetailsGrid.kt
│   │   │   └── WeatherPageIndicator.kt
│   │   ├── mappers/
│   │   │   └── WeatherMappers.kt
│   │   └── model/
│   │       ├── DailyForecast.kt
│   │       └── WeatherData.kt
│   └── widget/                    # Home Screen Widget
│       ├── WeatherWidget.kt
│       ├── WeatherWidgetReceiver.kt
│       ├── model/
│       │   └── WeatherWidgetData.kt
│       └── worker/
│           └── WeatherWidgetWorker.kt
│
├── utils/                         # Utilities
│   ├── Constants.kt               # App constants & enums
│   ├── DateTimeConverters.kt      # Room type converters
│   ├── LocaleHelper.kt            # Localization helper
│   └── WeatherPromptProvider.kt   # AI prompt builder
│
├── MainActivity.kt                # Main Activity
└── OutdoorsyApplication.kt        # Application class
```

---

## 🛠️ Tech Stack

| Category                 | Technologies                    |
|--------------------------|---------------------------------|
| **Language**             | Kotlin 2.2.21                   |
| **UI Framework**         | Jetpack Compose with Material 3 |
| **Architecture**         | Clean Architecture + MVVM       |
| **Dependency Injection** | Hilt 2.57.2                     |
| **Networking**           | Retrofit 3.0 + OkHttp           |
| **Local Database**       | Room 2.8.3                      |
| **Preferences**          | DataStore Preferences           |
| **Image Loading**        | Coil 3.3.0                      |
| **Location Services**    | Google Play Services Location   |
| **Background Work**      | WorkManager                     |
| **Widget**               | Jetpack Glance                  |
| **Permissions**          | Accompanist Permissions         |
| **Testing**              | JUnit, Google Truth, Espresso   |

---

## 🔌 API Integrations

### 1. OpenWeatherMap API

- **Purpose**: Current weather data and 5-day forecasts
- **Endpoints Used**:
    - `/data/2.5/weather` - Current weather
    - `/data/2.5/forecast` - 5-day forecast
    - `/geo/1.0/direct` - Geocoding (city to coordinates)
- **Documentation**: [OpenWeatherMap API](https://openweathermap.org/api)

### 2. eBay Browse API

- **Purpose**: Product search for outdoor gear recommendations
- **Features**: OAuth 2.0 authentication, product listings with images and prices
- **Documentation**: [eBay Browse API](https://developer.ebay.com/api-docs/buy/browse/overview.html)

### 3. Currency API

- **Purpose**: Real-time currency conversion for shopping prices
- **Supported Currencies**: USD, EUR, GBP
- **Documentation**: [CurrencyAPI](https://currencyapi.com/)

### 4. AI Assistant API

- **Purpose**: Intelligent activity feasibility analysis
- **Features**: Weather-based recommendations, clothing tips, suitability scoring

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2.1) or newer
- JDK 11+
- Android SDK 35+ (minimum) / 36 (target)
- API keys for the following services:
    - [OpenWeatherMap](https://openweathermap.org/api) (free tier available)
    - [eBay Developer Program](https://developer.ebay.com/) (free tier available)
    - [CurrencyAPI](https://currencyapi.com/) (free tier available)

### Setup Instructions

1. **Clone the repository**

   ```bash
   git clone https://github.com/SakuJuuH/outdoorsy.git
   cd outdoorsy
   ```

2. **Configure API keys**

   Create or edit the `local.properties` file in the project root:

   ```properties
   sdk.dir=/path/to/your/Android/sdk
   OPENWEATHER_API_KEY=your_openweather_api_key_here
   EBAY_BASIC_KEY=your_ebay_basic_key_here
   CURRENCY_API_KEY=your_currency_api_key_here
   ```

3. **Open in Android Studio**

    - Open Android Studio
    - Select "Open an existing project"
    - Navigate to the cloned directory

4. **Sync and Build**

    - Wait for Gradle sync to complete
    - Build the project: `Build > Make Project`

5. **Run the app**
    - Select a device/emulator (API 35+)
    - Click `Run 'app'`

### Permissions

The app requires the following permissions:

- `ACCESS_FINE_LOCATION` - For GPS-based weather
- `ACCESS_COARSE_LOCATION` - For approximate location

---

## 📱 Screens Overview

| Screen       | Description                                                       |
|--------------|-------------------------------------------------------------------|
| **Weather**  | Main dashboard showing weather for saved locations with forecasts |
| **History**  | View past activity plans with their conditions and suitability    |
| **Activity** | Plan new activities with AI-powered weather analysis              |
| **Shopping** | Browse and shop for outdoor gear with currency conversion         |
| **Settings** | Customize temperature units, theme, language, and currency        |

---

## 🧪 Testing

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

---

## 🌐 Localization

The app currently supports:

- 🇺🇸 English (default)
- 🇫🇮 Finnish

To add a new language:

1. Create a new `values-{locale}` folder in `res/`
2. Add translated `strings.xml`
3. Update `AppLanguage` enum in `Constants.kt`

---

## 📋 Requirements

| Requirement | Value           |
|-------------|-----------------|
| Minimum SDK | 35 (Android 15) |
| Target SDK  | 36              |
| Compile SDK | 36              |
| Kotlin      | 2.2.21          |
| Gradle      | 8.13.1          |

---

## 🔮 Future Improvements

- [ ] Add more outdoor activities
- [ ] Implement notifications for optimal activity times
- [ ] Add social features (share activities with friends)
- [ ] Integrate more shopping platforms
- [ ] Add offline support with cached weather data
- [ ] Implement activity reminders
- [ ] Add weather alerts and warnings
- [ ] Support for more languages

---

## 📄 License

This project is for educational purposes. Please ensure you have proper licenses for any APIs used
in production.

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📞 Contact

For questions or feedback, please open an issue on GitHub.

---

<p align="center">
  Made with ❤️ using Kotlin and Jetpack Compose
</p>
