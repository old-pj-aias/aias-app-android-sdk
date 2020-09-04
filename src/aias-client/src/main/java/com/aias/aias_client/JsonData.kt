package com.aias.aias_client

data class Signed (val data: String, val random: Int)
data class Data (val signed: Signed, val fair_blind_signature: String, val pubkey: String, val signature: String)