# MVVM Pattern Demo Project

This project demonstrates the MVVM (Model-View-ViewModel) architectural pattern with a remote data source, repository, network constants, and extension functions. It is designed to showcase best practices in Android development using Kotlin.

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Setup](#setup)
- [Project Structure](#project-structure)
- [Key Components](#key-components)
- [Dependencies](#dependencies)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Features

- MVVM architecture
- Remote data source using Retrofit
- Repository pattern
- Network constants for API management
- Extension functions for cleaner code

## Architecture

The project follows the MVVM architecture pattern, which helps in separating the UI code from the business logic and data handling code. The main components are:- **Model:** Handles the data layer, including network and database operations.
- **View:** Displays the data and handles user interactions.
- **ViewModel:** Acts as a bridge between the View and Model, handling the business logic.

## Setup

1. **Clone the repository**
2. **Open the project in Android Studio.**
3. **Build the project and run it on an emulator or a physical device.**

## Key Components

### Data Layer

- **Model:** Contains data classes.
- **Repository:** Mediates between the data sources (remote and local) and the rest of the application.
- **Remote Data Source:** Manages network requests using Retrofit.

### UI Layer

- **View:** Activities and Fragments that display the data.
- **ViewModel:** Holds UI-related data and communicates with the Repository.

### Utilities

- **Network Constants:** Contains constants for network configuration.
- **Extension Functions:** Provides utility functions to enhance Kotlin's functionality.

## Dependencies

- Retrofit
- LiveData
- ViewModel
- Kotlin Coroutines
- Dagger/Hilt for Dependency Injection (optional)

Add these dependencies to your `build.gradle` file.

## Usage

### Remote Data Source

Define your API endpoints in an interface:
   ```kotlin
interface ApiService { 
    @GET("endpoint") 
    suspend fun getData(): Response<DataModel> 
}
```
### Repository

Create a repository to manage data operations:
   ```kotlin
class DataRepository(private val apiService: ApiService) { 
    suspend fun fetchData(): DataModel { 
        val response = apiService.getData()  
        if (response.isSuccessful)  { 
            return response.body()!! 
        } else { throw Exception("Error fetching data") } 
    } 
}
```
### Network Constants

Define constants for network configurations:
   ```kotlin
    object NetworkConstants { 
        const val BASE_URL = "https://api.example. com/ "  
    const val TIMEOUT = 60L 
    }
```
## Contributing

Contributions are welcome! Please fork the repository and submit a pull request for review.

## License

This project is licensed under the MIT License - see the [LICENSE](https://opensource.org/licenses/MIT) file for details.