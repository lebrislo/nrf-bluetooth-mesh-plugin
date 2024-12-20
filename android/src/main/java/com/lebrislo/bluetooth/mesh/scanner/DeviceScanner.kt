package com.lebrislo.bluetooth.mesh.scanner

import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.util.Log
import com.getcapacitor.JSArray
import com.getcapacitor.JSObject
import com.lebrislo.bluetooth.mesh.BluetoothMeshPlugin
import com.lebrislo.bluetooth.mesh.models.ExtendedBluetoothDevice
import com.lebrislo.bluetooth.mesh.utils.NotificationManager
import com.lebrislo.bluetooth.mesh.utils.PermissionsManager
import com.lebrislo.bluetooth.mesh.utils.Utils
import no.nordicsemi.android.mesh.MeshManagerApi
import no.nordicsemi.android.mesh.MeshNetwork
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanFilter
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings


/**
 * DeviceScanner is a class that provides the functionality to scan for bluetooth devices
 * advertising with the Mesh Provisioning or Mesh Proxy service UUIDs.
 */
class DeviceScanner(
    private val meshManagerApi: MeshManagerApi
) {
    private val tag: String = "ScannerRepo"

    private var meshProxyScannedCallback: ((proxy: ExtendedBluetoothDevice) -> Unit)? = null
    private var isScanning: Boolean = false

    val unprovisionedDevices: MutableList<ExtendedBluetoothDevice> = mutableListOf()
    val provisionedDevices: MutableList<ExtendedBluetoothDevice> = mutableListOf()

    private val scanCallback: ScanCallback = object : ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val serviceUuid = result.scanRecord?.serviceUuids?.get(0)?.uuid
            if (serviceUuid == MeshManagerApi.MESH_PROVISIONING_UUID) {
                Log.v(tag, "Unprovisioned device discovered: ${result.device.address}")
                unprovDeviceDiscovered(result)
            } else if (serviceUuid == MeshManagerApi.MESH_PROXY_UUID) {
                val serviceData: ByteArray? = Utils.getServiceData(result, MeshManagerApi.MESH_PROXY_UUID)
                Log.v(tag, "Proxy discovered: ${result.device.address}")
                if (meshManagerApi.isAdvertisingWithNetworkIdentity(serviceData)) {
                    if (meshManagerApi.networkIdMatches(serviceData)) {
                        provDeviceDiscovered(result)
                    }
                } else if (meshManagerApi.isAdvertisedWithNodeIdentity(serviceData)) {
                    if (checkIfNodeIdentityMatches(serviceData!!)) {
                        provDeviceDiscovered(result)
                    }
                }
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {

        }

        override fun onScanFailed(errorCode: Int) {
            stopScanDevices()
        }
    }

    private fun unprovDeviceDiscovered(result: ScanResult) {
        val device = ExtendedBluetoothDevice(result)
        synchronized(unprovisionedDevices) {
            if (unprovisionedDevices.any { it.address == device.address }) {
                return
            }
            Log.d(tag, "Unprovisioned device discovered: ${result.device.address}")
            unprovisionedDevices.add(device)

            // Get the device UUID
            val deviceUuid = device.getDeviceUuid() ?: return

            // Delete the node from the mesh network if it was previously provisioned
            synchronized(provisionedDevices) {
                provisionedDevices.forEach { provisionedDevice ->
                    if (provisionedDevice.address == device.address) {
                        Log.d(tag, "Deleting provisioned device: ${provisionedDevice.address}")
                        meshManagerApi.meshNetwork?.nodes?.forEach { node ->
                            if (node.uuid == deviceUuid.toString()) {
                                meshManagerApi.meshNetwork?.deleteNode(node)
                            }
                        }
                    }
                }
            }
            // Notify about the scanned device
            this.notifyMeshDeviceScanned()
        }
    }


    private fun provDeviceDiscovered(result: ScanResult) {
        val device: ExtendedBluetoothDevice
        val scanRecord = result.scanRecord

        if (scanRecord != null) {
            if (scanRecord.bytes != null && scanRecord.serviceUuids != null) {
                device = ExtendedBluetoothDevice(result)
                if (!provisionedDevices.contains(device)) {
                    Log.d(tag, "Provisioned device discovered: ${result.device.address} ")
                    synchronized(provisionedDevices) {
                        provisionedDevices.add(device)

                        // Notify the callback
                        this.meshProxyScannedCallback?.invoke(device)

                        // Delete the node from the unprovisioned devices
                        synchronized(unprovisionedDevices) {
                            val devicesToRemove = mutableListOf<ExtendedBluetoothDevice>()
                            unprovisionedDevices.forEach { unprovisionedDevice ->
                                if (unprovisionedDevice.address == device.address) {
                                    Log.d(
                                        tag,
                                        "Marking unprovisioned device for removal: ${unprovisionedDevice.address}"
                                    )
                                    devicesToRemove.add(unprovisionedDevice)
                                }
                            }
                            unprovisionedDevices.removeAll(devicesToRemove)
                        }
                        // Notify about the scanned device
                        this.notifyMeshDeviceScanned()
                    }
                }
            }
        }
    }

    private fun notifyMeshDeviceScanned() {
        val scanNotification = JSObject().apply {
            put("unprovisioned", JSArray().apply {
                unprovisionedDevices.forEach {
                    put(JSObject().apply {
                        put("uuid", it.getDeviceUuid().toString())
                        put("macAddress", it.address)
                        put("rssi", it.rssi)
                        put("name", it.name)
                    })
                }
            })
            put("provisioned", JSArray().apply {
                provisionedDevices.forEach {
                    put(JSObject().apply {
                        put("uuid", it.getDeviceUuid().toString())
                        put("macAddress", it.address)
                        put("rssi", it.rssi)
                        put("name", it.name)
                    })
                }
            })
        }

        // Notify the listeners
        NotificationManager.getInstance().sendNotification(BluetoothMeshPlugin.MESH_DEVICE_SCAN_EVENT, scanNotification)
    }

    /**
     * Set the callback to be called when a mesh device is scanned
     *
     * @param callback the callback to be called
     */
    fun setMeshProxyScannedCallback(callback: (proxy: ExtendedBluetoothDevice) -> Unit) {
        this.meshProxyScannedCallback = callback
    }


    /**
     * Start scanning for bluetooth devices that are advertising with the Mesh Provisioning or Mesh Proxy service UUIDs.
     *
     * MESH_PROVISIONING_UUID: 00001827-0000-1000-8000-00805F9B34FB
     * MESH_PROXY_UUID: 00001828-0000-1000-8000-00805F9B34FB
     */
    fun startScanDevices() {

        val permission = PermissionsManager.getInstance().requestPermissions()
        Log.i(tag, "Permission to scan devices: $permission")
        if (permission != PackageManager.PERMISSION_GRANTED) {
            return
        }

        //Scanning settings
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // Refresh the devices list every second
            .setReportDelay(0) // Hardware filtering has some issues on selected devices
            .setUseHardwareFilteringIfSupported(false) // Samsung S6 and S6 Edge report equal value of RSSI for all devices. In this app we ignore the RSSI.
            .build()

        //Let's use the filter to scan only for unprovisioned mesh nodes.
        val filters: MutableList<ScanFilter> = ArrayList()
        filters.add(
            ScanFilter.Builder().setServiceUuid(
                ParcelUuid(
                    (MeshManagerApi.MESH_PROVISIONING_UUID)
                )
            ).build()
        )
        filters.add(
            ScanFilter.Builder().setServiceUuid(
                ParcelUuid(
                    (MeshManagerApi.MESH_PROXY_UUID)
                )
            ).build()
        )

        synchronized(this) {
            Log.v(tag, "startScanDevices isScanning: $isScanning")
            if (isScanning) {
                return
            }
            isScanning = true
            val scanner: BluetoothLeScannerCompat = BluetoothLeScannerCompat.getScanner()
            scanner.startScan(filters, settings, scanCallback)
        }
    }

    /**
     * stop scanning for bluetooth devices.
     */
    fun stopScanDevices() {
        synchronized(this) {
            Log.v(tag, "stopScan isScanning: $isScanning")
            if (!isScanning) {
                return
            }
            isScanning = false

            val scanner: BluetoothLeScannerCompat = BluetoothLeScannerCompat.getScanner()
            scanner.stopScan(scanCallback)
        }
    }

    /**
     * Check if node identity matches
     *
     * @param serviceData service data received from the advertising data
     * @return true if the node identity matches or false otherwise
     */
    private fun checkIfNodeIdentityMatches(serviceData: ByteArray): Boolean {
        val network: MeshNetwork? = meshManagerApi.meshNetwork
        if (network != null) {
            for (node in network.nodes) {
                if (meshManagerApi.nodeIdentityMatches(node, serviceData)) {
                    return true
                }
            }
        }
        return false
    }
}
