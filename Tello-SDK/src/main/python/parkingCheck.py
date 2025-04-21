import cv2
import pickle
import numpy
import os  # Used to get the absolute path of the current script
import socket  # Handles network communication with the Java application
import struct  # Used to unpack byte lengths sent from Java

spotsList = []

# Construct an absolute path to the saved parking spot positions file
base_path = os.path.dirname(os.path.abspath(__file__))
spots_path = os.path.join(base_path, 'parkingSpots')

# Load the predefined parking spot positions from file
with open(spots_path, 'rb') as f:
    spotsList = pickle.load(f)

width, height = 60, 100

def checkSpace(processImage):
    for pos in spotsList:
        x, y = pos

        # Stops out of bounds errors when cropping image parts that are near edges
        if y + height > processImage.shape[0] or x + width > processImage.shape[1]:
            continue

        imgCrop = processImage[y:y+height, x:x+width]
        cv2.imshow(str(x * y), imgCrop)

        count = cv2.countNonZero(imgCrop)
        if count < 525:
            color = (0, 255, 0)
        else:
            color = (255, 0, 0)

        cv2.rectangle(img, pos, (pos[0] + width, pos[1] + height), color, 2)

# Creates a TCP socket connection to get frames from the Java code
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.connect(('localhost', 9997))  # Matches the port used in Java
data_buffer = b''  # Buffer to get the incoming bytes

while True:
    # Wait until at least 4 bytes are gotten to then decide the size of the next frame
    while len(data_buffer) < 4:
        packet = server_socket.recv(4 - len(data_buffer))
        if not packet:
            break
        data_buffer += packet

    # If not enough data is there to read the frame length it skips it
    if len(data_buffer) < 4:
        continue

    # Takes the size of the upcoming image frame
    frame_len = struct.unpack('>I', data_buffer[:4])[0]
    data_buffer = data_buffer[4:]

    # Wait until the full frame's worth of data is received
    while len(data_buffer) < frame_len:
        packet = server_socket.recv(frame_len - len(data_buffer))
        if not packet:
            break
        data_buffer += packet

    # Takes the image frame from the buffer
    frame_data = data_buffer[:frame_len]
    data_buffer = data_buffer[frame_len:]

    # Converts the raw byte array into a NumPy array and decodes it into an image
    img_array = numpy.frombuffer(frame_data, dtype=numpy.uint8)
    img = cv2.imdecode(img_array, cv2.IMREAD_COLOR)

    # If the image decoding fails it skips the loop
    if img is None:
        continue

    imgGray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    imgBlur = cv2.GaussianBlur(imgGray, (3, 3), 1)

    imgThreshold = cv2.adaptiveThreshold(
        imgBlur, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C,
        cv2.THRESH_BINARY_INV, 25, 16)

    imgMedian = cv2.medianBlur(imgThreshold, 5)
    kern = numpy.ones((3, 3), numpy.uint8)
    imgDilate = cv2.dilate(imgMedian, kern, iterations=1)

    checkSpace(imgDilate)

    cv2.imshow("Image", img)
    cv2.imshow("ImageBlur", imgDilate)
    cv2.waitKey(1)
