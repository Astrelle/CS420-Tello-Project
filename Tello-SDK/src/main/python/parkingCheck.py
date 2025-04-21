import cv2
import pickle
import numpy
import os  # for getting script-relative path
import socket  # for socket connection
import struct  # for receiving frame length

spotsList = []

# Use script's folder to construct path to 'parkingSpots'
base_path = os.path.dirname(os.path.abspath(__file__))
spots_path = os.path.join(base_path, 'parkingSpots')

with open(spots_path, 'rb') as f:
    spotsList = pickle.load(f)

width, height = 60, 100

def checkSpace(processImage):
    available_count = 0
    total_spots = len(spotsList)
    
    for pos in spotsList:
        x,y = pos

        # Prevent crash if crop area exceeds frame bounds
        if y + height > processImage.shape[0] or x + width > processImage.shape[1]:
            continue

        imgCrop = processImage[y:y+height,x:x+width]
        cv2.imshow(str(x*y),imgCrop)

        count = cv2.countNonZero(imgCrop)
        if count < 525:
            color = (0, 255, 0)
        else:
            color = (255, 0, 0)

        cv2.rectangle(img, pos, (pos[0] + width, pos[1] + height), color, 2)

    # Show count window
    info_img = numpy.zeros((100, 500, 3), dtype=numpy.uint8)
    status_text = f"Available: {available_count} / {total_spots}"
    cv2.putText(info_img, status_text, (20, 60), cv2.FONT_HERSHEY_SIMPLEX, 1.5, (255, 255, 255), 3)
    cv2.imshow("Spot Count", info_img)

# Setup socket connection to receive frames from Java
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.connect(('localhost', 9997))  # match Java socket port
data_buffer = b''

while True:
    # receive 4-byte length header
    while len(data_buffer) < 4:
        packet = server_socket.recv(4 - len(data_buffer))
        if not packet:
            break
        data_buffer += packet

    if len(data_buffer) < 4:
        continue

    frame_len = struct.unpack('>I', data_buffer[:4])[0]
    data_buffer = data_buffer[4:]

    while len(data_buffer) < frame_len:
        packet = server_socket.recv(frame_len - len(data_buffer))
        if not packet:
            break
        data_buffer += packet

    frame_data = data_buffer[:frame_len]
    data_buffer = data_buffer[frame_len:]

    img_array = numpy.frombuffer(frame_data, dtype=numpy.uint8)
    img = cv2.imdecode(img_array, cv2.IMREAD_COLOR)

    # Skip if decoding fails
    if img is None:
        continue

    imgGray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    imgBlur = cv2.GaussianBlur(imgGray, (3, 3), 1)

    imgThreshold = cv2.adaptiveThreshold(imgBlur,255,cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY_INV,25,16)

    imgMedian = cv2.medianBlur(imgThreshold,5)
    kern = numpy.ones((3,3),numpy.uint8)
    imgDilate = cv2.dilate(imgMedian, kern, iterations=1)

    checkSpace(imgDilate)

    cv2.imshow("Image",img)
    cv2.imshow("ImageBlur", imgDilate)
    cv2.waitKey(1)