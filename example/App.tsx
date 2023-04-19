import { Button, Modal, StyleSheet, Text, View } from 'react-native';

import * as ExpoThermalPrinter from 'expo-thermal-printer';
import { useState } from 'react';

export default function App() {

  const [showUSBList, setShowUSBList] = useState(false);
  const [usbList, setUSBList] = useState<string[]>([]);
  const [selectedUSB, setSelectedUSB] = useState<string>();
  const [log, setLog] = useState<string>("");

  const onChooseUSB = () => {
    const useHostIds = ExpoThermalPrinter.getUsbHostIds();
    setUSBList(useHostIds)
    setShowUSBList(true)
    appendLog("USB LIST REQUESTED")
  }

  const onUSBSelected = (usb: string) => {
    setSelectedUSB(usb)
    appendLog("USB SELECTED : " + usb)
  }

  const onCheckPermission = () => {
    if (!selectedUSB) {
      return;
    }
    let hasPermission = ExpoThermalPrinter.hasUSBPermission(selectedUSB)

    appendLog("USB : " + selectedUSB + " hasPermission : " + hasPermission)
  }

  const onRequestPermission = () => {
    if (!selectedUSB) {
      return;
    }
    ExpoThermalPrinter.requestUSBPermission(selectedUSB)
    appendLog("USB : " + selectedUSB + " permission requested ")
  }

  const onConnectPrinter = () => {
    if (!selectedUSB) {
      return;
    }
    const result = ExpoThermalPrinter.connectPrinter(selectedUSB)

    appendLog("CONNECT PRINTER : " + result)
  }

  const onClosePrinter = () => {
    if (!selectedUSB) {
      return;
    }
    ExpoThermalPrinter.closePrinter()
  }

  const onTestPrinter = () => {
    if (!selectedUSB) {
      return;
    }
    ExpoThermalPrinter.printWithContent()
    appendLog("TEST PRINT ")
  }

  const appendLog = (newLog: string) => {
    setLog(log + newLog + "\n")
  }

  return (
    <>
      <View style={styles.container}>
        <View style={{ flexBasis: "30%", flexDirection: "row", flexWrap: "wrap", gap: 24 }}>
          <Button onPress={onChooseUSB} title='Choose USB' />
          <Button onPress={onCheckPermission} title='Check Permission' />
          <Button onPress={onRequestPermission} title='Request Permission' />
          <Button onPress={onConnectPrinter} title='Connect Printer' />
          <Button onPress={onClosePrinter} title='Close Printer' />
          <Button onPress={onTestPrinter} title='Test Print' />
        </View>
        <Text style={{ flex: 1, justifyContent: "flex-start", color: "#666", width: "100%" }}>{log || ""}</Text>
      </View>
      <Modal visible={showUSBList}>
        <View style={{ flex: 1, alignContent: "center", alignItems: "center", justifyContent: "center" }}>
          {usbList.map((device) => <Button onPress={() => { onUSBSelected(device) }} title={device} />)}
          <Button onPress={() => { setShowUSBList(false) }} title={"Close"} />
        </View>
      </Modal>
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
