# Outdoorsy 🏕️

**Outdoorsy** is a modern Android outdoor activity planner that helps users plan activities based on time, location, and weather conditions. The app leverages AI to analyze weather forecasts and provide intelligent recommendations about activity suitability, clothing tips, and more.

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

- Plan outdoor activities (hiking, gardening, camping, cycling, running, etc.)
- Select location from saved locations
- **Date and time range selection** with start/end dates and times
- **Add custom activities** to your activity list
- **Delete activities** you no longer need
- **AI-powered analysis** that evaluates weather suitability
- Receive suitability scores (Very Good, Good, Fair, Bad, Very Bad)
- Get personalized clothing recommendations based on weather
- Weather-specific tips for your chosen activity
- Quick navigation to shopping for recommended gear

### 📜 Activity History

- View history of all planned activities **sorted from newest to oldest**
- Track activity details including location, time range, date, and suitability
- See suitability scores and labels for each activity
- Activity-specific icons (cycling, hiking, running, beach, photography, dog walking)

### 🛒 Smart Shopping

- Browse outdoor gear recommendations (hiking boots, camping tents, jackets, backpacks)
- **AI-powered personalized recommendations** based on your planned activities' clothing suggestions
- Real-time currency conversion (USD, EUR, GBP) with **local caching** for offline rate access
- Integration with eBay's Browse API for product listings

### ⚙️ Settings & Customization

- Temperature unit toggle (Metric/Imperial)
- App theme selection (Light/Dark/System)
- Language support (English/Finnish)
- Currency preference for shopping

### 📱 Home Screen Widget (WIP)

- Glance-based weather widget for quick access
- Displays current location weather at a glance
- Auto-updates via WorkManager
- Tap to open the full app

---

## 🏗️ Architecture

Outdoorsy follows **Clean Architecture** principles combined with the **MVVM (Model-View-ViewModel)** pattern, ensuring separation of concerns, testability, and maintainability.

