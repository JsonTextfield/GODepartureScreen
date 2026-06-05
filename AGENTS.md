# Project: Departure_Screen

This project is an Android and Wear OS application for displaying transit departure information, specifically tailored for GO Transit services based on the provided API Data Catalogue.

## Project Structure

- `:composeApp`: Main Android application module using Jetpack Compose.
- `:wearApp`: Wear OS application module.
- `:core`: Shared logic and domain models (Kotlin Multiplatform).
- `iosApp`: Swift-based iOS application.

## Key Technologies

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Multiplatform)
- **Architecture:** Clean Architecture with Use Cases.

## Domain Context

The application interacts with the Open API Data Catalogue for transit information including:
- Stop Services (Predictions, details, facilities)
- Service Updates (Alerts)
- Schedules (Journey planning)
- Real-time GTFS feeds (Vehicle positions and trip updates)

## Agent Instructions

- Maintain consistency with the existing architecture in the `:core` and `:composeApp` modules.
- Ensure that UI components in `composeApp` are reusable where possible.
- Refer to `API_Data_Catalogue.md` for data structures and attribute meanings when working on data parsing or domain models.
