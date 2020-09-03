package com.aias.aias_client

import android.app.Activity
import android.content.Intent
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.widget.Toast
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import java.util.*


class AiasClient (activity: Activity){
    var fairBlindSignature = "";
    val activity = activity

    val KEY_PROVIDER = "AndroidKeyStore"
    val keyStore = KeyStore.getInstance(KEY_PROVIDER)

    init {
        keyStore.load(null);

        if (!keyStore.containsAlias("Aias")) {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, KEY_PROVIDER
            );

            keyPairGenerator.initialize(
                KeyGenParameterSpec.Builder(
                    KEY_PROVIDER,
                    KeyProperties.PURPOSE_SIGN
                )
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                    .build()
            )

            keyPairGenerator.generateKeyPair();
        }
    }

    fun startFBSSignActivity(){
        val publicKey : PublicKey = keyStore.getCertificate(KEY_PROVIDER).publicKey
        val encoded = Base64.getEncoder().encode(publicKey.encoded).toString(Charsets.UTF_8)

        val intent = Intent()
        intent.setClassName("com.aias.aias", "com.aias.aias.SignActivity");

        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"

        intent.putExtra(Intent.EXTRA_TEXT, encoded)
        activity.startActivityForResult(intent, 9)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if (requestCode != 9) { return }

        if (resultCode == Activity.RESULT_OK && data != null) {
            fairBlindSignature = data.getStringExtra(Intent.EXTRA_TEXT)
        }
    }
}