# Android Clean Architecture Project
This project demonstrates the implementation of Clean Architecture principles in an Android application. It aims to provide a well-structured and maintainable codebase by separating concerns into distinct layers.
### Architecture Overview
The project follows a layered architecture, consisting of the following components:
- **Presentation Layer:** Responsible for handling user interactions, displaying data, and managing UI state. It includes:
  **Fragments**
  **ViewModels**
  **View Binding**
- **Domain Layer:** Contains the business logic of the application, independent of any specific framework or platform. It defines use cases that encapsulate the core functionalities.
- **Data Layer:** Handles data access and persistence. It includes repositories that abstract the data sources (local database, network, etc.) and provide a unified interface to the domain layer.
### Key Features
- **Dependency Injection (Hilt):** Hilt is used for dependency injection, providing a convenient way to manage dependencies and improve testability.
- **Room Database:** Room persistence library is used for local data storage.
- **Retrofit:** Retrofit is used for making network requests.
- **Navigation Component:** The Navigation Component simplifies navigation between different screens and fragments.
- **View Binding:** View Binding is used to interact with UI elements, reducing boilerplate code.
- **Kotlin Coroutines:** Coroutines are used for asynchronous operations, providing a more concise and efficient way to handle background tasks.
### Project Structure
The project is organized into modules:
- **app:** The main application module containing the UI and presentation logic.
- **core:** Contains common components and utilities shared across modules.
- **data:** Implements the data layer, including repositories and data sources.
- **domain:** Defines the use cases and business logic of the application.

## Contributing
Contributions are welcome! If you find any issues or have suggestions for improvements, feel free to open an issue or submit a pull request.
## License
This project is licensed under the MIT License - see the [LICENSE](https://opensource.org/licenses/MIT) file for details.