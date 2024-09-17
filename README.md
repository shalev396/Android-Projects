
# Fleemarket Android Project

## Overview

Fleemarket is an Android application designed for users to buy and sell goods in a local marketplace setting. The app provides a platform where users can post items for sale and browse through various listings in their area.

## Features

- **User Authentication**: The app uses Firebase Authentication for user login and registration.
- **Buy/Sell Interface**: Users can post their items for sale with images and descriptions.
- **Local Marketplace**: The app displays listings from nearby sellers, making it easy to find items close to you.
- **Notification System**: Get notifications when someone is interested in your listing or when there are updates on your transactions.

## Technologies

- **Firebase**: Used for authentication and database services.
- **Google Play Services**: Integrated for location services to display local listings.
- **Gradle**: Project build automation and dependency management.
- **Android SDK 30**: Targeted and compiled with SDK version 30, supporting Android devices with SDK 19 and above.

## Requirements to Run the APK

To install and run the Fleemarket APK on an Android device, the following requirements must be met:

- **Android OS Version**: Android 4.4 (KitKat) or higher (API Level 19+).
- **RAM**: Minimum of 2GB RAM for smooth performance.
- **Storage**: At least 50MB of free space to install the APK and additional storage for storing images and cache data.
- **Internet Connection**: Required for interacting with Firebase services (authentication and data sync).
- **Google Play Services**: Must be installed and updated for location-based services.

## Getting Started

### Prerequisites

Ensure you have the following setup in your development environment:

- Android Studio
- JDK 8 or above
- Android SDK 30
- Firebase account for authentication and database configuration

### Setup

1. Clone this repository:

   ```bash
   git clone https://github.com/shalev396/Android-project-Fleemarket.git
   ```

2. Open the project in Android Studio.

3. Configure Firebase:
   - Connect the project to Firebase via the Firebase console.
   - Add your `google-services.json` file in the `/app` directory.

4. Build the project in Android Studio by syncing Gradle files.

### Running the App

1. Ensure your Android emulator or connected device is running.
2. Click the **Run** button in Android Studio or use the following command:

   ```bash
   ./gradlew assembleDebug
   ```

### Testing

Unit tests are included under the `app/src/test/java` directory. You can run these tests using:

```bash
./gradlew test
```

## Documentation

For more detailed documentation about the project, refer to the following link:

[Project Documentation](https://drive.google.com/file/d/1IVy8UH4-iJH0NDL1GWuAWypQPNOgVeLm/view?usp=drive_link)

## Bibliography

- **Creator**: [Shalev Ben Moshe](https://github.com/shalev396)
- **Android Developer Documentation**: [Android Developer](https://developer.android.com/develop)
- **Stack Overflow**: [Stack Overflow](https://stackoverflow.com/)
- **Teacher**: Udi Edri

### Additional Suggested Resources:
- **Firebase Documentation**: [Firebase Docs](https://firebase.google.com/docs)
- **Material Design Guidelines**: [Material Design](https://m2.material.io/design/guidelines-overview)
- **Git Documentation**: [Git Docs](https://git-scm.com/doc)

### License

- This project is licensed under the Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International License. To view a copy of this license, visit CC BY-NC-ND 4.0.
- read "LICENSE" form more details
