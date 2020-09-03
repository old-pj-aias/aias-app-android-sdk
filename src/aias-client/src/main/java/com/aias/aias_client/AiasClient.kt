package com.aias.aias_client

import android.app.Activity
import android.content.Intent
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.fasterxml.jackson.databind.ObjectMapper
import java.nio.charset.StandardCharsets
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import java.security.Signature
import java.util.Base64


class AiasClient (activity: Activity){
    var fairBlindSignature = "";
    var publicKey = "";

    val activity = activity

    val KEY_PROVIDER = "AndroidKeyStore"
    val keyStore = KeyStore.getInstance(KEY_PROVIDER)

    init {
        keyStore.load(null);

        if (!keyStore.containsAlias(KEY_PROVIDER)) {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, KEY_PROVIDER
            )

            val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
                "hoge",
                KeyProperties.PURPOSE_SIGN
            ).run {
                setDigests(KeyProperties.DIGEST_SHA256)
                setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                build()
            }

            keyPairGenerator.initialize(parameterSpec)
            val kp = keyPairGenerator.generateKeyPair()
        }
    }

    fun startFBSSignActivity(){
        val pubkey : PublicKey = keyStore.getCertificate(KEY_PROVIDER).publicKey
        publicKey = Base64.getEncoder().encode(pubkey.encoded).toString(Charsets.UTF_8)

        val intent = Intent()
        intent.setClassName("com.aias.aias", "com.aias.aias.SignActivity");

        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"

        intent.putExtra(Intent.EXTRA_TEXT, publicKey)
        activity.startActivityForResult(intent, 9)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if (requestCode != 9) { return }

        if (resultCode == Activity.RESULT_OK && data != null) {
            fairBlindSignature = data.getStringExtra(Intent.EXTRA_TEXT)
        }
    }

    fun generateAuth (data: String, token: Int) : String {
        val mapper = ObjectMapper()

        val signed = signed(data, token)
        val signedStr = mapper.writeValueAsString(signed).toByteArray(StandardCharsets.UTF_8);

        val entry = keyStore.getEntry("hoge", null)

        val s = Signature.getInstance("SHA256withRSA")
        s.initSign((entry as KeyStore.PrivateKeyEntry).privateKey)
        s.update(signedStr)

        val signature = s.sign()
        val result = Base64.getEncoder().encode(signature).toString(Charsets.UTF_8)

        val data = Data(signed, fairBlindSignature, publicKey, result)
        val dataStr = mapper.writeValueAsString(data)

        return dataStr;
    }
}
