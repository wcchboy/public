package com.igrs.projection.entity

data class TransferDevice(
    val deviceIp: String,
    val deviceName: String,
    val deviceType: Int,
    val deviceStatus: Int,
    val castCode: String
)
