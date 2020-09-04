package com.aias.demo

import com.aias.aias_client.Signed

data class Token (val random: Int)
data class Messages (val data: List<String>)