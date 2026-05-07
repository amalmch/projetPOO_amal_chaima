# Project Report: MNIST Binary Classification (3 vs 5)

## 1. Introduction

The **MNIST Digit Recognition** project is a comprehensive Java-based application designed to perform a binary classification task on handwritten digits. Specifically, the system is engineered to distinguish between the digit "3" (Trois) and the digit "5" (Cinq). The application combines an object-oriented architecture for robust data handling, integration with the Weka machine learning library for predictive modeling, and a modernized, interactive Swing Graphical User Interface (GUI) for real-time user testing.

This project was developed in multiple phases, transitioning from raw binary data parsing to sophisticated machine learning evaluation, and ultimately culminating in a seamless user experience.

---

## 2. Architecture & Data Pipeline

The backbone of the application relies on an efficient data pipeline capable of parsing, filtering, and converting the raw MNIST database.

### 2.1 Reading Raw MNIST Data
The MNIST dataset is distributed in a specific binary format. The `BinaryMNISTReader` class acts as the foundation, parsing `train-images-idx3-ubyte` (pixel data) and `train-labels-idx1-ubyte` (labels). It extracts the images into arrays of 784 integers (representing a 28x28 pixel grid) and aligns them with their corresponding integer labels.

### 2.2 Data Transformation and Filtering
The project employs a modular approach defined by the `DataProcessor` interface. The `TextFileHandler` is responsible for generating human-readable CSV files and converting them into ARFF (Attribute-Relation File Format), the format required by the Weka library.

For this specific binary classification task, the `ModelComparator` class filters the massive dataset down to a focused subset:
- **Training Set:** 800 images (400 of digit 3, 400 of digit 5).
- **Testing Set:** 100 images (50 of digit 3, 50 of digit 5).
This filtering ensures the models train specifically on the features that differentiate these two highly similar digits.

---

## 3. Machine Learning Models

The predictive core of the application leverages the **Weka** library to train and evaluate machine learning models. We implemented the `DigitClassifier` interface to allow seamless polymorphism between different algorithms.

### 3.1 Naive Bayes Classifier
The `NaiveBayesClassifier` applies Bayes' theorem with the "naive" assumption of conditional independence between every pair of features (pixels). While computationally inexpensive and fast to train, its performance on image data can sometimes be hindered because adjacent pixels are highly correlated.

### 3.2 Random Forest Classifier
The `RandomForestClassifier` builds an ensemble of decision trees during training and outputs the mode of the classes. By using a multitude of trees (e.g., 50 iterations), it corrects for decision trees' habit of overfitting to their training set. 

**Evaluation:**
During the evaluation phase (`ModelComparator`), Random Forest consistently outperforms Naive Bayes on this dataset (typically achieving ~97% accuracy compared to Naive Bayes' ~87%), making it the primary model used in the final GUI application.

---

## 4. Graphical User Interface (GUI)

The application features a fully custom, premium UI built over Java Swing, designed to provide a "wow" factor while remaining highly functional.

### 4.1 Custom Components (`ModernUIComponents`)
To bypass the dated appearance of default Swing components, a bespoke design system was created:
- **`RoundedPanel`:** Provides modern card layouts with glassmorphism highlights and outer shadow "glow" effects.
- **`AnimatedButton`:** Replaces standard buttons with vibrant gradient backgrounds, smooth hover transitions, and press feedback using `AlphaComposite` rendering.
- **`ProbabilityBar`:** A dynamic, smoothly animating horizontal bar chart. It features color gradients and automatically applies a "winner" highlight (a subtle glowing aura) to the digit with the highest predicted probability.

### 4.2 Application Flow (`MNIST_Creative_GUI`)
1. **Model Loading:** Upon launch, a background thread asynchronously loads and trains the Random Forest model to prevent UI freezing. A small animated `StatusIndicator` conveys the system's readiness.
2. **User Interaction:** The user draws on a spacious 420x420 `DrawingArea`. The canvas uses a customized `RadialGradientPaint` brush to simulate soft ink, which closer resembles the stroke weight of the original MNIST dataset.
3. **Prediction Pipeline:** 
   - The drawing is cropped to its bounding box, centered, and scaled down to the exact 28x28 resolution expected by the model.
   - The pixel vector is passed to the `RandomForestClassifier`.
   - The UI immediately updates, displaying the dominant prediction in large text alongside the exact confidence probabilities for both "3" and "5".

---

## 5. Conclusion

This project successfully bridges fundamental data parsing, machine learning classification, and modern UI/UX design in Java. By focusing the classification task specifically on distinguishing between a 3 and a 5, the model achieves high accuracy and delivers an interactive, responsive experience that clearly demonstrates the power of integrating Weka with a custom Swing front-end.
