package com.aias.demo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent()
        intent.setClassName("com.aias.aias", "com.aias.aias.SignActivity");

        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"

        intent.putExtra(Intent.EXTRA_TEXT, "hoge")
        startActivity(intent)

        val message = intent.getStringExtra("message")
        Toast.makeText(this, message, Toast.LENGTH_SHORT)
    }
}