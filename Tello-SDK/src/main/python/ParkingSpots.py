import cv2
import pickle
import os
import tkinter as tk
from tkinter import messagebox

width, height = 60, 100
spotsList = []

# Use absolute path to locate the ParkingSpots file relative to the script's directory
script_dir = os.path.dirname(os.path.abspath(__file__))
spots_path = os.path.join(script_dir, 'ParkingSpots')

# Load existing spot list if the file exists
if os.path.exists(spots_path):
    with open(spots_path, 'rb') as f:
        spotsList = pickle.load(f)

# GUI prompt to ask user which image to use
root = tk.Tk()
root.withdraw()  # Hide main Tk window

choice = messagebox.askquestion("Select Image Source", "Use latest drone picture?")

if choice == 'yes':
    img_path = os.path.join(script_dir, 'latest.jpg') 
else:
    img_path = os.path.join(script_dir, 'parkingLotEmpty.png')


def mouseClick(events, x, y, flags, params):
    if events == cv2.EVENT_LBUTTONDOWN:
        spotsList.append((x, y))

    if events == cv2.EVENT_RBUTTONDOWN:
        for i, pos in enumerate(spotsList):
            x1, y1 = pos
            if x1 < x < x1 + width and y1 < y < y1 + height:
                spotsList.pop(i)

    # Save the updated spots list
    with open(spots_path, 'wb') as f:
        pickle.dump(spotsList, f)


while True:
    img = cv2.imread(img_path)
    if img is None:
        print("Failed to load image.")
        break

    for pos in spotsList:
        cv2.rectangle(img, pos, (pos[0] + width, pos[1] + height), (255, 0, 255), 2)

    cv2.imshow("image", img)
    cv2.setMouseCallback("image", mouseClick)
    cv2.waitKey(1)