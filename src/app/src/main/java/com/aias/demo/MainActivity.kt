package com.aias.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aias.aias_client.AiasClient
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.kittinunf.fuel.Fuel
import java.io.File.separator
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), View.OnClickListener {
    var aias : AiasClient? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        aias = AiasClient(this)
        aias?.startFBSSignActivity()

        thread {
            val (_, getResponse, getResult) = Fuel.get("http://10.0.2.2:5000/get")
                .response()

            val getResponseStr = String(getResponse.data)

            val mapper = jacksonObjectMapper()
            val responseJson = mapper.readValue<Messages>(getResponseStr)
            val value = responseJson.data.joinToString (separator = "\n")

            runOnUiThread {
                val text = findViewById<TextView>(R.id.body)
                text.text = value
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        aias?.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(this, aias?.fairBlindSignature, Toast.LENGTH_LONG).show()
    }

    override fun onClick(v: View?) {
        val text = findViewById<EditText>(R.id.text).text.toString()

        thread {
            val (_, tokenResponse, tokenResult) = Fuel.post("http://10.0.2.2:5000/token")
                .response()

            val tokenResponseStr = String(tokenResponse.data)

            val mapper = jacksonObjectMapper()
            val postResponseJson = mapper.readValue<Token>(tokenResponseStr)

            val cookie: String = tokenResponse.headers["Set-Cookie"]?.first()!!
            val decodedCookie = cookie.split(' ').first()
            val cookieHeader = mapOf("Cookie" to decodedCookie)

            val auth = aias?.generateAuth(text, postResponseJson.random).toString()

            val (_, postResponse, postResult) = Fuel.post("http://10.0.2.2:5000/post")
                .header(cookieHeader)
                .body(auth)
                .response()

            val postResponseStr = String(postResponse.data)

            val (_, getResponse, getResult) = Fuel.get("http://10.0.2.2:5000/get")
                .header(cookieHeader)
                .response()

            val getResponseStr = String(getResponse.data)
            val responseJson = mapper.readValue<Messages>(getResponseStr)
            val value = responseJson.data.joinToString (separator = "\n")

            runOnUiThread {
                val text = findViewById<TextView>(R.id.body)

                text.text = value
            }
        }
    }
}