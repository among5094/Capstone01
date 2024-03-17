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
img = cv2.imread("img/21_4elephant.jpg") #복사한 이미지
if img is None:
    print("이미지 파일을 읽을 수 없습니다.")
    exit()

# 원하는 이미지 크기 설정 <-고정된 이미지 크기
desired_width = 800
desired_height = 600

# 이미지를 고정된 크기로 조정
img = cv2.resize(img, (desired_width, desired_height)) #img = cv2.resize(img, None, fx=0.4, fy=0.4) #원시코드-원본의 0.4배로 축소
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

# 이미지 크기에 따른 텍스트 스케일링을 위한 비율 계산
scale_factor = width / 1000 #이미지의 너비를 기준으로 계산
text_scale = max(1, scale_factor*1.5)  # 최소 텍스트 크기를 0.5로 설정
rectangle_thickness = max(2, int(scale_factor*2))  # 사각형 두께도 비례하게 조절

# 객체 수를 이미지에 표시 (스케일 조정된 텍스트 사용)
cv2.putText(img, "Detected Objects: " + str(detected_objects), (10, int(50 * scale_factor)), cv2.FONT_HERSHEY_PLAIN, text_scale, (255, 0, 0), rectangle_thickness)


for i in range(len(boxes)):
    if i in indexes:
        x, y, w, h = boxes[i]
        label = str(classes[class_ids[i]])
        color = colors[class_ids[i]]
        # 객체 주위에 사각형 그리기 (스케일 조정된 두께 사용)
        cv2.rectangle(img, (x, y), (x + w, y + h), color, rectangle_thickness)
        # 라벨 표시 (스케일 조정된 텍스트 사용)
        cv2.putText(img, label, (x, y + int(50 * scale_factor)), cv2.FONT_HERSHEY_PLAIN, text_scale, color, rectangle_thickness)


cv2.imshow("Image", img)
cv2.waitKey(0)
cv2.destroyAllWindows()
