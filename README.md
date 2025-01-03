![image](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![image](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)

# TheColor
Cutting-edge native Android app about each and every known color.

<img src="https://github.com/user-attachments/assets/a8caba45-7b72-484b-a68c-eed6ce9dab11" width="200" alt="Screenshot 01-01-2025" />
<img src="https://github.com/user-attachments/assets/f6166eb8-eda5-445e-9077-06fcda13ffcc" width="200" alt="Screenshot 01-01-2025" />
<img src="https://github.com/user-attachments/assets/1f84bfc5-7e62-4f69-8a9e-75629b311157" width="200" alt="Screenshot 01-01-2025" />
<img src="https://github.com/user-attachments/assets/c625e8ca-a294-4e24-9c75-c5cb6b73231c" width="200" alt="Screenshot 01-01-2025" />
<img src="https://github.com/user-attachments/assets/feff3c7c-172e-4410-a55c-1caf9284ba81" width="200" alt="Screenshot 01-01-2025" />

## Table of Contents

* [What is it](#what-is-it)
* [Who is it for](#who-is-it-for)
* [App features](#app-features)
* [Software approaches](#software-approaches)

-----

## What is it

This is a native Android application about colors.
You can search for a specific color or generate a random one.
Explore detailed color information and create custom color schemes with the color scheme builder.

The app is powered by [The Color API](https://github.com/joshbeckman/thecolorapi).
For a full list of features, check out the [App Features](#app-features) section.

## Who is it for

This project showcases my expertise in developing software in general and
native Android apps in specific.
Originally, it served as a playground, a safe environment to try new things in Android world.
Over the course of years, it evolved into a polished, comprehensive app that I'm proud to share.

I would be flattered if other developers find inspiration in this software, its approaches and 
solutions.
It has open-source code and full commit history, so anyone can trace my decisions and the app's development journey.

## App Features

### Home
The main screen of the app. Displayed after the Splash screen.
Contains *Color Input*, *Color Preview* and *Color Center* views.  They are always in sync:

 - entering a valid color in *Color Input* will show *Color Preview* for it.
 - changing or clearing *Color Input* will change / hide *Color Preview*.
 - generating a new random color will populate it into *Color input*.
 - selecting "go to exact color" or "go to initial color" in *Color Details* will populate *Color Input* with the corresponding color.

Allows to go to the *Settings* screen.
Allows to proceed with the current color. This will fetch data regarding the color and display *Color Center*.
Allows to generate a random color.

Handles keyboard "done" button click of *Color Input*. When there's a valid color, the app will proceed with it and a software keyboard (if any) will be hidden. If the color is not valid, then a message will be shown and a software keyboard will remain visible, allowing user to correct the input.

*Color Center* is fully recreated when a color is proceeded with. This means that any user input that happened for previous color will be reset to default values (e.g. color scheme mode in *Color Scheme*).

If *"resume from last searched color"* feature from *Settings* is enabled, then the last searched (proceeded with) color will be proceeded with on app startup.

If *"auto proceed with randomized colors"* feature from *Settings* is enabled, then generating a new random color will instantly proceed with it.

### Color Input
Allows user to enter a color in a chosen color space.
Consists of a view for user input and a list of color spaces to choose from.

All color input types are synchronized. If user enters a valid color through color input of color space X, then when switching to color space Y they will see the same color. If user clears input in any color input type, all color inputs are cleared.

User can proceed with entered color by clicking "done" button on a keyboard. The outcome depends on the state of currently entered color (valid or not) and the place where a particular *Color Input* is located. In other words, the event-handling is client-specific.

User can change a preferred type of color input on the *Settings* screen. The chosen type will become the first one in the list of available color input types. The chosen type will be selected on app launch.

Supports *"smart backspace"* and *"select all text on focus"* features from *Settings*.

### Color Preview
A small view that visually displays a color.

### Color Center
A group for two other feature: *Color Details* and *Color Scheme*.
Displays when user proceeds with a color. Hides when color is changed or cleared.

### Color Details
Presents information about a color: 
 - color name
 - translations to different color spaces
 - whether the color is an exact match or not
 - color of the exact match
 - deviation from the exact match

Allows to go to the exact match and view its info.
Allows to go back to the initial color after that.

### Color Scheme
Consists of two parts: color scheme with swatches and color scheme builder. List of the swatches has the current color (one that was proceeded with) as a seed. Other parameters of color scheme are configured from the builder.
Allows to choose the mode of the color scheme: monochrome, analogic, etc.
Allows to choose number of swatches to have in the color scheme.
Allows to view *Color Details* for each swatch.

### Settings
Contains list of user preferences. Changing values will change the behaviour of the features those items relate to.
Allows user to reset their preferences back to default values.
Items (features) are listed below.

#### Preferred type of color input
Allows to select a type of color input to be selected by default, on app startup in *Color Input*. This type will also be the first in the list of available input types.

#### UI theme
A color scheme of the application. The options are:

 - Light, always, irregarding of device's Dark mode setting.
 - Dark, always, irregarding of device's Dark mode setting.
 - Auto. Resolves into either Light or Dark depending on the device's Dark mode setting.

#### Resume from last searched color
On app startup, the *Home* screen will remember the last color that was proceeded with in the previous app session. This color will be automatically proceeded with.

#### Smart backspace
Go to previous text field when clicking "backspace" in an empty text field. 
Enables easier editing in text fields of *Color Input*. On Android, there's a "next" button on software keyboard. It allows to go to the next text field in order to not waste time reaching to it (next text field) manually. This feature allows to go to the previous text field by clicking "backspace" when the text field is already empty.

#### Select all text on focus
Select all text in a text field when it gets focused (e.g. when user clicks it). 
This feature makes it quicker to change the entire value of the text field. With the text pre-selected, there's no need to manually clear it—simply start typing, and the new input will instantly replace the existing text.

#### Auto proceed with randomized colors
Generating a new random color on *Home* screen will automatically proceed with it.

## Development stack

- Kotlin 1.9.x
- Gradle Kotlin DSL
- Kotlin Flows and coroutines
- DI: Dagger & Hilt
- Local data: Room, Preferences DataStore
- Remote data: Retrofit 2, Moshi
- UI: Jetpack Compose, Compose Navigation, single Activity approach, Material 3
- Unit testing: JUnit 4, MockK, Kotlin Assertions

## Software approaches

- Clean architecture
- Gradle modularization
