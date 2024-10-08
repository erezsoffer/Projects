# Object Detection using Faster R-CNN with MobileNetV3 Backbone

## Project Overview

This project implements object detection using the Faster R-CNN architecture with a MobileNetV3 backbone. It is trained on the Oxford Pets Dataset to detect cats and dogs in images and videos.

## Features

- **Dataset**: Uses the Oxford-IIIT Pets Dataset for training and validation.
- **Model**: Implements Faster R-CNN with a MobileNetV3 backbone for object detection.
- **Training**: Includes custom dataset creation, data augmentation, and model training.
- **Inference**: Performs real-time object detection on videos, drawing bounding boxes and labels.

## Technologies Used

- PyTorch (Deep Learning Framework)
- OpenCV (Computer Vision)
- Albumentations (Data Augmentation)
- Google Colab (Execution Environment)

## Installation

1. Mount Google Drive to access dataset and model storage:
   ```python
   from google.colab import drive
   drive.mount('/content/drive')
   ```

2. Install required dependencies:
   ```bash
   pip install torch torchvision albumentations opencv-python matplotlib pandas
   ```

3. Clone or download the project files.

## Dataset Preparation

- **Custom Dataset**: `OxfordPetsDataset` class is used to load images and annotations from the Oxford Pets Dataset. It converts images to tensors and applies transformations.

## Model Architecture

- **Backbone**: MobileNetV3 large model is used as the backbone.
- **Anchor Generator**: Configures anchor sizes and aspect ratios for the Region Proposal Network (RPN).
- **Head**: Replaces the classifier head to predict bounding box coordinates and class labels.

## Training

- **Loss Calculation**: Uses the Faster R-CNN loss function to optimize model parameters.
- **Optimizer**: Stochastic Gradient Descent (SGD) with momentum and weight decay.
- **Training Loop**: Iterates through epochs, calculates training and validation losses.

## Inference

- **Frame Processing**: `process_frame` function converts frames to tensors, resizes them, and performs predictions using the trained model.
- **Drawing Boxes**: `draw_boxes` function draws bounding boxes and labels on frames based on prediction results.
- **Video Processing**: `process_video` function processes input videos frame by frame, applies object detection, and outputs annotated videos.

## Example Output

When running the `process_video` function with your input video, the output video (`output_video.mp4`) will display frames with detected cats or dogs, annotated with bounding boxes and labels.  
See [Cat Detection](https://www.youtube.com/watch?v=m6ONQ4XXNaI), [Dog Detection](https://www.youtube.com/watch?v=YB1cFDFtem0)

## Notes

- Ensure your input video (`input_video.mp4`) is accessible and correctly named in the `process_video` function call.
- Adjust model hyperparameters, dataset paths, and other configurations as needed for your specific use case.
