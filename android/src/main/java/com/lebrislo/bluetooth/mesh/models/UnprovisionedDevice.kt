package com.lebrislo.bluetooth.mesh.models

class UnprovisionedDevice(
    val rssi: Int,
    val macAddress: String,
    val name: String,
    val advData: ByteArray
) {
}