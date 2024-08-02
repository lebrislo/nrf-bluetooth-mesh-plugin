interface BleMeshDevice {
  name: string;
  uuid: string;
  rssi: number;
  macAddress: string;
}

export interface ScanDevicesResponse {
  devices?: BleMeshDevice[];
}

export interface ProvisioningCapabilities {
  numberOfElements: number;
  availableOOBTypes: string[];
  algorithms: number;
  publicKeyType: number;
  staticOobTypes: number;
  outputOobSize: number;
  outputOobActions: number;
  inputOobSize: number;
  inputOobActions: number;
}

export interface ProvisioningStatus {
  provisioningComplete: boolean;
  uuid: string;
  unicastAddress?: number;
}

export interface PluginCallRejection {
  message: string;
  data: {
    methodName: string;
    [key: string]: any;
  };
}

export interface NrfMeshPlugin {
  scanUnprovisionedDevices(options: {
    timeout: number;
  }): Promise<ScanDevicesResponse>;
  scanProvisionedDevices(options: {
    timeout: number;
  }): Promise<ScanDevicesResponse>;
  getProvisioningCapabilities(options: {
    uuid: string;
  }): Promise<ProvisioningCapabilities | void>;
  provisionDevice(options: { uuid: string }): Promise<ProvisioningStatus>;
  unprovisionDevice(options: { unicastAddress: number }): Promise<void>;
  createApplicationKey(): Promise<void>;
  removeApplicationKey(options: { appKeyIndex: number }): Promise<void>;
  addApplicationKeyToNode(options: {
    unicastAddress: number;
    appKeyIndex: number;
  }): Promise<void>;
  bindApplicationKeyToModel(options: {
    elementAddress: number;
    appKeyIndex: number;
    modelId: number;
  }): Promise<void>;
  compositionDataGet(options: { unicastAddress: number }): Promise<void>;
  sendGenericOnOffSet(options: {
    unicastAddress: number;
    appKeyIndex: number;
    onOff: boolean;
  }): Promise<void | PluginCallRejection>;
  sendGenericPowerLevelSet(options: {
    unicastAddress: number;
    appKeyIndex: number;
    powerLevel: number;
  }): Promise<void | PluginCallRejection>;
  sendLightHslSet(options: {
    unicastAddress: number;
    appKeyIndex: number;
    hue: number;
    saturation: number;
    lightness: number;
  }): Promise<void | PluginCallRejection>;
  exportMeshNetwork(): Promise<object>;
}
