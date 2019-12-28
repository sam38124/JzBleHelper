package com.jianzhi.jzblehelper.Callback

import android.bluetooth.BluetoothDevice
import com.jianzhi.jzblehelper.Beans.BleStream

interface Ble_CallBack{
    fun connecting()
    fun connectFalse()
    fun connectSuccess()
    fun rx(a: BleStream)
    fun tx(b: BleStream)
    fun scanBack(device: BluetoothDevice)
    fun requestPermission(permission: ArrayList<String>)
    fun needGps()
}
