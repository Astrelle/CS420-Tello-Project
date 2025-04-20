import cv2
import pickle
import os
import numpy

script_dir_fix = os.path.dirname(os.path.abspath(__file__))
file_path_fix_spots = os.path.join(script_dir_fix, "parkingSpots")
testVideo_dir_fix = os.path.join(script_dir_fix, "parking1.mp4")

capture = cv2.VideoCapture(testVideo_dir_fix)
spotsList = []

with open(file_path_fix_spots, 'rb') as f:
	spotsList = pickle.load(f)

width, height = 60, 100

def checkSpace(processImage):
	for pos in spotsList:
		x,y = pos

		imgCrop = processImage[y:y+height,x:x+width]
		cv2.imshow(str(x*y),imgCrop)

		count = cv2.countNonZero(imgCrop)
		if count < 525:
			color = (0, 255, 0)
		else:
			color = (255, 0, 0)

		cv2.rectangle(img, pos, (pos[0] + width, pos[1] + height), color, 2)



while True:

	if capture.get(cv2.CAP_PROP_POS_FRAMES) == capture.get(cv2.CAP_PROP_FRAME_COUNT):
		capture.set(cv2.CAP_PROP_POS_FRAMES, 0)

	success, img = capture.read()

	imgGray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	imgBlur = cv2.GaussianBlur(imgGray, (3, 3), 1)

	imgThreshold = cv2.adaptiveThreshold(imgBlur,255,cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY_INV,25,16)

	# where there is a car we'll have white pixels
	imgMedian = cv2.medianBlur(imgThreshold,5)
	kern = numpy.ones((3,3),numpy.uint8)
	imgDilate = cv2.dilate(imgMedian, kern, iterations=1)

	checkSpace(imgDilate)


	cv2.imshow("Image",img)
	cv2.imshow("ImageBlur", imgDilate)
	cv2.waitKey(1)

