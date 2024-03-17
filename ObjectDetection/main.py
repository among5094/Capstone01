import cv2
import numpy as np

# YOLO 모델 로드
net = cv2.dnn.readNet("yolov3.weights", "yolov3.cfg")
classes = []
with open("yolo.names", "r") as f:
    classes = [line.strip() for line in f.readlines()]
layer_names = net.getLayerNames()
output_layers = [layer_names[i-1] for i in net.getUnconnectedOutLayers()]
colors = np.random.uniform(0, 255, size=(len(classes), 3))

# 이미지 가져오기: cat.jpg  raccoon.jpg    Cat_Dog_IMG.jpg  cats_3.jpg
img = cv2.imread("cat.jpg") #복사한 이미지
if img is None:
    print("이미지 파일을 읽을 수 없습니다.")
    exit()

img = cv2.resize(img, None, fx=0.4, fy=0.4)
height, width, channels = img.shape

# 객체 감지
blob = cv2.dnn.blobFromImage(img, 0.00392, (416, 416), (0, 0, 0), True, crop=False)
net.setInput(blob)
outs = net.forward(output_layers)

# 정보를 화면에 표시
class_ids = []
confidences = []
boxes = []

# 임계값 설정 (낮은 값으로 변경)
confidence_threshold = 0.4

for out in outs:
    for detection in out:
        scores = detection[5:]
        class_id = np.argmax(scores)
        confidence = scores[class_id]
        if confidence > confidence_threshold:
            # 객체 감지
            center_x = int(detection[0] * width)
            center_y = int(detection[1] * height)
            w = int(detection[2] * width)
            h = int(detection[3] * height)
            x = int(center_x - w / 2)
            y = int(center_y - h / 2)
            boxes.append([x, y, w, h])
            confidences.append(float(confidence))
            class_ids.append(class_id)

# NMS 적용
indexes = cv2.dnn.NMSBoxes(boxes, confidences, confidence_threshold, 0.4)

# 객체 수 세기
detected_objects = len(indexes)

# 이미지에 객체 수 표시
cv2.putText(img, "Detected Objects: " + str(detected_objects), (10, 30), cv2.FONT_HERSHEY_PLAIN, 2, (255, 0, 0), 2)


for i in range(len(boxes)):
    if i in indexes:
        x, y, w, h = boxes[i]
        label = str(classes[class_ids[i]])
        color = colors[class_ids[i]]
        cv2.rectangle(img, (x, y), (x + w, y + h), color, 2)
        cv2.putText(img, label, (x, y + 30), cv2.FONT_HERSHEY_PLAIN, 2, color, 2)


cv2.imshow("Image", img)
cv2.waitKey(0)
cv2.destroyAllWindows()
