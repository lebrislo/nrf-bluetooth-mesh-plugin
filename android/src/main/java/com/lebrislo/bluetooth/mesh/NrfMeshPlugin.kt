package com.lebrislo.bluetooth.mesh

import com.getcapacitor.JSArray
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin
import com.lebrislo.bluetooth.mesh.models.BleMeshDevice
import com.lebrislo.bluetooth.mesh.plugin.PluginCallManager
import com.lebrislo.bluetooth.mesh.utils.Permissions
import com.lebrislo.bluetooth.mesh.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.nordicsemi.android.mesh.MeshManagerApi
import no.nordicsemi.android.mesh.opcodes.ApplicationMessageOpCodes
import no.nordicsemi.android.mesh.opcodes.ConfigMessageOpCodes
import java.util.UUID


@CapacitorPlugin(name = "NrfMesh")
class NrfMeshPlugin : Plugin() {
    private val tag: String = NrfMeshPlugin::class.java.simpleName

    private lateinit var implementation: NrfMeshManager

    override fun load() {
        this.implementation = NrfMeshManager(this.context)
        PluginCallManager.getInstance().setPlugin(this)
    }

    fun sendNotification(eventName: String, data: JSObject) {
        if (!hasListeners(eventName)) {
            return
        }
        notifyListeners(eventName, data)
    }

    @PluginMethod
    fun scanUnprovisionedDevices(call: PluginCall) {
        val scanDuration = call.getInt("timeout", 5000)

        if (!Permissions.isBleEnabled(context)) {
            call.reject("Bluetooth is disabled")
        }

        if (!Permissions.isLocationGranted(context)) {
            call.reject("Location permission is required")
        }

        CoroutineScope(Dispatchers.Main).launch {
            val devices = implementation.scanUnprovisionedDevices(scanDuration!!)

            val result = JSArray().apply {
                devices.forEach {
                    val serviceData = Utils.getServiceData(
                        it.scanResult!!,
                        MeshManagerApi.MESH_PROVISIONING_UUID
                    )

                    if (serviceData == null || serviceData.size < 18) return@forEach

                    val uuid: UUID = implementation.meshManagerApi.getDeviceUuid(serviceData)

                    put(JSObject().apply {
                        put("uuid", uuid.toString())
                        put("macAddress", it.scanResult.device.address)
                        put("rssi", it.rssi)
                        put("name", it.name)
                    })
                }
            }
            call.resolve(JSObject().put("devices", result))
        }
    }

    @PluginMethod
    fun scanProvisionedDevices(call: PluginCall) {
        val scanDuration = call.getInt("timeout", 5000)

        if (!Permissions.isBleEnabled(context)) {
            call.reject("Bluetooth is disabled")
        }

        if (!Permissions.isLocationGranted(context)) {
            call.reject("Location permission is required")
        }

        CoroutineScope(Dispatchers.Main).launch {
            val devices = implementation.scanProvisionedDevices(scanDuration!!)

            val result = JSArray().apply {
                devices.forEach {
                    val serviceData = Utils.getServiceData(
                        it.scanResult!!,
                        MeshManagerApi.MESH_PROXY_UUID
                    )

                    if (serviceData == null || serviceData.size < 18) return@forEach

                    val uuid: UUID = implementation.meshManagerApi.getDeviceUuid(serviceData)

                    put(JSObject().apply {
                        put("uuid", uuid.toString())
                        put("macAddress", it.scanResult.device.address)
                        put("rssi", it.rssi)
                        put("name", it.name)
                    })
                }
            }
            call.resolve(JSObject().put("devices", result))
        }
    }

    @PluginMethod
    fun scanMeshDevices(call: PluginCall) {
        val scanDuration = call.getInt("timeout", 5000)

        if (!Permissions.isBleEnabled(context)) {
            call.reject("Bluetooth is disabled")
        }

        if (!Permissions.isLocationGranted(context)) {
            call.reject("Location permission is required")
        }

        CoroutineScope(Dispatchers.Main).launch {
            val devices = implementation.scanMeshDevices(scanDuration!!)

            // return a dict of devices, unprovisioned and provisioned
            val result = JSObject().apply {
                put("unprovisioned", JSArray().apply {
                    devices.forEach {
                        val serviceData = Utils.getServiceData(
                            it.scanResult!!,
                            MeshManagerApi.MESH_PROVISIONING_UUID
                        )

                        if (serviceData == null || serviceData.size < 18) return@forEach

                        val uuid: UUID = implementation.meshManagerApi.getDeviceUuid(serviceData)

                        put(JSObject().apply {
                            put("uuid", uuid.toString())
                            put("macAddress", it.scanResult.device.address)
                            put("rssi", it.rssi)
                            put("name", it.name)
                        })
                    }
                })
                put("provisioned", JSArray().apply {
                    devices.forEach {
                        val serviceData = Utils.getServiceData(
                            it.scanResult!!,
                            MeshManagerApi.MESH_PROXY_UUID
                        )

                        if (serviceData == null || serviceData.size < 18) return@forEach

                        val uuid: UUID = implementation.meshManagerApi.getDeviceUuid(serviceData)

                        put(JSObject().apply {
                            put("uuid", uuid.toString())
                            put("macAddress", it.scanResult.device.address)
                            put("rssi", it.rssi)
                            put("name", it.name)
                        })
                    }
                })
            }
            call.resolve(result)
        }
    }

