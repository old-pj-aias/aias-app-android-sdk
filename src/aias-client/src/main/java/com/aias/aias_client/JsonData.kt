package com.aias.aias_client

data class signed (val data: String, val random: Int);
data class Data (val signed: signed, val fair_blind_signature: String, val pubkey: String, val signature: String);