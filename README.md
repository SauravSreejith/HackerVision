<img width="3188" height="1202" alt="frame (3)" src="https://github.com/user-attachments/assets/517ad8e9-ad22-457d-9538-a9e62d137cd7" />

# HackerVision 🧑‍💻

## Basic Details
### Team Name: FANT4STIC

### Team Members
- Team Lead: Saurav Sreejith - NSS College of Engineering, Palakkad

### Project Description
HackerVision is an augmented reality lens that peels back the surface of reality to reveal the hidden structural wireframe of the world, inspired by classic cyberpunk and sci-fi movie aesthetics. It transforms your camera feed into a real-time edge detection visualization with phosphor green wireframes on a black void background.

### The Problem (that doesn't exist)
Ever felt like you're living in a poorly rendered simulation? Tired of seeing the world in boring full-color HD when you could be seeing the raw geometric data structures underneath? Frustrated that you can't debug reality like you debug code?

### The Solution (that nobody asked for)
HackerVision solves this by giving you x-ray vision into the "source code" of reality! Using advanced Canny edge detection algorithms, it strips away the unnecessary surface textures and reveals the underlying wireframe skeleton of everything around you. Now you can finally see the world as the Matrix intended - as glowing green lines floating in digital void!

## Technical Details
### Technologies/Components Used
For Software:
- Kotlin (Android native development)
- OpenCV 4.8.0 (Computer vision and image processing)
- Android Camera2 API (Real-time camera access)
- Canny Edge Detection Algorithm (Core image processing)
- Android Studio (Development environment)

For Hardware:
- Android smartphone (API level 24+)
- Device camera (rear-facing preferred)
- Minimum 4GB RAM (for real-time processing)
- ARM64 or x86_64 processor

### Implementation
For Software:
# Installation
```bash
# Clone the repository
git clone [https://github.com/yourusername/hackervision.git

# Open in Android Studio
# File -> Open -> Select hackervision folder

# Sync Gradle dependencies
# Click "Sync Now" when prompted
```

# Run
```bash
# Connect Android device via USB
# Enable Developer Mode and USB Debugging on device
# Select device in Android Studio
# Click Run button (green triangle) or Ctrl+R
# Grant camera permissions when prompted
```

## 😏 You can skip these steps and download the apk from the releases section.

### Project Documentation
For Software:

# Screenshots (Add at least 3)
![e032d53b-51cc-4743-a1d1-11fb6a054e49](https://github.com/user-attachments/assets/522e8765-cfff-49d2-a587-6e420d14aef2)
*HackerVision main interface showing real-time edge detection with phosphor green wireframes on black background*

![e12a37c4-b281-414d-8bd7-59ed74ea1215](https://github.com/user-attachments/assets/3276c278-772c-4e03-9f1a-1861a9139d1a)
*Scan Depth control slider at minimum setting - showing only major architectural edges*

![d252eda6-fa49-4200-86d7-8f976fd03bd0](https://github.com/user-attachments/assets/a25c8e48-9b65-4125-85d1-e61982215453)
*Scan Depth control slider at maximum setting - showing detailed edge detection with all surface textures*

# Diagrams
<img width="3843" height="585" alt="image" src="https://github.com/user-attachments/assets/e38baec7-5176-41c5-97ac-50d1459ea40b" />

*HackerVision processing pipeline: Camera Input → Grayscale Conversion → Gaussian Blur → Canny Edge Detection → Green Wireframe Rendering → Scanline Effect → Display*

## Core Algorithm Flow:

1. **Camera Input**: Real-time RGBA frames from device camera
2. **Preprocessing**: Convert to grayscale and apply Gaussian blur for noise reduction
3. **Edge Detection**: Apply Canny algorithm with dynamic thresholds controlled by user slider
4. **Visualization**: Map detected edges to phosphor green (0, 255, 0) on black canvas
5. **Post-processing**: Add subtle scanline effect for retro monitor aesthetic
6. **Display**: Render final wireframe effect in real-time at camera framerate

### Project Demo
# Video

### A Note on Video Quality

Please be aware that the demo video was captured using a screen recorder, which has resulted in a lower frame rate and reduced visual quality. This is a limitation of the recording process and does not represent the real-time performance of the application, which runs smoothly on a physical device.

https://github.com/user-attachments/assets/3f56ae0f-44ad-46c1-80a5-e5436724530c

*Video demonstration showing HackerVision transforming real-world objects into cyberpunk wireframes, with interactive scan depth control*

## Team Contributions
- Saurav Sreejith: 
  - Complete Android app architecture and development
  - OpenCV integration and Canny edge detection implementation
  - Real-time camera processing pipeline optimization
  - UI/UX design with cyberpunk aesthetic
  - Performance optimization for mobile devices
  - Project documentation and presentation

## Key Features
- **Real-time Processing**: Blazing fast edge detection at camera framerate
- **Interactive Control**: Live adjustment of edge detection sensitivity
- **Cyberpunk Aesthetic**: Authentic phosphor green wireframes with scanlines
- **Optimized Performance**: Efficient OpenCV implementation for mobile devices
- **Intuitive Interface**: Single-slider control for scan depth adjustment

## Technical Achievements
- Successfully implemented real-time Canny edge detection on Android
- Created seamless camera-to-processing pipeline with minimal latency
- Developed intuitive user control mapping slider position to algorithm parameters
- Achieved authentic retro-futuristic visual aesthetic
- Optimized for smooth performance across various Android devices

---
Made with ❤️ at TinkerHub Useless Projects 

![Static Badge](https://img.shields.io/badge/TinkerHub-24?color=%23000000&link=https%3A%2F%2Fwww.tinkerhub.org%2F)
![Static Badge](https://img.shields.io/badge/UselessProjects--25-25?link=https%3A%2F%2Fwww.tinkerhub.org%2Fevents%2FQ2Q1TQKX6Q%2FUseless%2520Projects)
