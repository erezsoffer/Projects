# Lane Detection Project

## Project Overview

This project implements a lane detection system using computer vision techniques, specifically designed to process video frames and identify lane lines. It can detect lane changes and mark them on the real-time video feed.

## Features
- **Lane Detection**: Uses edge detection (Canny) and Hough Line Transform to detect lanes.
- **Lane Change Detection**: Identifies and flags lane changes by comparing detected lanes with previous frames.
- **Day and Night Mode**: Handles different conditions using adaptive thresholds for day and night vision.
- **Video Processing**: Processes input video frames, detects lanes, and saves an annotated output video.

## Technologies Used
- OpenCV (Computer Vision)
- NumPy (Matrix Operations)
- Python

## Installation

1. Install dependencies:
   ```bash
   pip install opencv-python numpy
   ```

2. Clone or download the project files.

3. Update `input_video_path` and `output_video_path` variables with your own video file paths.

## How to Run

1. Ensure you have the necessary dependencies installed.
2. Run the Python script:
   ```bash
   python lane_detection.py
   ```

3. The program will process the input video, detect lanes, and save the processed video with detected lanes and lane changes highlighted.

## Project Structure

- `lane_detection.py`: Contains the main logic for processing video frames and detecting lanes.
- `input_video.mp4`: Path to your input video file.
- `output_video.mp4`: Path where the output video with annotations will be saved.

## Core Functions

- **`LineDetector` class**: Manages the points for detected lines and computes the lane line equations.
- **`process_frame` function**: Processes a single frame of the video, detecting the lanes and any potential lane changes.
- **`process_video` function**: Processes the entire video, calling the frame processor and saving the output with annotations.

## Example Output

When running the program, you will see the lane lines drawn on the video in red, with lane change notifications (either "Lane Change --->" or "<--- Lane Change") if a lane change is detected.
See [Lane Changing](https://www.youtube.com/watch?v=9vDveLTsRgo), [Night Output](https://www.youtube.com/watch?v=V6N4vKbF1Lw)

## Additional Notes

- The lane change notification persists for about 200 frames after detection.
- The video feed can be viewed in real-time during processing, and pressing 'q' will quit the feed.
