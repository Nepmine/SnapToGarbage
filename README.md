# SnapToGarbage

**Android application** for reporting environmental waste by snapping photos with geolocation. Built with Kotlin and Firebase to demonstrate mobile dev and cloud integration.

## 🚀 Key Features
- **Photo Capture:** Use device camera to take a picture of garbage/waste.
- **Geolocation:** Retrieves current GPS coordinates when the photo is taken.
- **Cloud Storage:** Uploads the image to Firebase Storage and saves a report entry to Firestore with the image URL and location.
- **User Workflow:** Simple UI with a capture button and status messages.

## 🛠️ Tech Stack
- **Language:** Kotlin
- **Platform:** Android (minimum API level specified in Gradle)
- **Services:** Google Play Location Services (FusedLocationProviderClient) for GPS; Firebase Authentication optional; Firebase Firestore (data) & Storage (images).

## 📱 Usage
1. **Clone the project:** `git clone https://github.com/Nepmine/SnapToGarbage.git`
2. **Firebase Setup:** Create a Firebase project and add an Android app.
3. **Config file:** Copy the generated `google-services.json` into the `app/` directory.
4. **Permissions:** Grant CAMERA and LOCATION permissions in `AndroidManifest.xml` (already declared).
5. **Run:** Open the project in Android Studio and run on an emulator or real device.

Upon launch, the app will request camera/location access. When you tap *Capture*, it takes a photo, gets the device’s last known location, and then uploads the data to Firebase. Check your Firestore console to see the report entries.

## 🔒 Security Note
- This demo uses Firebase rules that allow authenticated writes. For production, secure your Firestore and Storage with proper rules.
