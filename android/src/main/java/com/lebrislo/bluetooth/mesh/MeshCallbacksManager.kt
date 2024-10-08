package com.lebrislo.bluetooth.mesh

import android.util.Log
import com.lebrislo.bluetooth.mesh.ble.BleMeshManager
import no.nordicsemi.android.mesh.MeshManagerCallbacks
import no.nordicsemi.android.mesh.MeshNetwork
import no.nordicsemi.android.mesh.provisionerstates.UnprovisionedMeshNode

class MeshCallbacksManager(val bleMeshManager: BleMeshManager) : MeshManagerCallbacks {
    private val tag: String = MeshCallbacksManager::class.java.simpleName

    private var meshNetwork: MeshNetwork? = null

    override fun onNetworkLoaded(meshNetwork: MeshNetwork?) {
        Log.d(tag, "onNetworkLoaded")

        if (meshNetwork == null) {
            return
        }

        Log.d(tag, "Mesh network UUID Loaded : " + meshNetwork.meshUUID)

        this.meshNetwork = meshNetwork

        if (!this.meshNetwork!!.isProvisionerSelected()) {
            val provisioner = this.meshNetwork!!.provisioners[0]
            this.meshNetwork!!.selectProvisioner(provisioner)
        }
    }

    override fun onNetworkUpdated(meshNetwork: MeshNetwork?) {
        Log.d(tag, "onNetworkUpdated")
    }

    override fun onNetworkLoadFailed(error: String?) {
        Log.d(tag, "onNetworkLoadFailed")
    }

    override fun onNetworkImported(meshNetwork: MeshNetwork?) {
        Log.d(tag, "onNetworkImported")
    }

    override fun onNetworkImportFailed(error: String?) {
        Log.d(tag, "onNetworkImportFailed")
    }

    override fun sendProvisioningPdu(meshNode: UnprovisionedMeshNode?, pdu: ByteArray?) {
        Log.d(tag, "sendProvisioningPdu")
        bleMeshManager.sendPdu(pdu)
    }

    override fun onMeshPduCreated(pdu: ByteArray?) {
        Log.d(tag, "onMeshPduCreated")
        bleMeshManager.sendPdu(pdu)
    }

    override fun getMtu(): Int {
        Log.d(tag, "getMtu")
        return bleMeshManager.maximumPacketSize
    }
}