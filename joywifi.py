import board
import wifi
import socketpool
import usb_hid
from adafruit_hid.keyboard import Keyboard
from adafruit_hid.keycode import Keycode
import time

# Wi-Fi 설정
SSID = "장장"
PASSWORD = "78167816"

# Wi-Fi 연결
print("Connecting to Wi-Fi...")
wifi.radio.connect(SSID, PASSWORD)
print("Connected to Wi-Fi!")
print("IP Address:", wifi.radio.ipv4_address)

# 소켓 풀 생성
pool = socketpool.SocketPool(wifi.radio)
server_socket = pool.socket(pool.AF_INET, pool.SOCK_STREAM)

# 서버 소켓 설정
server_socket.bind(('172.20.10.3', 5000))  # 포트 5000에서 대기
server_socket.listen(1)
print("Socket listening on port 5000...")

# Keyboard 객체 생성
keyboard = Keyboard(usb_hid.devices)

# 눌린 키들을 추적하는 세트
pressed_keys = set()

# 명령어 처리 함수
# 명령어 처리 함수
def handle_command(command):
    print(f"Handling command: {command}")

    if command == "press_up" and "up" not in pressed_keys:
        keyboard.press(Keycode.UP_ARROW)  # 화살표 위
        pressed_keys.add("up")
        print("Pressed up arrow")
       # time.sleep(0.1)
    elif command == "press_down" and "down" not in pressed_keys:
        keyboard.press(Keycode.DOWN_ARROW)  # 화살표 아래
        pressed_keys.add("down")
        print("Pressed down arrow")
       # time.sleep(0.1)
    elif command == "press_left" and "left" not in pressed_keys:
        keyboard.press(Keycode.LEFT_ARROW)  # 화살표 왼쪽
        pressed_keys.add("left")
        print("Pressed left arrow")
       # time.sleep(0.1)
    elif command == "press_right" and "right" not in pressed_keys:
        keyboard.press(Keycode.RIGHT_ARROW)  # 화살표 오른쪽
        pressed_keys.add("right")
        print("Pressed right arrow")
        #time.sleep(0.1)
    elif command == "press_space" and "space" not in pressed_keys:
        keyboard.press(Keycode.SPACE)  # 스페이스바
        pressed_keys.add("space")
        print("Pressed space")
        #time.sleep(0.1)
    elif command == "press_W" and "W" not in pressed_keys:
        keyboard.press(Keycode.W)  # W
        pressed_keys.add("W")
        print("Pressed W")
        #time.sleep(0.1)
    elif command == "press_A" and "A" not in pressed_keys:
        keyboard.press(Keycode.A)  # A
        pressed_keys.add("A")
        print("Pressed A")
        #time.sleep(0.1)
    elif command == "press_S" and "S" not in pressed_keys:
        keyboard.press(Keycode.S)  # S
        pressed_keys.add("S")
        print("Pressed S")
        #time.sleep(0.1)
    elif command == "press_D" and "D" not in pressed_keys:
        keyboard.press(Keycode.D)  # D
        pressed_keys.add("D")
        print("Pressed D")
        #time.sleep(0.1)
    elif command == "press_T" and "T" not in pressed_keys:
        keyboard.press(Keycode.T)  # T
        pressed_keys.add("T")
        print("Pressed T")
        #time.sleep(0.1)
    elif command == "press_P" and "P" not in pressed_keys:
        keyboard.press(Keycode.P)  # P
        pressed_keys.add("P")
        print("Pressed P")
        #time.sleep(0.1)
    elif command == "press_U" and "U" not in pressed_keys:
        keyboard.press(Keycode.U)  # U
        pressed_keys.add("U")
        print("Pressed U")
        #time.sleep(0.1)

    # release 처리
    elif command.startswith("release_"):
        key = command.split("_")[1]
        # only release the key if it is currently pressed
        if key in pressed_keys:
            if key == "right":
                keyboard.release(Keycode.RIGHT_ARROW)  # 화살표 오른쪽 해제
            elif key == "left":
                keyboard.release(Keycode.LEFT_ARROW)  # 화살표 왼쪽 해제
            elif key == "up":
                keyboard.release(Keycode.UP_ARROW)  # 화살표 위 해제
            elif key == "down":
                keyboard.release(Keycode.DOWN_ARROW)  # 화살표 아래 해제
            elif key == "space":
                keyboard.release(Keycode.SPACE)  # 스페이스바 해제
            elif key == "W":
                keyboard.release(Keycode.W)  # W 해제
            elif key == "A":
                keyboard.release(Keycode.A)  # A 해제
            elif key == "S":
                keyboard.release(Keycode.S)  # S 해제
            elif key == "D":
                keyboard.release(Keycode.D)  # D 해제
            elif key == "T":
                keyboard.release(Keycode.T)  # T 해제
            elif key == "U":
                keyboard.release(Keycode.U)  # U 해제
            elif key == "P":
                keyboard.release(Keycode.P)  #  해제

            pressed_keys.remove(key)
            print(f"Released {key}")
            #time.sleep(0.1)  # 키 해제 후 약간의 지연을 두기

buffer = bytearray(1024)  # 데이터를 저장할 버퍼

while True:
    print('Waiting for connections...')
    conn, addr = server_socket.accept()
    print(f"Connected by {addr}")

    # 데이터 버퍼에 수신
    bytes_received = conn.recv_into(buffer)
    if bytes_received:
        command = buffer[:bytes_received].decode("utf-8").strip()
        print(f"Received command: {command}")

        # 명령어 처리
        handle_command(command)

    conn.close()
