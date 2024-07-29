package com.lebrislo.bluetooth.mesh.ble

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import no.nordicsemi.android.mesh.MeshManagerApi

class BleCallbacksManager(val meshManagerApi: MeshManagerApi) : BleCallbacks {
    private val tag: String = BleCallbacksManager::class.java.simpleName

    private var isConnected = MutableLiveData<Boolean>()

    fun initConnectedLiveData() {
        isConnected = MutableLiveData<Boolean>()
    }

    fun isConnected(): LiveData<Boolean> {
        return isConnected
    }

    override fun onDataReceived(bluetoothDevice: BluetoothDevice?, mtu: Int, pdu: ByteArray?) {
        meshManagerApi.handleNotifications(mtu, pdu!!)
    }

    override fun onDataSent(device: BluetoothDevice?, mtu: Int, pdu: ByteArray?) {
        meshManagerApi.handleWriteCallbacks(mtu, pdu!!)
    }

    override fun onDeviceConnecting(device: BluetoothDevice) {
        Log.d(tag, "onDeviceConnecting")
    }

    override fun onDeviceConnected(device: BluetoothDevice) {
        Log.d(tag, "onDeviceConnected")
        isConnected.postValue(true)
    }

    override fun onDeviceDisconnecting(device: BluetoothDevice) {
        Log.d(tag, "onDeviceDisconnecting")
    }

    override fun onDeviceDisconnected(device: BluetoothDevice) {
        Log.d(tag, "onDeviceDisconnected")
        isConnected.postValue(false)
    }

    override fun onLinkLossOccurred(device: BluetoothDevice) {
        Log.d(tag, "onLinkLossOccurred")
        isConnected.postValue(false)
    }

    override fun onServicesDiscovered(device: BluetoothDevice, optionalServicesFound: Boolean) {
        Log.d(tag, "onServicesDiscovered")
    }

    override fun onDeviceReady(device: BluetoothDevice) {
        Log.d(tag, "onDeviceReady")
    }

    override fun onBondingRequired(device: BluetoothDevice) {
        Log.d(tag, "onBondingRequired")
    }

    override fun onBonded(device: BluetoothDevice) {
        TODO("Not yet implemented")
    }

    override fun onBondingFailed(device: BluetoothDevice) {
        TODO("Not yet implemented")
    }

    override fun onError(device: BluetoothDevice, message: String, errorCode: Int) {
        TODO("Not yet implemented")
    }

    override fun onDeviceNotSupported(device: BluetoothDevice) {
        TODO("Not yet implemented")
    }
}