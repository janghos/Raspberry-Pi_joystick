package com.jangho.joyjoy

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.net.Socket

class MainActivity : AppCompatActivity() {

    private val ESP32_IP = "172.20.10.3"  // ESP32 IP 주소
    private val PORT = 5000  // ESP32에서 사용하는 포트
    private var mIs1p = true
    private val pressedKeys = mutableSetOf<String>()  // 누른 키들을 추적
    private var lastDirectionKey: String? = null  // 마지막 눌린 방향 키

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val upButton: Button = findViewById(R.id.btn_up)
        val downButton: Button = findViewById(R.id.btn_down)
        val leftButton: Button = findViewById(R.id.btn_left)
        val rightButton: Button = findViewById(R.id.btn_right)
        val spaceButton: Button = findViewById(R.id.btn_attack)
        val switchButton: Button = findViewById(R.id.btn_switch)
        val infiButton: Button = findViewById(R.id.btn_infinity)

        fun setupTouchListener(button: Button, command: String) {
            button.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 만약 다른 화살표를 눌렀다면 이전 키 release하고 새 키 press
                        if (lastDirectionKey != null && lastDirectionKey != command) {
                            sendCommand("release_$lastDirectionKey")  // 이전 방향키 release
                        }
//                        if (!pressedKeys.contains(command)) {
                            sendCommand("press_$command")  // 새로운 방향키 press
                            pressedKeys.add(command)
                            lastDirectionKey = command  // 마지막 방향키 저장
//                        }
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        sendCommand("release_$command")  // 버튼을 뗐을 때 release 명령 전송
                        pressedKeys.remove(command)
                        if (command == lastDirectionKey) {
                            lastDirectionKey = null  // 마지막 방향키 초기화
                        }
                    }
                }
                true  // 이벤트 처리 완료
            }
        }

        infiButton.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    sendCommand("press_T")
                    pressedKeys.add("T")
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    sendCommand("release_T")
                    pressedKeys.remove("T")
                }
            }
            true
        }

        switchButton.text = "1P MODE"
        setupTouchListener(upButton, "up")
        setupTouchListener(downButton, "down")
        setupTouchListener(leftButton, "left")
        setupTouchListener(rightButton, "right")
        setupTouchListener(spaceButton, "space")

        switchButton.setOnClickListener {
            if (mIs1p) {
                switchButton.text = "2P MODE"
                setupTouchListener(upButton, "W")
                setupTouchListener(downButton, "S")
                setupTouchListener(leftButton, "A")
                setupTouchListener(rightButton, "D")
                setupTouchListener(spaceButton, "U")
            } else {
                switchButton.text = "1P MODE"
                setupTouchListener(upButton, "up")
                setupTouchListener(downButton, "down")
                setupTouchListener(leftButton, "left")
                setupTouchListener(rightButton, "right")
                setupTouchListener(spaceButton, "space")
            }
            mIs1p = !mIs1p

        }
    }

    private fun sendCommand(command: String) {
        Log.d("command", command)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 소켓을 사용하여 ESP32에 연결
                val socket = Socket(ESP32_IP, PORT)
                val outputStream: OutputStream = socket.getOutputStream()

                // 명령 전송
                outputStream.write(command.toByteArray())
                outputStream.flush()

                // 소켓 닫기
                socket.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}