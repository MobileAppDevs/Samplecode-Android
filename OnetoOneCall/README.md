# Agora One-to-One Voice Calling

This project demonstrates how to implement one-to-one voice calling functionality in an Android application using the Agora RTC SDK. It provides a simple and intuitive interface for users to initiate and manage voice calls.

## Features

- **One-to-One Voice Calling**: Users can initiate voice calls with another user using their unique user IDs and a shared channel name.
- **Real-Time Communication**: The Agora RTC SDK enables low-latency, high-quality voice communication between users.
- **User Interface**: A simple and user-friendly interface allows users to start and end calls and view call status.
- **Token Generation**: The project includes functionality to fetch Agora tokens from a server, ensuring secure communication.

## Prerequisites

- Android Studio (latest version recommended)
- An Agora developer account (sign up at [Agora](https://www.agora.io/))
- Basic knowledge of Android development and Kotlin

## Getting Started

1. **Clone the repository.**
2. **Open the project in Android Studio**.
3. **Obtain your Agora App ID and Certificate**:
   - Log in to your Agora developer account.
   - Create a new project and obtain the App ID and Certificate.
   - Replace the placeholders `APP_ID` and `CERTIFICATES` in the code with your actual values.
4. **Set up a token server**:
   - The project requires a server to generate Agora tokens for secure communication. You can use the Agora token server provided in the official documentation or implement your own.
   - Update the `BASE_URL` in the `ApiClient` class to point to your token server.
5. **Build and run the application** on an emulator or physical device.

## Usage

1. **Enter the channel name and user ID**:
   - In the `MainActivity`, provide the channel name and user ID for the call.

2. **Start the call**:
   - Tap the "Start Call" button to initiate a voice call.

3. **End the call**:
   - Tap the "End Call" button to terminate the voice call.

## Project Structure

- **MainActivity**: The main activity responsible for handling user interactions and Agora RTC functionality.
- **ApiClient**: A singleton object that provides a Retrofit instance for making API calls to the token server.
- **RtcTokenService**: A service interface for fetching RTC tokens from the server.
- **AuthInterceptor**: An interceptor to add authentication headers to network requests.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, feel free to open an issue or submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](https://opensource.org/licenses/MIT) file for details.
