package com.jianzhi.jzblehelper

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import com.jianzhi.jzblehelper.Beans.BleStream
import com.jianzhi.jzblehelper.Callback.Ble_CallBack
import com.jianzhi.jzblehelper.Server.BleServiceControl
import com.jianzhi.jzblehelper.Server.ScanDevice
import java.nio.charset.Charset

class Ble_Helper(val caller: Ble_CallBack, val context: Context) {
    var RXchannel = ""
    var TXchannel = ""
    var handler = Handler()
    var bleServiceControl = BleServiceControl()
    var scan = ScanDevice(this, context)
    fun setChannel(rx: String, tx: String) {
        RXchannel = rx
        TXchannel = tx
    }

    fun connect(address: String,time: Int) {
        scan.scanLeDevice(false)
        caller.connecting()
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
                    caller.connectFalse()
                }
            }
            stopScan()
        }.start()
    }

    fun readCharacteristics(uuid: String) {
        bleServiceControl.ReadCmd(uuid)
    }

    fun writeHex(a: String,rx: String, tx: String) {
        setChannel(rx,tx)
        bleServiceControl.WriteCmd(a, 0)
    }

    fun writeUtf(a: String,rx: String, tx: String) {
        setChannel(rx,tx)
        bleServiceControl.WriteCmd(a.toByteArray(Charset.defaultCharset()), 0)
    }

    fun writeBytes(a: ByteArray,rx: String, tx: String) {
        setChannel(rx,tx)
        bleServiceControl.WriteCmd(a, 0)
    }

    fun startScan() {
        scan.setmBluetoothAdapter()
    }

    fun stopScan() {
        scan.scanLeDevice(false)
    }

    fun disconnect() {
        bleServiceControl.disconnect()
    }


}


