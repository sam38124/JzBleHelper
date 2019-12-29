package com.jianzhi.jzblehelper.callback

import android.bluetooth.BluetoothDevice
import com.jianzhi.jzblehelper.models.BleBinary

interface BleCallBack{
    fun onConnecting()
    fun onConnectFalse()
    fun onConnectSuccess()
    fun rx(a: BleBinary)
    fun tx(b: BleBinary)
    fun scanBack(device: BluetoothDevice)
    fun requestPermission(permission: ArrayList<String>)
    fun needGPS()
}