    @PluginMethod
    fun getProvisioningCapabilities(call: PluginCall) {
        val uuidString = call.getString("uuid")
        if (uuidString == null) {
            call.reject("UUID is required")
            return
        }
        val uuid = UUID.fromString(uuidString)

        CoroutineScope(Dispatchers.Main).launch {
            val bluetoothDevice = withContext(Dispatchers.IO) {
                implementation.searchUnprovisionedBluetoothDevice(uuidString)
            }
            if (bluetoothDevice == null) {
                call.reject("Failed to find unprovisioned device")
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(bluetoothDevice)
            }

            val deferred = implementation.getProvisioningCapabilities(uuid)

            val unprovisionedDevice = deferred.await()
            if (unprovisionedDevice != null) {

                val result = JSObject().apply {
                    put("numberOfElements", unprovisionedDevice.provisioningCapabilities.numberOfElements)
                    val oobTypeArray = JSArray().apply {
                        unprovisionedDevice.provisioningCapabilities.availableOOBTypes.forEach {
                            put(it)
                        }
                    }
                    put("availableOOBTypes", oobTypeArray)
                    put("algorithms", unprovisionedDevice.provisioningCapabilities.rawAlgorithm)
                    put("publicKeyType", unprovisionedDevice.provisioningCapabilities.rawPublicKeyType)
                    put("staticOobTypes", unprovisionedDevice.provisioningCapabilities.rawStaticOOBType)
                    put("outputOobSize", unprovisionedDevice.provisioningCapabilities.outputOOBSize)
                    put("outputOobActions", unprovisionedDevice.provisioningCapabilities.rawOutputOOBAction)
                    put("inputOobSize", unprovisionedDevice.provisioningCapabilities.inputOOBSize)
                    put("inputOobActions", unprovisionedDevice.provisioningCapabilities.rawInputOOBAction)
                }
                call.resolve(result)
            } else {
                call.reject("Failed to get provisioning capabilities")
            }
        }
    }

    @PluginMethod
    fun provisionDevice(call: PluginCall) {
        val uuid = call.getString("uuid")

        if (uuid == null) {
            call.reject("UUID is required")
        }

        CoroutineScope(Dispatchers.IO).launch {
            val bluetoothDevice = withContext(Dispatchers.IO) {
                implementation.searchUnprovisionedBluetoothDevice(uuid!!)
            }
            if (bluetoothDevice == null) {
                call.reject("Failed to find unprovisioned device")
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(bluetoothDevice)
            }

            val deferred = implementation.provisionDevice(UUID.fromString(uuid))

            val meshDevice = deferred.await()
            if (meshDevice == null) {
                call.reject("Failed to provision device")
                return@launch
            }

            when (meshDevice) {
                is BleMeshDevice.Provisioned -> {
                    val result = JSObject().apply {
                        put("provisioningComplete", true)
                        put("uuid", meshDevice.node.uuid)
                        put("unicastAddress", meshDevice.node.unicastAddress)
                    }
                    call.resolve(result)
                }

                is BleMeshDevice.Unprovisioned -> {
                    val result = JSObject().apply {
                        put("provisioningComplete", false)
                        put("uuid", meshDevice.node.deviceUuid)
                    }
                    call.resolve(result)
                }
            }
            implementation.disconnectBle()
        }
    }

    @PluginMethod
    fun unprovisionDevice(call: PluginCall) {
        val unicastAddress = call.getInt("unicastAddress")

        if (unicastAddress == null) {
            call.reject("unicastAddress is required")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val proxy = withContext(Dispatchers.IO) {
                implementation.searchProxyMesh()
            }
            if (proxy == null) {
                call.reject("Failed to find proxy node")
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(proxy)
            }

            PluginCallManager.getInstance()
                .addConfigPluginCall(ConfigMessageOpCodes.CONFIG_NODE_RESET, unicastAddress, call)

            implementation.unprovisionDevice(unicastAddress)
        }
    }

