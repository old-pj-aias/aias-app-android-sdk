package com.aias.aias_client

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore


class AiasClient (activity: Activity){
    var fairBlindSignature = "";
    val activity = activity

    init {

    }

    fun startFBSSignActivity(){
        val intent = Intent()
        intent.setClassName("com.aias.aias", "com.aias.aias.SignActivity");

        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"

        intent.putExtra(Intent.EXTRA_TEXT, "hoge")
        activity.startActivityForResult(intent, 9)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if (requestCode != 9) { return }

        if (resultCode == Activity.RESULT_OK && data != null) {
            fairBlindSignature = data.getStringExtra(Intent.EXTRA_TEXT)
        }
    }
}