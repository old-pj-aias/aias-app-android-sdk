package com.aias.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aias.aias_client.AiasClient

class MainActivity : AppCompatActivity() {
    var aias : AiasClient? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        aias = AiasClient(this)
        aias?.startFBSSignActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        aias?.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(this, aias?.fairBlindSignature, Toast.LENGTH_LONG).show()

        val auth = aias?.generateAuth("hoge", 10)
        Toast.makeText(this, auth, Toast.LENGTH_LONG).show()
    }
}