package expo.modules.thermalprinter

import com.printsdk.PrintSerializable;
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.records.Field
import expo.modules.kotlin.records.Record
import android.app.PendingIntent
import android.content.Context
import android.hardware.usb.UsbManager
import android.content.Intent
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.google.zxing.BarcodeFormat

class ExpoThermalPrinterModule : Module() {

  private var state : PrintState = PrintState.IDLE;
  private val printer : PrintSerializable = PrintSerializable()

  private val context
  get() = requireNotNull(appContext.reactContext)

  private val usbManager
  get() = context.getSystemService(Context.USB_SERVICE) as UsbManager

  override fun definition() = ModuleDefinition {
    Name("ExpoThermalPrinter")

    Events("onChange")

    AsyncFunction("print") { value: PrintParams ->
      // Send an event to JavaScript.
      sendEvent("onChange", mapOf(
        "value" to value
      ))
    }

    Function("getUsbHostIds") {
      return@Function usbManager.deviceList.map { it.key }
    }

    Function("hasUSBPermission") { usbId: String ->
      val usbDevice = usbManager.deviceList[usbId]
      return@Function usbManager.hasPermission(usbDevice)
    }

    Function("requestUSBPermission") { usbId: String ->
      val pendingIntent = PendingIntent.getBroadcast(
          context,
          0,
          Intent("com.android.usb.USB_PERMISSION"),
          0
      )
      val usbDevice = usbManager.deviceList[usbId]
      usbManager.requestPermission(usbDevice, pendingIntent)
    }

    Function("connectPrinter") { usbId: String ->
      val usbDevice = usbManager.deviceList[usbId]
      if(usbDevice != null){
        printer.open(usbManager, usbDevice)
      }
      return@Function printer.state
    }

    Function("closePrinter") {
      printer.close()
    }

    Function("printWithContent") { printContent: PrintContent ->
      val barcodeEncoder = BarcodeEncoder()
      printer.init()
      printer.setAlign(PrintSerializable.ALIGN_CENTER.toInt())
      printer.printText(printContent.title)
      printer.wrapLines(1)

      printer.setAlign(PrintSerializable.ALIGN_CENTER.toInt())
      printer.printTwoBarCode(PrintSerializable.TWO_QRCODE,10, 0, 0x48, printContent.qrContent);
      printer.wrapLines(1)

      printer.setAlign(PrintSerializable.ALIGN_CENTER.toInt())
      printer.printText("Date : " + printContent.date)
      printer.wrapLines(1)

      printer.setAlign(PrintSerializable.ALIGN_CENTER.toInt())
      printer.printText("Valid until : " + printContent.validUntil)
      printer.wrapLines(1)

      printer.Beeper(10.toByte())
      printer.Cutter()
      printer.OpenDrawer(true, true)
    }

  }
}

internal data class PrintParams(
  @Field var serial: String? = null,
  @Field var port: String? = null,
) : Record

internal data class PrintContent( 
  @Field var title: String? = null,
  @Field var qrContent: String? = null,
  @Field var date: String? = null,
  @Field var validUntil: String? = null,
)

private sealed class PrintState{
  object IDLE : PrintState()
  object PRINTING : PrintState()
  object ERROR : PrintState()
}
