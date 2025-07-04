# Offering-hands

Offering-hands is an Android application designed to facilitate community support and charitable activities. The app enables users to create, view, join, and communicate about various social events, such as volunteering opportunities, donation drives, and support initiatives for causes like orphanages and old age homes.

## Key Features

- **Broadcast Creation**: Users can create new broadcasts/events with details like name, subject, description, location, and an image. Events are stored in Firebase and can be joined by other users.
- **Event Discovery & Participation**: Users can browse broadcasts, join events, and see event details including creator info, address, and image.
- **Real-time Chat**: Each broadcast has an associated chat, allowing participants to communicate in real time.
- **Community Navigation**: Quick access to information and navigation for orphanages and old age homes, including map integration.
- **Web Integration**: Embedded web view for related donation or informational websites.
- **User Authentication**: Ensures only logged-in users can participate in the platform.

## Technical Overview

- **Android (Java)**: The primary platform and programming language.
- **Firebase**: Used for authentication, real-time database, and file storage (images, event data, chat messages).
- **Image Upload**: Event images are uploaded to Firebase Storage and referenced in event data.
- **RecyclerView**: Used for displaying lists of broadcasts and chat messages.
- **Navigation**: Integrates with Google Maps for location-based features.

## Example Use Cases

- Organize a food drive and allow community members to join and chat.
- Share volunteering opportunities at local care homes.
- Quickly connect donors, volunteers, and organizers for social initiatives.

## Getting Started

This project is for Android devices. Clone and open in Android Studio, configure Firebase, and run on your device.

---

> **Note:**  
> The project’s README and code indicate that it is a complete mobile solution for creating and sharing help-oriented community events, with chat and media support.
