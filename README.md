# nrf-bluetooth-mesh

Capacitor plugin for Bluetooth Mesh, based on nRF Mesh Libraries

## Install

```bash
npm install nrf-bluetooth-mesh
npx cap sync
```

## API

<docgen-index>

* [`scanUnprovisionedDevices(...)`](#scanunprovisioneddevices)
* [`scanProvisionedDevices(...)`](#scanprovisioneddevices)
* [`scanMeshDevices(...)`](#scanmeshdevices)
* [`getProvisioningCapabilities(...)`](#getprovisioningcapabilities)
* [`provisionDevice(...)`](#provisiondevice)
* [`unprovisionDevice(...)`](#unprovisiondevice)
* [`createApplicationKey()`](#createapplicationkey)
* [`removeApplicationKey(...)`](#removeapplicationkey)
* [`addApplicationKeyToNode(...)`](#addapplicationkeytonode)
* [`bindApplicationKeyToModel(...)`](#bindapplicationkeytomodel)
* [`compositionDataGet(...)`](#compositiondataget)
* [`sendGenericOnOffSet(...)`](#sendgenericonoffset)
* [`sendGenericOnOffGet(...)`](#sendgenericonoffget)
* [`sendGenericPowerLevelSet(...)`](#sendgenericpowerlevelset)
* [`sendGenericPowerLevelGet(...)`](#sendgenericpowerlevelget)
* [`sendLightHslSet(...)`](#sendlighthslset)
* [`sendLightHslGet(...)`](#sendlighthslget)
* [`sendLightCtlSet(...)`](#sendlightctlset)
* [`sendVendorModelMessage(...)`](#sendvendormodelmessage)
* [`initMeshNetwork(...)`](#initmeshnetwork)
* [`exportMeshNetwork()`](#exportmeshnetwork)
* [`importMeshNetwork(...)`](#importmeshnetwork)
* [`addListener(string, ...)`](#addlistenerstring-)
* [`removeAllListeners()`](#removealllisteners)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### scanUnprovisionedDevices(...)

```typescript
scanUnprovisionedDevices(options: { timeout: number; }) => Promise<ScanDevicesResponse>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ timeout: number; }</code> |

**Returns:** <code>Promise&lt;<a href="#scandevicesresponse">ScanDevicesResponse</a>&gt;</code>

--------------------


### scanProvisionedDevices(...)

```typescript
scanProvisionedDevices(options: { timeout: number; }) => Promise<ScanDevicesResponse>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ timeout: number; }</code> |

**Returns:** <code>Promise&lt;<a href="#scandevicesresponse">ScanDevicesResponse</a>&gt;</code>

--------------------


### scanMeshDevices(...)

```typescript
scanMeshDevices(options: { timeout: number; }) => Promise<ScanMeshDevices>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ timeout: number; }</code> |

**Returns:** <code>Promise&lt;<a href="#scanmeshdevices">ScanMeshDevices</a>&gt;</code>

--------------------


### getProvisioningCapabilities(...)

```typescript
getProvisioningCapabilities(options: { uuid: string; }) => Promise<ProvisioningCapabilities | void>
```

| Param         | Type                           |
| ------------- | ------------------------------ |
| **`options`** | <code>{ uuid: string; }</code> |

**Returns:** <code>Promise&lt;void | <a href="#provisioningcapabilities">ProvisioningCapabilities</a>&gt;</code>

--------------------


### provisionDevice(...)

```typescript
provisionDevice(options: { uuid: string; }) => Promise<ProvisioningStatus>
```

| Param         | Type                           |
| ------------- | ------------------------------ |
| **`options`** | <code>{ uuid: string; }</code> |

**Returns:** <code>Promise&lt;<a href="#provisioningstatus">ProvisioningStatus</a>&gt;</code>

--------------------


### unprovisionDevice(...)

```typescript
unprovisionDevice(options: { unicastAddress: number; }) => Promise<void>
```

| Param         | Type                                     |
| ------------- | ---------------------------------------- |
| **`options`** | <code>{ unicastAddress: number; }</code> |

--------------------


### createApplicationKey()

```typescript
createApplicationKey() => Promise<void>
```

--------------------


### removeApplicationKey(...)

```typescript
removeApplicationKey(options: { appKeyIndex: number; }) => Promise<void>
```

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ appKeyIndex: number; }</code> |

--------------------


### addApplicationKeyToNode(...)

```typescript
addApplicationKeyToNode(options: { unicastAddress: number; appKeyIndex: number; }) => Promise<AddAppKeyStatus>
```

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code>{ unicastAddress: number; appKeyIndex: number; }</code> |

**Returns:** <code>Promise&lt;<a href="#addappkeystatus">AddAppKeyStatus</a>&gt;</code>

--------------------


### bindApplicationKeyToModel(...)

```typescript
bindApplicationKeyToModel(options: { elementAddress: number; appKeyIndex: number; modelId: number; }) => Promise<void>
```

| Param         | Type                                                                           |
| ------------- | ------------------------------------------------------------------------------ |
| **`options`** | <code>{ elementAddress: number; appKeyIndex: number; modelId: number; }</code> |

--------------------


### compositionDataGet(...)

```typescript
compositionDataGet(options: { unicastAddress: number; }) => Promise<void>
```

| Param         | Type                                     |
| ------------- | ---------------------------------------- |
| **`options`** | <code>{ unicastAddress: number; }</code> |

--------------------


### sendGenericOnOffSet(...)

```typescript
sendGenericOnOffSet(options: { unicastAddress: number; appKeyIndex: number; onOff: boolean; }) => Promise<ModelMessageStatus | PluginCallRejection>
```

| Param         | Type                                                                          |
| ------------- | ----------------------------------------------------------------------------- |
| **`options`** | <code>{ unicastAddress: number; appKeyIndex: number; onOff: boolean; }</code> |

**Returns:** <code>Promise&lt;<a href="#modelmessagestatus">ModelMessageStatus</a> | <a href="#plugincallrejection">PluginCallRejection</a>&gt;</code>

--------------------


### sendGenericOnOffGet(...)

```typescript
sendGenericOnOffGet(options: { unicastAddress: number; appKeyIndex: number; }) => Promise<ModelMessageStatus | PluginCallRejection>
```

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code>{ unicastAddress: number; appKeyIndex: number; }</code> |

**Returns:** <code>Promise&lt;<a href="#modelmessagestatus">ModelMessageStatus</a> | <a href="#plugincallrejection">PluginCallRejection</a>&gt;</code>

--------------------


### sendGenericPowerLevelSet(...)

```typescript
sendGenericPowerLevelSet(options: { unicastAddress: number; appKeyIndex: number; powerLevel: number; }) => Promise<ModelMessageStatus | PluginCallRejection>
```

| Param         | Type                                                                              |
| ------------- | --------------------------------------------------------------------------------- |
| **`options`** | <code>{ unicastAddress: number; appKeyIndex: number; powerLevel: number; }</code> |

**Returns:** <code>Promise&lt;<a href="#modelmessagestatus">ModelMessageStatus</a> | <a href="#plugincallrejection">PluginCallRejection</a>&gt;</code>

--------------------


### sendGenericPowerLevelGet(...)

```typescript
sendGenericPowerLevelGet(options: { unicastAddress: number; appKeyIndex: number; }) => Promise<ModelMessageStatus | PluginCallRejection>
```

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code>{ unicastAddress: number; appKeyIndex: number; }</code> |

**Returns:** <code>Promise&lt;<a href="#modelmessagestatus">ModelMessageStatus</a> | <a href="#plugincallrejection">PluginCallRejection</a>&gt;</code>

--------------------


### sendLightHslSet(...)

```typescript
sendLightHslSet(options: { unicastAddress: number; appKeyIndex: number; hue: number; saturation: number; lightness: number; }) => Promise<ModelMessageStatus | PluginCallRejection>
```

| Param         | Type                                                                                                              |
| ------------- | ----------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ unicastAddress: number; appKeyIndex: number; hue: number; saturation: number; lightness: number; }</code> |

**Returns:** <code>Promise&lt;<a href="#modelmessagestatus">ModelMessageStatus</a> | <a href="#plugincallrejection">PluginCallRejection</a>&gt;</code>

--------------------


### sendLightHslGet(...)

```typescript
sendLightHslGet(options: { unicastAddress: number; appKeyIndex: number; }) => Promise<ModelMessageStatus | PluginCallRejection>
```

| Param         | Type                                                          |
| ------------- | ------------------------------------------------------------- |
| **`options`** | <code>{ unicastAddress: number; appKeyIndex: number; }</code> |

**Returns:** <code>Promise&lt;<a href="#modelmessagestatus">ModelMessageStatus</a> | <a href="#plugincallrejection">PluginCallRejection</a>&gt;</code>

--------------------


### sendLightCtlSet(...)

```typescript
sendLightCtlSet(options: { unicastAddress: number; appKeyIndex: number; lightness: number; temperature: number; deltaUv: number; }) => Promise<ModelMessageStatus | PluginCallRejection>
```

| Param         | Type                                                                                                                   |
| ------------- | ---------------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ unicastAddress: number; appKeyIndex: number; lightness: number; temperature: number; deltaUv: number; }</code> |

**Returns:** <code>Promise&lt;<a href="#modelmessagestatus">ModelMessageStatus</a> | <a href="#plugincallrejection">PluginCallRejection</a>&gt;</code>

--------------------


### sendVendorModelMessage(...)

```typescript
sendVendorModelMessage(options: { unicastAddress: number; appKeyIndex: number; modelId: number; companyIdentifier: number; opcode: number; parameters: number[]; }) => Promise<ModelMessageStatus | PluginCallRejection>
```

| Param         | Type                                                                                                                                            |
| ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ unicastAddress: number; appKeyIndex: number; modelId: number; companyIdentifier: number; opcode: number; parameters: number[]; }</code> |

**Returns:** <code>Promise&lt;<a href="#modelmessagestatus">ModelMessageStatus</a> | <a href="#plugincallrejection">PluginCallRejection</a>&gt;</code>

--------------------


### initMeshNetwork(...)

```typescript
initMeshNetwork(options: { networkName: string; }) => Promise<MeshNetworkObject>
```

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ networkName: string; }</code> |

**Returns:** <code>Promise&lt;<a href="#meshnetworkobject">MeshNetworkObject</a>&gt;</code>

--------------------


### exportMeshNetwork()

```typescript
exportMeshNetwork() => Promise<MeshNetworkObject>
```

**Returns:** <code>Promise&lt;<a href="#meshnetworkobject">MeshNetworkObject</a>&gt;</code>

--------------------


### importMeshNetwork(...)

```typescript
importMeshNetwork(options: { meshNetwork: string; }) => Promise<void>
```

| Param         | Type                                  |
| ------------- | ------------------------------------- |
| **`options`** | <code>{ meshNetwork: string; }</code> |

--------------------


### addListener(string, ...)

```typescript
addListener(eventName: string, listenerFunc: (event: ModelMessageStatus) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                                                  |
| ------------------ | ------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>string</code>                                                                   |
| **`listenerFunc`** | <code>(event: <a href="#modelmessagestatus">ModelMessageStatus</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

--------------------


### Interfaces


#### ScanDevicesResponse

| Prop          | Type                         |
| ------------- | ---------------------------- |
| **`devices`** | <code>BleMeshDevice[]</code> |


#### BleMeshDevice

| Prop             | Type                |
| ---------------- | ------------------- |
| **`name`**       | <code>string</code> |
| **`uuid`**       | <code>string</code> |
| **`rssi`**       | <code>number</code> |
| **`macAddress`** | <code>string</code> |


#### ScanMeshDevices

| Prop                | Type                         |
| ------------------- | ---------------------------- |
| **`unprovisioned`** | <code>BleMeshDevice[]</code> |
| **`provisioned`**   | <code>BleMeshDevice[]</code> |


#### ProvisioningCapabilities

| Prop                    | Type                  |
| ----------------------- | --------------------- |
| **`numberOfElements`**  | <code>number</code>   |
| **`availableOOBTypes`** | <code>string[]</code> |
| **`algorithms`**        | <code>number</code>   |
| **`publicKeyType`**     | <code>number</code>   |
| **`staticOobTypes`**    | <code>number</code>   |
| **`outputOobSize`**     | <code>number</code>   |
| **`outputOobActions`**  | <code>number</code>   |
| **`inputOobSize`**      | <code>number</code>   |
| **`inputOobActions`**   | <code>number</code>   |


#### ProvisioningStatus

| Prop                       | Type                 |
| -------------------------- | -------------------- |
| **`provisioningComplete`** | <code>boolean</code> |
| **`uuid`**                 | <code>string</code>  |
| **`unicastAddress`**       | <code>number</code>  |


#### AddAppKeyStatus

| Prop          | Type                 |
| ------------- | -------------------- |
| **`success`** | <code>boolean</code> |


#### ModelMessageStatus

| Prop                | Type                |
| ------------------- | ------------------- |
| **`src`**           | <code>number</code> |
| **`dst`**           | <code>number</code> |
| **`opcode`**        | <code>number</code> |
| **`vendorModelId`** | <code>number</code> |
| **`data`**          | <code>any</code>    |


#### PluginCallRejection

| Prop          | Type                                                     |
| ------------- | -------------------------------------------------------- |
| **`message`** | <code>string</code>                                      |
| **`data`**    | <code>{ [key: string]: any; methodName: string; }</code> |


#### MeshNetworkObject

| Prop              | Type                |
| ----------------- | ------------------- |
| **`meshNetwork`** | <code>string</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |

</docgen-api>