    @PluginMethod
    fun createApplicationKey(call: PluginCall) {
        val result = implementation.createApplicationKey()

        if (result) {
            call.resolve()
        } else {
            call.reject("Failed to add application key")
        }
    }

    @PluginMethod
    fun removeApplicationKey(call: PluginCall) {
        val appKeyIndex = call.getInt("appKeyIndex")

        if (appKeyIndex == null) {
            call.reject("appKeyIndex is required")
        }

        val result = implementation.removeApplicationKey(appKeyIndex!!)

        if (result) {
            call.resolve()
        } else {
            call.reject("Failed to remove application key")
        }
    }

    @PluginMethod
    fun addApplicationKeyToNode(call: PluginCall) {
        val unicastAddress = call.getInt("unicastAddress")
        val appKeyIndex = call.getInt("appKeyIndex")

        if (appKeyIndex == null || unicastAddress == null) {
            call.reject("appKeyIndex and unicastAddress are required")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val proxy = withContext(Dispatchers.IO) {
                implementation.searchProxyMesh()
            }
            if (proxy == null) {
                call.resolve(JSObject().put("success", false))
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(proxy)
            }

//            PluginCallManager.getInstance()
//                .addConfigPluginCall(ConfigMessageOpCodes.CONFIG_APPKEY_ADD, unicastAddress, call)

            val deferred = implementation.addApplicationKeyToNode(unicastAddress, appKeyIndex)
            val result = deferred.await()

            call.resolve(JSObject().put("success", result))
        }
    }

    @PluginMethod
    fun bindApplicationKeyToModel(call: PluginCall) {
        val elementAddress = call.getInt("elementAddress")
        val appKeyIndex = call.getInt("appKeyIndex")
        val modelId = call.getInt("modelId")

        if (elementAddress == null || appKeyIndex == null || modelId == null) {
            call.reject("elementAddress, appKeyIndex and modelId are required")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val proxy = withContext(Dispatchers.IO) {
                implementation.searchProxyMesh()
            }
            if (proxy == null) {
                call.reject("Failed to find proxy node")
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(proxy)
            }

            PluginCallManager.getInstance()
                .addConfigPluginCall(ConfigMessageOpCodes.CONFIG_MODEL_APP_BIND, elementAddress, call)

            val result = implementation.bindApplicationKeyToModel(elementAddress, appKeyIndex, modelId)

            if (!result) {
                call.reject("Failed to bind application key")
            }
        }
    }

    @PluginMethod
    fun compositionDataGet(call: PluginCall) {
        val unicastAddress = call.getInt("unicastAddress")

        if (unicastAddress == null) {
            call.reject("unicastAddress is required")
        }

        CoroutineScope(Dispatchers.Main).launch {
            val proxy = withContext(Dispatchers.IO) {
                implementation.searchProxyMesh()
            }
            if (proxy == null) {
                call.reject("Failed to find proxy node")
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(proxy)
            }

            val deferred = implementation.compositionDataGet(unicastAddress!!)

            val result = deferred.await()
            if (result!!) {
                val meshNetwork = implementation.exportMeshNetwork()
                call.resolve(JSObject().put("meshNetwork", meshNetwork))
            } else {
                call.reject("Failed to get composition data")
            }
        }
    }

    @PluginMethod
    fun sendGenericOnOffSet(call: PluginCall) {
        val unicastAddress = call.getInt("unicastAddress")
        val appKeyIndex = call.getInt("appKeyIndex")
        val onOff = call.getBoolean("onOff")
        val acknowledgement = call.getBoolean("acknowledgement", false)

        if (unicastAddress == null || appKeyIndex == null || onOff == null) {
            call.reject("unicastAddress, appKeyIndex, and onOff are required")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val proxy = withContext(Dispatchers.IO) {
                implementation.searchProxyMesh()
            }
            if (proxy == null) {
                call.reject("Failed to find proxy node")
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(proxy)
            }

            if (acknowledgement == true) {
                PluginCallManager.getInstance()
                    .addSigPluginCall(ApplicationMessageOpCodes.GENERIC_ON_OFF_SET, unicastAddress, call)
            }

            val result = implementation.sendGenericOnOffSet(
                unicastAddress,
                appKeyIndex,
                onOff,
                0
            )

            if (!result) {
                call.reject("Failed to send Generic OnOff Set")
            } else {
                if (acknowledgement == false) {
                    call.resolve()
                }
            }
        }
    }

