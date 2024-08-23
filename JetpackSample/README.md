# Overview

![Overview](https://github.com/MobileAppDevs/Samplecode-Android/blob/master/JetpackSample/images/jetpack_demo.gif)

# This project demonstrates how to build a robust Android application using Jetpack Compose, Retrofit, and ViewModel, following modern Android development best practices.

### Features

*   **Modern UI with Jetpack Compose:** The entire user interface is built using Jetpack Compose, providing a declarative and efficient way to create beautiful UIs.
*   **Navigation:** Seamless navigation between Splash Screen, Login, Signup, and Home screens using Jetpack Compose Navigation.
*   **State Management:** Efficient state management with `ViewModel` to handle UI state changes and interact with the data layer.
*   **Network Requests with Retrofit:**  Retrofit is used to make API calls for login, signup, and fetching data for the home screen.
*   **Theming & Material Design 3:**  Adheres to Material Design 3 guidelines for a consistent and modern look and feel.

### Getting Started

1.  **Clone the repository**
2.  **Open the project in Android Studio:** Import the project into Android Studio (Arctic Fox or later recommended).

3.  **Build and run:** Build and run the app on an emulator or a physical device.

### Project Structure

*   **`ui` Package:** Contains the composable functions for the UI.
    *   **`screens`:** Holds composables for each screen (Splash, Login, Signup, Home).
    *   **`theme`:** Defines the app's theme and styling.
    *   **`navigation`:** Handles navigation between screens.
*   **`viewmodel` Package:** Contains the `ViewModel` classes for managing UI state and data interactions.
*   **`data` Package:**  Contains classes for networking (Retrofit setup) and data models.
*   **`MainActivity.kt`:** The main entry point of the application.

### Contributing

Contributions are welcome! Feel free to open issues or pull requests.

### License

This project is licensed under the MIT License - see the [LICENSE](https://opensource.org/licenses/MIT) file for details.