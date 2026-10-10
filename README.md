# Smart Pantry Manager

A Java Android application that helps reduce food waste by tracking the ingredients you already have at home and suggesting recipes you can cook using **strictly** those ingredients.

## Features
- Add, edit and delete pantry items (name, quantity, unit, optional expiry date)
- Pantry list screen
- Pre-loaded recipe collection
- Suggested Recipes screen: a recipe is only shown if the pantry contains **every** ingredient it needs, in the required quantity
- Recipe detail screen with ingredients and method
- Settings screen

## Database
**SQLite** (local, on-device using SQLiteOpenHelper).
I chose SQLite because it works offline, needs no server or internet connection, and is covered in the module's persistent data chapter. Data persists after the app is closed and reopened.

## Setup / Run Instructions
1. Install Android Studio.
2. Clone this repository:
   `git clone https://github.com/Khayelihl/SmartPantryManager.git`
3. Open the project folder in Android Studio and wait for Gradle sync to finish.
4. Run the app on an emulator or a physical Android device (minimum SDK: API 24).

## Author
Khayelihle - Mobile App Development 700