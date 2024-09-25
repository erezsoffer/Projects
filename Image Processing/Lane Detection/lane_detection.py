import cv2
import numpy as np

# Class to manage line detection and aggregation
class LineDetector:
    def __init__(self):
        self.points = []

    def add_point(self, x, y):
        self.points.append((x, y))

    def get_line(self, height):
        if len(self.points) < 2:
            return None
        [vx, vy, x, y] = cv2.fitLine(np.array(self.points), cv2.DIST_L2, 0, 0.01, 0.01)
        slope = vy / vx
        intercept = y - slope * x
        y1 = height
        x1 = int((y1 - intercept) / slope)
        y2 = int(height * 0.54)  # Extend line to the top of ROI
        x2 = int((y2 - intercept) / slope)
        return x1, y1, x2, y2

def process_frame(frame, prev_left_line, prev_right_line, lane_change_count):
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    if (np.mean(gray) < 100):  # Night
        # Apply Gaussian blur to reduce noise
        blurred = cv2.GaussianBlur(gray, (7, 7), 0)
        # Perform Canny edge detection
        edges = cv2.Canny(blurred, 50, 50)
    else:  # Day
        # Perform Canny edge detection
        edges = cv2.Canny(gray, 250, 450)

    # Region of Interest (ROI) for lane detection
    roi_vertices = np.array([[(0, frame.shape[0]), (frame.shape[1] // 2, frame.shape[0] // 2),
                              (frame.shape[1], frame.shape[0])]], dtype=np.int32)
    roi_edges = cv2.fillPoly(np.zeros_like(edges), roi_vertices, 255)
    roi_edges = cv2.bitwise_and(edges, roi_edges)

    # Perform Hough transform
    lines = cv2.HoughLinesP(roi_edges, 1, np.pi / 180, threshold=50, minLineLength=100, maxLineGap=100)

    left_lane = LineDetector()
    right_lane = LineDetector()

    if lines is not None:
        for line in lines:
            x1, y1, x2, y2 = line[0]
            slope = (y2 - y1) / (x2 - x1 + 1e-6)
            if abs(slope) > 0.5:  # Filter out near-horizontal lines
                if slope < 0 and x1 < frame.shape[1] // 2 and x2 < frame.shape[1] // 2:  # Left lane
                    left_lane.add_point(x1, y1)
                    left_lane.add_point(x2, y2)
                elif slope > 0 and x1 > frame.shape[1] // 2 and x2 > frame.shape[1] // 2:  # Right lane
                    right_lane.add_point(x1, y1)
                    right_lane.add_point(x2, y2)

    # Get lines
    left_line = left_lane.get_line(frame.shape[0])
    right_line = right_lane.get_line(frame.shape[0])

    # Draw lane lines
    if left_line is not None:
        cv2.line(frame, (left_line[0], left_line[1]), (left_line[2], left_line[3]), (0, 0, 255), 3)
    if right_line is not None:
        cv2.line(frame, (right_line[0], right_line[1]), (right_line[2], right_line[3]), (0, 0, 255), 3)

    # Detect lane change and draw markings
    if 180 > lane_change_count > 0:
        cv2.putText(frame, '<--- Lane Change', (800, 300), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)
    elif -180 < lane_change_count < 0:
        cv2.putText(frame, 'Lane Change --->', (800, 300), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2, cv2.LINE_AA)
    elif lane_change_count == 0:
        if prev_left_line is not None and prev_right_line is not None:
            if left_line is not None and right_line is not None:
                if left_line[0] - prev_left_line[0] > 120 or right_line[0] - prev_right_line[0] > 120:
                    lane_change_count = 200  # Show lane change message for the next 200 frames
                elif prev_left_line[0] - left_line[0] > 120 or prev_right_line[0] - right_line[0] > 120:
                    lane_change_count = -200  # Show lane change message for the next 200 frames

    return frame, left_line, right_line, lane_change_count

# Main function for processing the video
def process_video(input_video_path, output_video_path):
    cap = cv2.VideoCapture(input_video_path)
    fps = cap.get(cv2.CAP_PROP_FPS)
    width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
    out = cv2.VideoWriter(output_video_path, cv2.VideoWriter_fourcc(*'mp4v'), fps, (width, height))

    prev_left_line = None
    prev_right_line = None
    lane_change_count = 0
    frame_count = 0

    while cap.isOpened():
        ret, frame = cap.read()
        if not ret:
            break

        processed_frame, left_line, right_line, lane_change_count = process_frame(frame, prev_left_line, prev_right_line, lane_change_count)

        if frame_count % 5 == 0:  # Update prev_left_line and prev_right_line every 5 frames
            prev_left_line = left_line
            prev_right_line = right_line

        cv2.imshow('Lane Detection', processed_frame)
        out.write(processed_frame)

        if lane_change_count > 0:
            lane_change_count -= 1
        elif lane_change_count < 0:
            lane_change_count += 1

        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

        frame_count += 1

    cap.release()
    out.release()
    cv2.destroyAllWindows()

# Call the process_video function with input and output paths
input_video_path = 'path_to_input_video.mp4'
output_video_path = 'path_to_output_video.mp4'
process_video(input_video_path, output_video_path)