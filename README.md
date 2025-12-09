# Weight Tracker App

## Overview

This Android application is a personal weight tracking tool designed to help users monitor their weight, set goals, and visualize their progress. It offers a clean, user-friendly interface built with modern Android design principles, coupled with robust backend functionality for data persistence and personalized user features.

## DEMO



https://github.com/user-attachments/assets/30acdcbd-3165-450f-888d-5c5be4976828



## Features

*   **User Authentication:**
    *   Secure login and registration system utilizing a local SQLite database.
    *   User credentials are persistently stored.
*   **Weight Tracking Dashboard:**
    *   **Current Weight Display:** A prominent, circular display of the user's latest recorded weight.
    *   **Goal Weight & Progress:** Dynamically displays the user's set goal weight and visualizes progress towards it with an interactive progress bar.
    *   **Weight History Graph:** An interactive graph showcasing the user's weight trend over time, with adaptive axis scaling.
*   **Data Management:**
    *   **Add/Edit Weight:** Easily add new weight entries or modify existing records with an intuitive input screen.
    *   **Weight History List:** A dedicated screen provides a detailed list of all recorded weight entries, offering direct options to edit or delete individual records.
    *   **Persistent Data:** All user and weight data is stored in a local SQLite database, ensuring information is retained across app sessions.
*   **Customizable Settings:**
    *   **Theme Selection:** Users can switch between Light and Dark modes, with preferences saved for consistency.
    *   **Goal Weight Setting:** Allows users to define and update their personal weight goals.
    *   **Notification Management:** Provides access to SMS permission controls for automated alerts.
*   **Automated SMS Notifications:**
    *   **Permission Handling:** Proactively requests necessary `SEND_SMS` permission at runtime.
    *   **Goal Achievement Alerts:** Sends a congratulatory SMS when the user reaches their set goal weight (requires granted SMS permission and is sent once per achievement).
*   **Modern User Interface:**
    *   Adheres to Material Design standards, providing a polished and intuitive user experience.
    *   Features a custom app logo for consistent branding across the launcher and login screen.

## Technologies Used

*   **Language:** Java
*   **Build System:** Gradle
*   **UI Toolkit:** Android Jetpack (Material Design Components, `AppCompat`)
*   **Database:** SQLite (managed via `SQLiteOpenHelper`)
*   **Graphing Library:** `com.jjoe64:graphview`
*   **Theme Management:** `AppCompatDelegate`, `SharedPreferences`
*   **Permissions:** AndroidX `ContextCompat`, `ActivityCompat`
*   **SMS:** `SmsManager`

## Setup and Installation

To set up and run this project locally, please follow these steps:

1.  **Clone the Repository:**
    ```bash
    git clone <your-repository-url>
    cd WeightTracker
    ```
2.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select `File > Open...` and navigate to the cloned `WeightTracker` project directory.
3.  **Sync and Build:**
    *   Allow Android Studio to sync the project with Gradle. Resolve any dependency issues if prompted.
    *   Perform a clean and rebuild: `Build > Clean Project`, then `Build > Rebuild Project`.
4.  **Run Application:**
    *   Select an Android Emulator or connect a physical Android device.
    *   Click the `Run` button (green triangle) in the toolbar to deploy the application.

## Contributing

We welcome contributions! If you wish to contribute, please fork the repository, make your changes, and submit a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
