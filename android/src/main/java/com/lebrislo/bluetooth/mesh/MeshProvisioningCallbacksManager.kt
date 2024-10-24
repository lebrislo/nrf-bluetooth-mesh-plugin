package com.lebrislo.bluetooth.mesh

import android.util.Log
import com.lebrislo.bluetooth.mesh.models.BleMeshDevice
import no.nordicsemi.android.mesh.MeshProvisioningStatusCallbacks
import no.nordicsemi.android.mesh.provisionerstates.ProvisioningState
import no.nordicsemi.android.mesh.provisionerstates.UnprovisionedMeshNode
import no.nordicsemi.android.mesh.transport.ProvisionedMeshNode

class MeshProvisioningCallbacksManager(
    var unprovisionedMeshNodes: ArrayList<UnprovisionedMeshNode>,
    var nrfMeshManager: NrfMeshManager
) :
    MeshProvisioningStatusCallbacks {
    private val tag: String = MeshProvisioningCallbacksManager::class.java.simpleName

    override fun onProvisioningStateChanged(
        meshNode: UnprovisionedMeshNode?,
        state: ProvisioningState.States?,
        data: ByteArray?
    ) {
        Log.d(tag, "onProvisioningStateChanged : ${meshNode?.deviceUuid}  ${state?.name}")
        if (state == ProvisioningState.States.PROVISIONING_CAPABILITIES) {
            unprovisionedMeshNodes.add(meshNode!!)
            nrfMeshManager.onProvisioningCapabilitiesReceived(meshNode)
        }
    }

    override fun onProvisioningFailed(
        meshNode: UnprovisionedMeshNode?,
        state: ProvisioningState.States?,
        data: ByteArray?
    ) {
        Log.d(tag, "onProvisioningFailed : " + meshNode?.deviceUuid)
        if (state == ProvisioningState.States.PROVISIONING_FAILED) {
            nrfMeshManager.onProvisioningFinish(BleMeshDevice.Unprovisioned(meshNode!!))
        }
    }

    override fun onProvisioningCompleted(
        meshNode: ProvisionedMeshNode?,
        state: ProvisioningState.States?,
        data: ByteArray?
    ) {
        Log.d(tag, "onProvisioningCompleted : " + meshNode?.uuid)
        if (state == ProvisioningState.States.PROVISIONING_COMPLETE) {
            nrfMeshManager.onProvisioningFinish(BleMeshDevice.Provisioned(meshNode!!))
        }
    }
}