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

    Function("printReceiptContent") { printContent: ReceiptContent ->
        printer.init()
        
        // Print Date/Time
        printer.setAlign(PrintSerializable.ALIGN_LEFT.toInt())
        printer.printText(printContent.dateTime)
        printer.wrapLines(1)

        // Print Shop no., Device no., and Receipt no.
        printer.setAlign(PrintSerializable.ALIGN_LEFT.toInt())
        printer.printText(printContent.shopNo)
        printer.printText(printContent.deviceNo)
        printer.printText(printContent.receiptNo)
        printer.wrapLines(1)

        // Print Product details
        printer.setAlign(PrintSerializable.ALIGN_LEFT.toInt())
        printer.printText(printContent.itemPrice)
        printer.printText(printContent.total)
        printer.wrapLines(1)

        // Print Octopus payment details
        printer.setAlign(PrintSerializable.ALIGN_LEFT.toInt())
        printer.printText(printContent.octopusPayment)
        printer.printText(printContent.octopusNo)
        printer.printText(printContent.amountDeducted)
        printer.printText(printContent.remainingValue)
        printer.wrapLines(1)

        // Print Last add value by Cash
        printer.setAlign(PrintSerializable.ALIGN_LEFT.toInt())
        printer.printText(printContent.lastAddValueDate)
        printer.wrapLines(1)

        // Finish up
        printer.Beeper(10.toByte())
        printer.Cutter()
        printer.OpenDrawer(true, true)
    }

    Function("printQRCode") { qrCode: String ->
      printer.init()
      printer.wrapLines(1)

      printer.setAlign(PrintSerializable.ALIGN_CENTER.toInt())
      printer.printTwoBarCode(PrintSerializable.TWO_QRCODE,10, 0, 0x48, qrCode);
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
) : Record

internal data class ReceiptContent(
  @Field var dateTime: String? = null,
  @Field var shopNo: String? = null,
  @Field var deviceNo: String? = null,
  @Field var receiptNo: String? = null,
  @Field var itemPrice: String? = null,
  @Field var total: String? = null,
  @Field var octopusPayment: String? = null,
  @Field var octopusNo: String? = null,
  @Field var amountDeducted: String? = null,
  @Field var remainingValue: String? = null,
  @Field var lastAddValueDate: String? = null
) : Record

private sealed class PrintState{
  object IDLE : PrintState()
  object PRINTING : PrintState()
  object ERROR : PrintState()
}
