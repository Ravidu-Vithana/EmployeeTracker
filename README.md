# EmployeeTracker Android Application

This project consists of a Simple Android application built using Java and a Node.js Express server. The Android application allows employees to track their work hours, and the server manages user authentication, data storage, and API services.

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
After that, check for the IPV4 address of your local machine that runs the server and place it in the ``` server_ip_port ``` constant in MainActivity.java. The default port is set to 5000.

### 4. Android Application Setup
Open EmployeeTracker Android project directory in android studio. Run the project on the paired device or emulator.

### 5. Google and Firebase Setup
Create a project in [Google Cloud Console](https://console.cloud.google.com) and configure the [OAuth Consent Screen](https://console.cloud.google.com/apis/credentials/consent). Then go to create a new [Firebase project](https://console.firebase.google.com/) and select the Google Console project. Then add an android app to the firebase console and follow to steps to obtain the google-services.json file. Add that file to the src folder ``` (EmployeeTracker\app\src\google-services.json) ``` from the Project view of android studio. Copy the Web Application type client id now available in [Google Console Credentials page](https://console.cloud.google.com/apis/credentials) and add it to the ``` web_server_client_id ``` String in the ``` strings.xml ``` resource file

### Note
Ensure that the server is running before interacting with the Android app. This app communicates with the Node.js server to fetch data from the MongoDB database.




