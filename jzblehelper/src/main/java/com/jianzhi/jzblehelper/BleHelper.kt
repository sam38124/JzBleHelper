package com.jianzhi.jzblehelper

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Handler
import android.provider.CallLog
import com.jianzhi.jzblehelper.callback.BleCallBack
import com.jianzhi.jzblehelper.callback.ConnectResult
import com.jianzhi.jzblehelper.server.BleServiceControl
import com.jianzhi.jzblehelper.server.ScanDevice
import java.nio.charset.Charset

class BleHelper(val context: Context, val callback: BleCallBack) {
    companion object{
        //判斷是否正在掃描
        var isScanning=false
    }
    var RXchannel = ""
    var TXchannel = ""
    var RxData = ""
    var handler = Handler()
    var bleadapter = BluetoothAdapter.getDefaultAdapter()
    var bleServiceControl = BleServiceControl()
    var scan = ScanDevice(context, this)
    fun setChannel(rx: String, tx: String) {
        RXchannel = rx
        TXchannel = tx
    }

    fun openBle(): Boolean {
        return bleadapter.enable()
    }

    fun closeBle(): Boolean {
        return bleadapter.disable()
    }

    fun connect(address: String, seconds: Int, back: ConnectResult) {
        bleServiceControl.bleCallbackC = this
        bleServiceControl.connect(address)
        Thread {
            var nowtime = 0
            while (true) {
                if (bleServiceControl.isconnect || nowtime == seconds) {
                    break
                }
                Thread.sleep(1000)
                nowtime++
            }
            handler.post {
                back.result(bleServiceControl.isconnect)
            }
        }.start()
    }

    fun readCharacteristics(uuid: String) {
        bleServiceControl.ReadCmd(uuid)
    }

    fun writeHex(a: String, rx: String, tx: String): Boolean {
        RxData = ""
        setChannel(rx, tx)
        return bleServiceControl.WriteCmd(a, 0)
    }

    fun writeUtf(a: String, rx: String, tx: String): Boolean {
        RxData = ""
        setChannel(rx, tx)
        return bleServiceControl.WriteCmd(a.toByteArray(Charset.forName("UTF-8")), 0)
    }

    fun writeBytes(a: ByteArray, rx: String, tx: String): Boolean {
        RxData = ""
        setChannel(rx, tx)
        return bleServiceControl.WriteCmd(a, 0)
    }

    fun startScan(): Boolean {
        return scan.setmBluetoothAdapter()
    }

    fun stopScan() {
        scan.scanLeDevice(false)
    }

    fun disconnect() {
        bleServiceControl.disconnect()
    }

    fun isConnect(): Boolean {
        return bleServiceControl.isconnect
    }

}