```
┌─────────────────────────────────────────────────────────────┐
│                      Presentation Layer                     │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Screens   │  │  ViewModels │  │   UI Components     │  │
│  │  (Compose)  │  │   (State)   │  │   (Reusable)        │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌──────────────────────────────────────────────────────────────┐
│                       Domain Layer                           │
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────────────┐  │
│  │   Models    │  │  Use Cases  │  │ Repository Interfaces│  │
│  │  (Entities) │  │  (Business) │  │   (Contracts)        │  │
│  └─────────────┘  └─────────────┘  └──────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        Data Layer                           │
│  ┌─────────────────────┐  ┌─────────────────────────────┐   │
│  │      Remote         │  │           Local             │   │
│  │  ┌───────────────┐  │  │  ┌───────────────────────┐  │   │
│  │  │ Retrofit APIs │  │  │  │    Room Database      │  │   │
│  │  │ - Weather     │  │  │  │  - Locations          │  │   │
│  │  │ - Forecast    │  │  │  │  - Activities         │  │   │
│  │  │ - eBay        │  │  │  │  - Activity Logs      │  │   │
│  │  │ - Currency    │  │  │  │  - Currency Rates     │  │   │
│  │  │ - AI Assist   │  │  │  ├───────────────────────┤  │   │
│  │  └───────────────┘  │  │  │   DataStore Prefs     │  │   │
│  └─────────────────────┘  │  │  - Settings           │  │   │
│                           │  │  - Search History     │  │   │
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
│   │   │   ├── CurrencyRateDao.kt # Currency rate caching
│   │   │   └── LocationDao.kt
│   │   ├── datastore/             # DataStore preferences
│   │   │   └── SearchHistoryRepository.kt
│   │   └── entity/                # Room entities
│   │       ├── ActivityEntity.kt
│   │       ├── ActivityLogEntity.kt
│   │       ├── CurrencyRateEntity.kt
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
│   └── repository/                # Repository implementations
│       ├── ActivityLogRepositoryImpl.kt
│       ├── ActivityRepositoryImpl.kt
│       ├── AssistantRepositoryImpl.kt
│       ├── CurrencyRepositoryImpl.kt
│       ├── EbayRepositoryImpl.kt
│       ├── ForecastRepositoryImpl.kt
│       ├── LocationRepositoryImpl.kt
│       ├── SettingsRepositoryImpl.kt
│       └── WeatherRepositoryImpl.kt
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
│       ├── CoroutinesModule.kt    # Dispatcher injection for testability
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
│   │       ├── components/        # Weather components
│   │       │   ├── City.kt
│   │       │   ├── ForecastItem.kt
│   │       │   ├── Main.kt
│   │       │   ├── Weather.kt
│   │       │   ├── WeatherSys.kt
│   │       │   └── Wind.kt
│   │       ├── ForecastResponse.kt
│   │       └── WeatherResponse.kt
│   ├── repository/                # Repository interfaces (contracts)
│   │   ├── ActivityLogRepository.kt
│   │   ├── ActivityRepository.kt
│   │   ├── AssistantRepository.kt
│   │   ├── CurrencyRepository.kt
│   │   ├── EbayRepository.kt
│   │   ├── ForecastRepository.kt
│   │   ├── LocationRepository.kt
│   │   ├── SettingsRepository.kt
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
│   │       ├── DatePickerField.kt      # Date selection
│   │       ├── EditableFilteringInput.kt
│   │       ├── HelpTooltip.kt          # Help tooltips
│   │       ├── RecommendationCard.kt
│   │       ├── ShopMessageCard.kt      # Navigation to shopping
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

## 🧪 Testing

The project includes comprehensive **unit tests** and **UI/instrumented tests** for ensuring code quality and reliability.

### Test Structure

```
app/src/test/java/com/example/outdoorsy/     # Unit Tests
├── data/
│   └── repository/
│       ├── ActivityLogRepositoryImplTest.kt
│       ├── ActivityRepositoryImplTest.kt
│       ├── EbayRepositoryImplTest.kt
│       ├── SettingsRepositoryImplTest.kt
│       └── WeatherRepositoryImplTest.kt
└── ui/
    ├── activity/
    │   └── ActivityViewModelTest.kt
    ├── history/
    │   └── HistoryViewModelTest.kt
    ├── settings/
    │   └── SettingsViewModelTest.kt
    ├── shopping/
    │   └── ShoppingViewModelTest.kt
    └── weather/
        ├── WeatherMappersTest.kt
        └── WeatherViewModelTest.kt

app/src/androidTest/java/com/example/outdoorsy/  # Instrumented Tests
├── DatabaseTest.kt                    # Room database tests
└── ui/
    ├── activity/
    │   └── ActivityScreenTest.kt
    ├── history/
    │   └── HistoryScreenTest.kt
    ├── settings/
    │   └── SettingsScreenTest.kt
    ├── shopping/
    │   └── ShoppingScreenTest.kt
    └── weather/
        └── components/
            └── WeatherComponentsTest.kt
```

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run all instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests "com.example.outdoorsy.ui.history.HistoryViewModelTest"
```

### Testing Libraries

| Library             | Purpose                 |
| ------------------- | ----------------------- |
| **JUnit 4**         | Unit test framework     |
| **MockK**           | Kotlin mocking library  |
| **Turbine**         | Testing Kotlin Flows    |
| **Google Truth**    | Fluent assertions       |
| **Compose UI Test** | Jetpack Compose testing |
| **Espresso**        | Android UI testing      |

---

## 🛠️ Tech Stack

| Category                 | Technologies                                                   |
| ------------------------ | -------------------------------------------------------------- |
| **Language**             | Kotlin 2.2.21                                                  |
| **UI Framework**         | Jetpack Compose with Material 3                                |
| **Architecture**         | Clean Architecture + MVVM                                      |
| **Dependency Injection** | Hilt 2.57.2                                                    |
| **Networking**           | Retrofit 3.0 + OkHttp                                          |
| **Local Database**       | Room 2.8.3                                                     |
| **Preferences**          | DataStore Preferences                                          |
| **Image Loading**        | Coil 3.3.0                                                     |
| **Location Services**    | Google Play Services Location                                  |
| **Background Work**      | WorkManager                                                    |
| **Widget**               | Jetpack Glance                                                 |
| **Permissions**          | Accompanist Permissions                                        |
| **Testing**              | JUnit, MockK, Turbine, Google Truth, Espresso, Compose UI Test |

