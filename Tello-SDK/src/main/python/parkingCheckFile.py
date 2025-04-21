import cv2
import pickle
import numpy
import os

spotsList = []

# Use script-relative path to find parkingSpots
base_path = os.path.dirname(os.path.abspath(__file__))
spots_path = os.path.join(base_path, 'parkingSpots')

with open(spots_path, 'rb') as f:
    spotsList = pickle.load(f)

width, height = 60, 100

def checkSpace(processImage):
    available_count = 0
    total_spots = len(spotsList)

    for pos in spotsList:
        x, y = pos

        # Safety check
        if y + height > processImage.shape[0] or x + width > processImage.shape[1]:
            continue

        imgCrop = processImage[y:y+height, x:x+width]
        cv2.imshow(str(x*y), imgCrop)

        count = cv2.countNonZero(imgCrop)
        if count < 525:
            color = (0, 255, 0)
            available_count += 1
        else:
            color = (255, 0, 0)

        cv2.rectangle(img, pos, (pos[0] + width, pos[1] + height), color, 2)

    # Show count window
    info_img = numpy.zeros((100, 500, 3), dtype=numpy.uint8)
    status_text = f"Available: {available_count} / {total_spots}"
    cv2.putText(info_img, status_text, (20, 60), cv2.FONT_HERSHEY_SIMPLEX, 1.5, (255, 255, 255), 3)
    cv2.imshow("Spot Count", info_img)

# Load from video file (make sure path is valid or modify accordingly)
video_path = os.path.join(base_path, 'parking1.mp4')
capture = cv2.VideoCapture(video_path)

while True:
    if capture.get(cv2.CAP_PROP_POS_FRAMES) == capture.get(cv2.CAP_PROP_FRAME_COUNT):
        capture.set(cv2.CAP_PROP_POS_FRAMES, 0)

    success, img = capture.read()

    if not success or img is None:
        continue

    imgGray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    imgBlur = cv2.GaussianBlur(imgGray, (3, 3), 1)

    imgThreshold = cv2.adaptiveThreshold(imgBlur, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
                                         cv2.THRESH_BINARY_INV, 25, 16)

    imgMedian = cv2.medianBlur(imgThreshold, 5)
    kern = numpy.ones((3, 3), numpy.uint8)
    imgDilate = cv2.dilate(imgMedian, kern, iterations=1)

    checkSpace(imgDilate)

    cv2.imshow("Image", img)
    cv2.imshow("ImageBlur", imgDilate)
    cv2.waitKey(1)