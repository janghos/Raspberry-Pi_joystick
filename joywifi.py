import board
import wifi
import socketpool
import usb_hid
from adafruit_hid.keyboard import Keyboard
from adafruit_hid.keycode import Keycode

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

# 명령어 처리 함수
def handle_command(command):
    if command == "up":
        keyboard.press(Keycode.UP_ARROW)
        keyboard.release(Keycode.UP_ARROW)
    elif command == "down":
        keyboard.press(Keycode.DOWN_ARROW)
        keyboard.release(Keycode.DOWN_ARROW)
    elif command == "left":
        keyboard.press(Keycode.LEFT_ARROW)                 
        keyboard.release(Keycode.LEFT_ARROW)
    elif command == "right":
        keyboard.press(Keycode.RIGHT_ARROW)
        keyboard.release(Keycode.RIGHT_ARROW)
    elif command == "space":
        keyboard.press(Keycode.SPACE)
        keyboard.release(Keycode.SPACE)
    elif command == "W":
        keyboard.press(Keycode.W)
        keyboard.release(Keycode.W)
    elif command == "S":
        keyboard.press(Keycode.S)
        keyboard.release(Keycode.S)
    elif command == "A":
        keyboard.press(Keycode.A)
        keyboard.release(Keycode.A)
    elif command == "D":
        keyboard.press(Keycode.D)
        keyboard.release(Keycode.D)
    elif command == "T":
        print('T')
        keyboard.press(Keycode.T)
        keyboard.release(Keycode.T)
        

buffer = bytearray(1024)  # 데이터를 저장할 버퍼

while True:
    print('start')
    conn, addr = server_socket.accept()
    print(f"Connected by {addr}")

    # 데이터를 버퍼에 수신
    bytes_received = conn.recv_into(buffer)
    if bytes_received:
        command = buffer[:bytes_received].decode("utf-8").strip()
        print(f"Received command: {command}")
        handle_command(command)

    conn.close()