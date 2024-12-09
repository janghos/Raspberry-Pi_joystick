package com.jangho.joyjoy

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.net.Socket

class MainActivity : AppCompatActivity() {

    private val ESP32_IP = "172.20.10.3"  // ESP32 IP 주소 (Wi-Fi 연결 후 확인)
    private val PORT = 5000  // ESP32에서 사용하는 포트
    private var mIs1p = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val upButton: Button = findViewById(R.id.btn_up)
        val downButton: Button = findViewById(R.id.btn_down)
        val leftButton: Button = findViewById(R.id.btn_left)
        val rightButton: Button = findViewById(R.id.btn_right)
        val spaceButton: Button = findViewById(R.id.btn_attack)
        val switchButton: Button = findViewById(R.id.btn_switch)
        switchButton.setOnClickListener {
            if(mIs1p){
                switchButton.text = "2P MODE"
                upButton.setOnClickListener { sendCommand("W") }
                downButton.setOnClickListener { sendCommand("S") }
                leftButton.setOnClickListener { sendCommand("A") }
                rightButton.setOnClickListener { sendCommand("D") }
                spaceButton.setOnClickListener { sendCommand("T") }
            }else {
                switchButton.text = "1P MODE"
                upButton.setOnClickListener { sendCommand("up") }
                downButton.setOnClickListener { sendCommand("down") }
                leftButton.setOnClickListener { sendCommand("left") }
                rightButton.setOnClickListener { sendCommand("right") }
                spaceButton.setOnClickListener { sendCommand("space") }
            }
            mIs1p = !mIs1p
        }

        upButton.setOnClickListener { sendCommand("up") }
        downButton.setOnClickListener { sendCommand("down") }
        leftButton.setOnClickListener { sendCommand("left") }
        rightButton.setOnClickListener { sendCommand("right") }
        spaceButton.setOnClickListener { sendCommand("space") }

    }

    private fun sendCommand(command: String) {
        Log.d("command", command.toString())
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
