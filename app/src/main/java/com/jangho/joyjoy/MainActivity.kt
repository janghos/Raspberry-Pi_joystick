package com.jangho.joyjoy

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
            val handler = Handler(Looper.getMainLooper())  // 메인 쓰레드에서 핸들러 생성
            var releaseRunnable: Runnable? = null

             button.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        pressedKeys?.let {
                            for(i in it) {
                                sendCommand("release_$i")
                                sendCommand("release_$i")
                            }
                        }
                        // 만약 다른 화살표를 눌렀다면 이전 키 release하고 새 키 press
                        if (lastDirectionKey != null && lastDirectionKey != command) {
                            sendCommand("release_$lastDirectionKey")  // 이전 방향키 release
                        }

                        sendCommand("press_$command")  // 새로운 방향키 press
                        pressedKeys.add(command)
                        lastDirectionKey = command  // 마지막 방향키 저장

                        // 릴리즈를 강제로 보내는 Runnable 생성
                        releaseRunnable = Runnable {
                            sendCommand("release_$command")
                            sendCommand("release_$command")
                            pressedKeys.remove(command)
                            if (command == lastDirectionKey) {
                                lastDirectionKey = null  // 마지막 방향키 초기화
                            }
                        }

                         // 1초 후에 release 명령을 강제로 보내도록 타이머 시작
                        handler.postDelayed(releaseRunnable!!, 1000)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        // 버튼을 뗐을 때 release 명령 전송
                        sendCommand("release_$command")
                        sendCommand("release_$command")
                        pressedKeys.remove(command)
                        if (command == lastDirectionKey) {
                            lastDirectionKey = null  // 마지막 방향키 초기화
                        }

                        // 만약 타이머가 설정되어 있다면 취소
                        releaseRunnable?.let {
                            handler.removeCallbacks(it)
                        }
                    }
                }
                true  // 이벤트 처리 완료
            }
        }
        infiButton.setOnTouchListener { _, event  ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if(mIs1p) {
                        sendCommand("press_P")
                        pressedKeys.add("P")
                    }else {
                        sendCommand("press_T")
                        pressedKeys.add("T")
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if(mIs1p){
                        sendCommand("release_P")
                        sendCommand("release_P")
                        pressedKeys.remove("P")
                    }else {
                        sendCommand("release_T")
                        sendCommand("release_T")
                        pressedKeys.remove("T")
                    }

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