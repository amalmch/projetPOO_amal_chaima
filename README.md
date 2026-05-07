# MNIST Digit Recognition: Trois vs Cinq

![Application Screenshot](docs/screenshot.png)

A modern, machine learning-powered Java application that allows users to draw digits on a canvas and automatically classifies them as either **3 (Trois)** or **5 (Cinq)** using advanced AI algorithms. 

## ✨ Features

- **Interactive Drawing Canvas:** A responsive, anti-aliased drawing area with "soft brush" ink effects optimized for handwriting recognition.
- **Real-Time Classification:** Instantly predicts whether the drawn digit is a 3 or a 5.
- **Premium User Interface:** A completely overhauled, modern dark-themed GUI featuring gradient accents, glassmorphism elements, and smooth micro-animations.
- **Multiple ML Models:** Implements both Naive Bayes and Random Forest classifiers using the powerful Weka library.
- **Probability Feedback:** Displays visual probability bars indicating the model's confidence level for each digit.

## 🛠️ Technology Stack

- **Language:** Java 17
- **UI Framework:** Java Swing (Custom UI Components)
- **Machine Learning:** Weka (Waikato Environment for Knowledge Analysis)
- **Build Tool:** Maven
- **Data Export:** Apache POI (Excel)

## 🚀 Quick Start

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Maven 3.6+

### Installation & Execution

1. **Clone the repository:**
   ```bash
   git clone <your-repository-url>
   cd mnist-recognition
   ```

2. **Generate the Training Data:**
   Before running the UI, the application needs to extract the specific 3s and 5s from the MNIST dataset and build the training/testing ARFF files.
   ```bash
   mvn compile
   mvn exec:java -Dexec.mainClass="ModelComparator"
   ```

3. **Launch the Application:**
   ```bash
   mvn exec:java -Dexec.mainClass="MNIST_Creative_GUI"
   ```

## 📂 Project Structure

```
Projet POO/
├── data/                       # Contains the generated CSV and ARFF datasets
├── dataset/                    # The raw MNIST binary files
├── src/main/java/              # Source code
│   ├── MNIST_Creative_GUI.java # Main application interface
│   ├── DrawingArea.java        # The interactive canvas component
│   ├── ModernUIComponents.java # Custom styled UI components (Buttons, Bars)
│   ├── DigitClassifier.java    # Interface for ML models
│   ├── RandomForestClassifier.java
│   ├── NaiveBayesClassifier.java
│   ├── ModelComparator.java    # Data pipeline and evaluation script
│   ├── TextFileHandler.java    # CSV and ARFF file generation
│   ├── ExcelExporter.java      # Excel export utilities
│   └── BinaryMNISTReader.java  # Parser for raw MNIST binary files
└── pom.xml                     # Maven configuration
```

## 📸 Screenshots

*(Please replace `docs/screenshot.png` with an actual screenshot of the application).*

## 📖 Comprehensive Report

For a deep dive into the project's architecture, machine learning concepts, implementation details, and evaluation metrics, please refer to the detailed [Project Report](docs/REPORT.md).
