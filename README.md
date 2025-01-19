# EmployeeTracker Android Application

This project consists of an Android application built using Java/Kotlin (with React Native or Expo if applicable) and a Node.js Express server. The Android application allows employees to track their work hours, and the server manages user authentication, data storage, and API services.

## Prerequisites

- Node.js (LTS version recommended)
- MongoDB (use MongoDB Atlas or a local instance)
- Android Studio (for Android app development)
- npm (Node Package Manager)

## Setup Instructions

### 1. Clone the Repository
Clone this repository to your local machine:

```bash
git clone https://github.com/your-username/EmployeeTracker.git
cd EmployeeTracker
```
### 2. Server Setup
Install the server dependencies using npm in root directory:
```bash
npm install
```
Create a .env file in the server directory and add the following variables:
```bash
PORT=5000
MONGO_URI=your-mongodb-connection-string
```
### 3. Start the Server
Run the server with the following command:
```bash
npm start
```
### 4. Android Application Setup
Open EmployeeTracker Android project directory in android studio. Run the project on the paired device or emulator.

### Note
Ensure that the server is running before interacting with the Android app. This app communicates with the Node.js server to fetch data from the MongoDB database.