---

## 🔌 API Integrations

### 1. OpenWeatherMap API

- **Purpose**: Current weather data and 5-day forecasts
- **Endpoints Used**:
  - `/data/2.5/weather` - Current weather
  - `/data/2.5/forecast` - 5-day forecast
  - `/geo/1.0/direct` - Geocoding (city to coordinates)
- **Documentation**: [OpenWeatherMap API](https://openweathermap.org/api)
- **Key Instructions**: [OpenWeatherMap API key instructions](./docs/openweathermap-apikey-instructions.md)

### 2. eBay Browse API

- **Purpose**: Product search for outdoor gear recommendations
- **Features**: OAuth 2.0 authentication, product listings with images and prices
- **Documentation**: [eBay Browse API](https://developer.ebay.com/api-docs/buy/browse/overview.html)
- **Key Instructions**: [ebay Browse API key instructions](./docs/ebay-apikey-instructions.md)

### 3. Currency API

- **Purpose**: Real-time currency conversion for shopping prices
- **Features**: Results are cached locally in Room database for offline access
- **Supported Currencies**: USD, EUR, GBP
- **Documentation**: [CurrencyAPI](https://currencyapi.com/)
- **Key Instructions**: [Currency API key instructions](./docs/currencyapi-apikey-instructions.md)

### 4. AI Assistant API

- **Purpose**: Intelligent activity feasibility analysis
- **Features**: Weather-based recommendations, clothing tips, suitability scoring
- **Documentation**: [OpenWeatherMap API](https://openweathermap.org/api/one-call-3#ai_weather_assistant)
- **Key Instructions**: [OpenWeatherMap API key instructions](./docs/openweathermap-apikey-instructions.md)

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug (2024.2.1) or newer
- JDK 11+
- Android SDK 35+ (minimum) / 36 (target)
- API keys for the following services:
  - [OpenWeatherMap](https://openweathermap.org/api) (free tier available, but One Call API 3.0 required for AI features)
  - [eBay Developer Program](https://developer.ebay.com/) (free tier available)
  - [CurrencyAPI](https://currencyapi.com/) (free tier available)

### Setup Instructions

1. **Clone the repository**

   ```bash
   git clone https://github.com/SakuJuuH/outdoorsy.git
   cd outdoorsy
   ```

2. **Configure API keys**

   Create a `local.properties` or rename the `local.properties.example` file in the project root:

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
   - Build the project: `Build > Assemble Project`

5. **Run the app**
   - Select a device/emulator (API 35+)
   - Click `Run 'app'`

### Permissions

The app requires the following permissions:

- `ACCESS_FINE_LOCATION` - For GPS-based weather
- `ACCESS_COARSE_LOCATION` - For approximate location

---

## 📱 Screens Overview

| Screen       | Description                                                                  |
| ------------ | ---------------------------------------------------------------------------- |
| **Weather**  | Main dashboard showing weather for saved locations with forecasts            |
| **History**  | View past activity plans sorted by date (newest first) with suitability info |
| **Activity** | Plan new activities with date/time selection and AI-powered weather analysis |
| **Shopping** | Browse and shop for outdoor gear with currency conversion                    |
| **Settings** | Customize temperature units, theme, language, and currency                   |

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

Internet access is required for most of the app's functionality.

---

## 🔮 Future Improvements

- [ ] Implement notifications for optimal activity times
- [ ] Add social features (share activities with friends)
- [ ] Integrate more shopping platforms
- [ ] Add offline support with cached weather data
- [ ] Implement activity reminders
- [ ] Add weather alerts and warnings
- [ ] Support for more languages
- [ ] Add activity statistics and insights

---

## 📄 License

This project is for educational purposes, licensed under the [MIT license](LICENSE). Please ensure you have proper licenses for any APIs used in production.

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
