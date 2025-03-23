import cv2
import pickle

width, height = 60, 100
spotsList = []

with open('parkingSpots', 'rb') as f:
	spotsList = pickle.load(f)



def mouseClick(events,x,y,flags,params):
	if events == cv2.EVENT_LBUTTONDOWN:
		spotsList.append((x,y))

	if events == cv2.EVENT_RBUTTONDOWN:
		for i, pos in enumerate(spotsList):
			x1,y1 = pos

			if x1 < x < x1+width and y1 < y < y1+height:
				spotsList.pop(i)


	with open('parkingSpots', 'wb') as f:
		pickle.dump(spotsList, f)


while True:

	img = cv2.imread('parkingLotEmpty.png')

	for pos in spotsList:
		cv2.rectangle(img, pos, (pos[0] + width, pos[1] + height), (255, 0, 255), 2)

	#cv2.rectangle(img,(90,800),(150,900),(255,0,255),2)
	cv2.imshow("image",img)
	cv2.setMouseCallback("image", mouseClick)
	cv2.waitKey(1)