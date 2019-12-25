package com.jianzhi.jzblehelper

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import com.jianzhi.jzblehelper.Server.BleServiceControl
import com.jianzhi.jzblehelper.Server.ScanDevice
import java.nio.charset.Charset

class Ble_Helper(val caller:Ble_CallBack,val context: Context) {
     var RXchannel=""
     var TXchannel=""
    var handler= Handler()
    var bleServiceControl= BleServiceControl()
    var scan = ScanDevice(this,context)
     fun SetChannel(rx:String,tx:String){
         RXchannel=rx
         TXchannel=tx
     }
    fun Connect(address: String,txchannel:String,rxchannel:String,time:Int) {
        RXchannel=rxchannel
        TXchannel=txchannel
        scan.scanLeDevice(false)
        caller.Connecting()
        bleServiceControl.bleCallbackC=this
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
     fun WriteHex(a:String){
         bleServiceControl.WriteCmd(a,0)
     }
     fun WriteUtf(a:String){
         bleServiceControl.WriteCmd(a.toByteArray(Charset.defaultCharset()),0)
     }
     fun WriteBytes(a:ByteArray){
         bleServiceControl.WriteCmd(a,0)
     }

    fun StartScan() {
        scan.setmBluetoothAdapter()
    }
    fun StopScan(){
        scan.scanLeDevice(false)
    }
    fun Disconnect(){
        bleServiceControl.disconnect()
    }
    interface Ble_CallBack{
        fun Connecting()
        fun ConnectFalse()
        fun ConnectSuccess()
        fun RX(a:String)
        fun TX(b:String)
        fun ScanBack(device:BluetoothDevice)
        fun RequestPermission(permission:ArrayList<String>)
        fun NeedGps()
    }
}


