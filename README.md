# 📱 Flashlight Toggle App

A simple Android application built using Kotlin that allows users to turn the device flashlight ON/OFF with a single toggle button.

# ✨ Features

🔦 Toggle flashlight ON/OFF

⚡ Works on most Android devices with a camera flash

🎨 Simple & minimal UI

🚀 Lightweight and fast

# 🛠️ Tech Stack

Language: Kotlin

Framework: Android SDK

UI: XML Layout

# Permissions:

android.permission.CAMERA

android.permission.FLASHLIGHT (implicitly handled in newer Android versions)

Make sure you allow Camera Permission when prompted, otherwise flashlight won’t work.


# 📂 Project Structure

FlashlightToggleApp/

│── app/

│   ├── src/

│   │   ├── main/

│   │   │   ├── java/com/example/flashlight/

│   │   │   │   ├── MainActivity.kt   # Handles flashlight toggle logic

│   │   │   ├── res/

│   │   │   │   ├── layout/activity_main.xml  # UI with toggle button

│   │   │   │   ├── values/strings.xml

│   │   │   │   ├── values/colors.xml

│   │   │   │   ├── values/themes.xml

│   │   │   └── AndroidManifest.xml   # Permissions declared here

│── build.gradle

│── README.md

# ▶️ How It Works

App checks if the device has a flashlight.

On toggle button click → turns flashlight ON/OFF using CameraManager.

Updates UI based on flashlight state.

# 📷 Screenshots 

### ON

<img src="Screenshots/flashlight_output1.jpg" alt="ON" width="250"/>

### OFF

<img src="Screenshots/flashlight_output2.jpg" alt="OFF" width="250"/>


# 📦 Installation

Clone the repository:

git clone https://github.com/your-username/FlashlightToggleApp.git


Open project in Android Studio

Connect a physical device (flashlight won’t work on emulator)

Run the app 🚀


# 🚀 Future Improvements

Add a widget to toggle flashlight directly from home screen

Add shake gesture to turn on flashlight

Add SOS blinking mode
