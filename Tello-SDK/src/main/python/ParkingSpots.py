import cv2
import pickle
import os
import subprocess

width, height = 60, 100
spotsList = []

script_dir_fix = os.path.dirname(os.path.abspath(__file__))
file_path_fix_spots = os.path.join(script_dir_fix, "parkingSpots")
file_path_fix_empty = os.path.join(script_dir_fix, "parkingLotEmpty.png")

with open(file_path_fix_spots, 'rb') as f:
	spotsList = pickle.load(f)


def mouseClick(events,x,y,flags,params):
	if events == cv2.EVENT_LBUTTONDOWN:
		spotsList.append((x,y))

	if events == cv2.EVENT_RBUTTONDOWN:
		for i, pos in enumerate(spotsList):
			x1,y1 = pos

			if x1 < x < x1+width and y1 < y < y1+height:
				spotsList.pop(i)


	with open(file_path_fix_spots, 'wb') as f:
		pickle.dump(spotsList, f)


while True:

	img = cv2.imread(file_path_fix_empty)

	for pos in spotsList:
		cv2.rectangle(img, pos, (pos[0] + width, pos[1] + height), (255, 0, 255), 2)

	#cv2.rectangle(img,(90,800),(150,900),(255,0,255),2)
	cv2.imshow("image",img)
	cv2.setMouseCallback("image", mouseClick)
	cv2.waitKey(1)

	if cv2.getWindowProperty("image", cv2.WND_PROP_VISIBLE) < 1:
		print("Closing Window!")
		break

cv2.destroyAllWindows()

nextPyScript = os.path.join(script_dir_fix, "parkingCheck.py")
subprocess.run(["python", nextPyScript])