    @PluginMethod
    fun sendGenericOnOffGet(call: PluginCall) {
        val unicastAddress = call.getInt("unicastAddress")
        val appKeyIndex = call.getInt("appKeyIndex")

        if (unicastAddress == null || appKeyIndex == null) {
            call.reject("unicastAddress and appKeyIndex are required")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val proxy = withContext(Dispatchers.IO) {
                implementation.searchProxyMesh()
            }
            if (proxy == null) {
                call.reject("Failed to find proxy node")
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(proxy)
            }

            PluginCallManager.getInstance()
                .addSigPluginCall(ApplicationMessageOpCodes.GENERIC_ON_OFF_GET, unicastAddress, call)

            val result = implementation.sendGenericOnOffGet(
                unicastAddress,
                appKeyIndex,
            )

            if (!result) {
                call.reject("Failed to send Generic OnOff Get")
            } else {
                call.resolve()
            }
        }
    }

    @PluginMethod
    fun sendGenericPowerLevelSet(call: PluginCall) {
        val unicastAddress = call.getInt("unicastAddress")
        val appKeyIndex = call.getInt("appKeyIndex")
        val powerLevel = call.getInt("powerLevel")
        val acknowledgement = call.getBoolean("acknowledgement", false)

        if (unicastAddress == null || appKeyIndex == null || powerLevel == null) {
            call.reject("unicastAddress, appKeyIndex, and powerLevel are required")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val proxy = withContext(Dispatchers.IO) {
                implementation.searchProxyMesh()
            }
            if (proxy == null) {
                call.reject("Failed to find proxy node")
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(proxy)
            }

            if (acknowledgement == true) {
                PluginCallManager.getInstance()
                    .addSigPluginCall(ApplicationMessageOpCodes.GENERIC_POWER_LEVEL_SET, unicastAddress, call)
            }

            val result = implementation.sendGenericPowerLevelSet(
                unicastAddress,
                appKeyIndex,
                powerLevel,
                0
            )

            if (!result) {
                call.reject("Failed to send Generic Power Level Set")
            } else {
                if (acknowledgement == false) {
                    call.resolve()
                }
            }
        }
    }

    @PluginMethod
    fun sendGenericPowerLevelGet(call: PluginCall) {
        val unicastAddress = call.getInt("unicastAddress")
        val appKeyIndex = call.getInt("appKeyIndex")

        if (unicastAddress == null || appKeyIndex == null) {
            call.reject("unicastAddress and appKeyIndex are required")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val proxy = withContext(Dispatchers.IO) {
                implementation.searchProxyMesh()
            }
            if (proxy == null) {
                call.reject("Failed to find proxy node")
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(proxy)
            }

            PluginCallManager.getInstance()
                .addSigPluginCall(ApplicationMessageOpCodes.GENERIC_POWER_LEVEL_GET, unicastAddress, call)

            val result = implementation.sendGenericPowerLevelGet(
                unicastAddress,
                appKeyIndex,
            )

            if (!result) {
                call.reject("Failed to send Generic Power Level Get")
            } else {
                call.resolve()
            }
        }
    }

    @PluginMethod
    fun sendLightHslSet(call: PluginCall) {
        val unicastAddress = call.getInt("unicastAddress")
        val appKeyIndex = call.getInt("appKeyIndex")
        val hue = call.getInt("hue")
        val saturation = call.getInt("saturation")
        val lightness = call.getInt("lightness")
        val acknowledgement = call.getBoolean("acknowledgement", false)

        if (unicastAddress == null || appKeyIndex == null || hue == null || saturation == null || lightness == null) {
            call.reject("unicastAddress, appKeyIndex, hue, saturation, and lightness are required")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val proxy = withContext(Dispatchers.IO) {
                implementation.searchProxyMesh()
            }
            if (proxy == null) {
                call.reject("Failed to find proxy node")
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(proxy)
            }

            if (acknowledgement == true) {
                PluginCallManager.getInstance()
                    .addSigPluginCall(ApplicationMessageOpCodes.LIGHT_HSL_SET, unicastAddress, call)
            }

            val result = implementation.sendLightHslSet(
                unicastAddress,
                appKeyIndex,
                hue,
                saturation,
                lightness,
                0
            )

            if (!result) {
                call.reject("Failed to send Light HSL Set")
            } else {
                if (acknowledgement == false) {
                    call.resolve()
                }
            }
        }
    }

