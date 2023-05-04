import { EventEmitter, NativeModulesProxy, Subscription } from 'expo-modules-core';

// Import the native module. On web, it will be resolved to ExpoThermalPrinter.web.ts
// and on native platforms to ExpoThermalPrinter.ts
import { ChangeEventPayload, PrintParams, PrintContent } from './ExpoThermalPrinter.types';
import ExpoThermalPrinterModule from './ExpoThermalPrinterModule';

// Get the native constant value.
export async function print(value: PrintParams) {
  return await ExpoThermalPrinterModule.print(value);
}

export function getUsbHosts() {
  return ExpoThermalPrinterModule.getUsbHosts();
}

export function getUsbHostIds(): string[] {
  return ExpoThermalPrinterModule.getUsbHostIds();
}

export function hasUSBPermission(usbId: string): boolean {
  return ExpoThermalPrinterModule.hasUSBPermission(usbId);
}

export function requestUSBPermission(usbId: string): boolean {
  return ExpoThermalPrinterModule.requestUSBPermission(usbId);
}

export function connectPrinter(usbId: string): number {
  return ExpoThermalPrinterModule.connectPrinter(usbId);
}

export function closePrinter(): boolean {
  return ExpoThermalPrinterModule.closePrinter();
}

export function printWithContent(content: PrintContent) {
  return ExpoThermalPrinterModule.printWithContent(content);
}


export function printQRCode(qrCode: string) {
  return ExpoThermalPrinterModule.printQRCode(qrCode);
}

const emitter = new EventEmitter(ExpoThermalPrinterModule ?? NativeModulesProxy.ExpoThermalPrinter);

export function addChangeListener(listener: (event: ChangeEventPayload) => void): Subscription {
  return emitter.addListener<ChangeEventPayload>('onChange', listener);
}

export { ChangeEventPayload };
