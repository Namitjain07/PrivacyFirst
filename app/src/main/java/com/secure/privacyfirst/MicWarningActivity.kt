package com.secure.privacyfirst
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MicWarningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mic_warning)  // connects XML layout

        val yesButton = findViewById<Button>(R.id.yesButton)
        val noButton = findViewById<Button>(R.id.noButton)

        yesButton.setOnClickListener {
            finish()
        }

        noButton.setOnClickListener {
            // If it wasn't them, logout or go back to login screen
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
