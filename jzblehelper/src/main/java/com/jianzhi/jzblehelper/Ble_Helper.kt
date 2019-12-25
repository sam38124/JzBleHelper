package com.jianzhi.jzblehelper

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import com.jianzhi.jzblehelper.Beans.BleStream
import com.jianzhi.jzblehelper.Server.BleServiceControl
import com.jianzhi.jzblehelper.Server.ScanDevice
import java.nio.charset.Charset

class Ble_Helper(val caller: Ble_CallBack, val context: Context) {
    var RXchannel = ""
    var TXchannel = ""
    var handler = Handler()
    var bleServiceControl = BleServiceControl()
    var scan = ScanDevice(this, context)
    fun SetChannel(rx: String, tx: String) {
        RXchannel = rx
        TXchannel = tx
    }

    fun Connect(address: String,time: Int) {
        scan.scanLeDevice(false)
        caller.Connecting()
        bleServiceControl.bleCallbackC = this
        bleServiceControl.connect(address)
        Thread {
            var fal = 0
            while (true) {
                if (bleServiceControl.isconnect || fal == time) {
                    break
                }
                Thread.sleep(1000)
                fal++
            }
            handler.post {
                if (!bleServiceControl.isconnect) {
                    caller.ConnectFalse()
                }
            }
            StopScan()
        }.start()
    }

    fun ReadCharacteristics(uuid: String) {
        bleServiceControl.ReadCmd(uuid)
    }

    fun WriteHex(a: String,rx: String, tx: String) {
        SetChannel(rx,tx)
        bleServiceControl.WriteCmd(a, 0)
    }

    fun WriteUtf(a: String,rx: String, tx: String) {
        SetChannel(rx,tx)
        bleServiceControl.WriteCmd(a.toByteArray(Charset.defaultCharset()), 0)
    }

    fun WriteBytes(a: ByteArray,rx: String, tx: String) {
        SetChannel(rx,tx)
        bleServiceControl.WriteCmd(a, 0)
    }

    fun StartScan() {
        scan.setmBluetoothAdapter()
    }

    fun StopScan() {
        scan.scanLeDevice(false)
    }

    fun Disconnect() {
        bleServiceControl.disconnect()
    }

    interface Ble_CallBack {
        fun Connecting()
        fun ConnectFalse()
        fun ConnectSuccess()
        fun RX(a: BleStream)
        fun TX(b: BleStream)
        fun ScanBack(device: BluetoothDevice)
        fun RequestPermission(permission: ArrayList<String>)
        fun NeedGps()
    }
}