    @PluginMethod
    fun sendLightHslGet(call: PluginCall) {
        val unicastAddress = call.getInt("unicastAddress")
        val appKeyIndex = call.getInt("appKeyIndex")

        if (unicastAddress == null || appKeyIndex == null) {
            call.reject("unicastAddress and appKeyIndex are required")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val proxy = withContext(Dispatchers.IO) {
                implementation.searchProxyMesh()
            }
            if (proxy == null) {
                call.reject("Failed to find proxy node")
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(proxy)
            }

            PluginCallManager.getInstance()
                .addSigPluginCall(ApplicationMessageOpCodes.LIGHT_HSL_GET, unicastAddress, call)

            val result = implementation.sendLightHslGet(
                unicastAddress,
                appKeyIndex,
            )

            if (!result) {
                call.reject("Failed to send Light HSL Get")
            } else {
                call.resolve()
            }
        }
    }

    @PluginMethod
    fun sendLightCtlSet(call: PluginCall) {
        val unicastAddress = call.getInt("unicastAddress")
        val appKeyIndex = call.getInt("appKeyIndex")
        val lightness = call.getInt("lightness")
        val temperature = call.getInt("temperature")
        val deltaUv = call.getInt("deltaUv")
        val acknowledgement = call.getBoolean("acknowledgement", false)

        if (unicastAddress == null || appKeyIndex == null || lightness == null || temperature == null || deltaUv == null) {
            call.reject("unicastAddress, appKeyIndex, lightness, temperature, and deltaUv are required")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val proxy = withContext(Dispatchers.IO) {
                implementation.searchProxyMesh()
            }
            if (proxy == null) {
                call.reject("Failed to find proxy node")
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(proxy)
            }

            if (acknowledgement == true) {
                PluginCallManager.getInstance()
                    .addSigPluginCall(ApplicationMessageOpCodes.LIGHT_CTL_SET, unicastAddress, call)
            }

            val result = implementation.sendLightCtlSet(
                unicastAddress,
                appKeyIndex,
                lightness,
                temperature,
                deltaUv,
                0
            )

            if (!result) {
                call.reject("Failed to send Light CTL Set")
            } else {
                if (acknowledgement == false) {
                    call.resolve()
                }
            }
        }
    }

    @PluginMethod
    fun sendVendorModelMessage(call: PluginCall) {
        val unicastAddress = call.getInt("unicastAddress")
        val appKeyIndex = call.getInt("appKeyIndex")
        val modelId = call.getInt("modelId")
        val opcode = call.getInt("opcode")
        val parameters = call.getArray("parameters")
        val acknowledgement = call.getBoolean("acknowledgement", false)
        val opPairCode = call.getInt("opPairCode", null)
        val companyIdentifier = modelId?.shr(16)

        if (unicastAddress == null || appKeyIndex == null || modelId == null || companyIdentifier == null || opcode == null || parameters == null) {
            call.reject("unicastAddress, appKeyIndex, modelId, companyIdentifier, opcode, and parameters are required")
            return
        }

        val params = parameters.toList<Int>().map { it.toByte() }.toByteArray()

        CoroutineScope(Dispatchers.Main).launch {
            val proxy = withContext(Dispatchers.IO) {
                implementation.searchProxyMesh()
            }
            if (proxy == null) {
                call.reject("Failed to find proxy node")
                return@launch
            }

            withContext(Dispatchers.IO) {
                implementation.connectBle(proxy)
            }

            if (acknowledgement == true) {
                PluginCallManager.getInstance()
                    .addVendorPluginCall(modelId, opcode, opPairCode!!, unicastAddress, call)
            }

            val result = implementation.sendVendorModelMessage(
                unicastAddress,
                appKeyIndex,
                modelId,
                companyIdentifier,
                opcode,
                params,
            )

            if (!result) {
                call.reject("Failed to send Vendor Model Message")
            } else {
                if (acknowledgement == false) {
                    call.resolve()
                }
            }
        }
    }

    @PluginMethod
    fun exportMeshNetwork(call: PluginCall) {
        val result = implementation.exportMeshNetwork()

        if (result != null) {
            call.resolve(JSObject().put("meshNetwork", result))
        } else {
            call.reject("Failed to export mesh network")
        }
    }

    @PluginMethod
    fun importMeshNetwork(call: PluginCall) {
        val meshNetwork = call.getString("meshNetwork")

        if (meshNetwork == null) {
            call.reject("meshNetwork is required")
            return
        }

        val result = implementation.importMeshNetwork(meshNetwork)

        call.resolve()
    }

    @PluginMethod
    fun initMeshNetwork(call: PluginCall) {
        val networkName = call.getString("networkName")

        if (networkName == null) {
            call.reject("networkName is required")
            return
        }

        implementation.initMeshNetwork(networkName)

        val network = implementation.exportMeshNetwork()

        if (network != null) {
            call.resolve(JSObject().put("meshNetwork", network))
        } else {
            call.reject("Failed to initialize mesh network")
        }
    }
}
