package com.jianzhi.jzblehelper.server

import android.app.ActivityManager
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.jianzhi.jzblehelper.BleHelper

import java.util.ArrayList
import java.util.HashMap
import java.util.UUID

import android.content.ContentValues.TAG
import android.content.Context.BIND_AUTO_CREATE
import com.jianzhi.jzblehelper.FormatConvert.StringHexToByte

class BleServiceControl {
    private val LIST_NAME = "NAME"
    private val LIST_UUID = "UUID"
    private var mBluetoothLeService: BluetoothLeService? = null
    private var mGattCharacteristics = ArrayList<BluetoothGattCharacteristic>()
    var isconnect = false
    private var mNotifyCharacteristic: BluetoothGattCharacteristic? = null
    private var mDeviceAddress: String? = null
    var getData = ByteArray(10)
    var bleCallbackC: BleHelper? = null
    var mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            mBluetoothLeService = (service as BluetoothLeService.LocalBinder).service
            mBluetoothLeService!!.bleCallbackC = bleCallbackC
            if (!mBluetoothLeService!!.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth")
            }
            mBluetoothLeService!!.connect(mDeviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService = null
        }
    }
    var first = true

    fun connect(mDeviceAddress: String) {
        try {
            this.mDeviceAddress = mDeviceAddress
            if (mBluetoothLeService != null) {
                mBluetoothLeService!!.connect(mDeviceAddress)
                bleCallbackC!!.context.unbindService(mServiceConnection)
            }
            val gattServiceIntent = Intent(bleCallbackC!!.context, BluetoothLeService::class.java)
            bleCallbackC!!.context.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun displayGattServices(gattServices: List<BluetoothGattService>?) {
        if (gattServices == null) return
        var uuid: String? = null
        val unknownServiceString = "unknownServiceString"
        val unknownCharaString = "unknownCharaString"
        val gattServiceData = ArrayList<HashMap<String, String>>()
        val gattCharacteristicData = ArrayList<ArrayList<HashMap<String, String>>>()
        mGattCharacteristics = ArrayList()
        for (gattService in gattServices) {
            val currentServiceData = HashMap<String, String>()
            uuid = gattService.uuid.toString()
            Log.d("uuid", uuid)
            currentServiceData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownServiceString)
            currentServiceData[LIST_UUID] = uuid
            gattServiceData.add(currentServiceData)

            val gattCharacteristicGroupData = ArrayList<HashMap<String, String>>()
            val gattCharacteristics = gattService.characteristics

            // Loops through available Characteristics.
            for (gattCharacteristic in gattCharacteristics) {
                mGattCharacteristics.add(gattCharacteristic)
                val currentCharaData = HashMap<String, String>()
                uuid = gattCharacteristic.uuid.toString()
                currentCharaData[LIST_NAME] = SampleGattAttributes.lookup(uuid, unknownCharaString)
                currentCharaData[LIST_UUID] = uuid
                gattCharacteristicGroupData.add(currentCharaData)
            }
            gattCharacteristicData.add(gattCharacteristicGroupData)
        }
    }

    fun ReadCmd(uuid: String) {
        for (a in mGattCharacteristics) {
            Log.w("char", "" + a.uuid)
            if (UUID.fromString(uuid) == a.uuid) {
                mBluetoothLeService!!.readCharacteristic(a)
                break
            }
        }
    }

    fun SubscribeRxChannel() {
        for (a in mGattCharacteristics) {
            if (UUID.fromString(bleCallbackC!!.RXchannel) == a.uuid) {
                mBluetoothLeService!!.setCharacteristicNotification(a, true)
            }
        }
    }

    fun WriteCmd(write: ByteArray, check: Int): Boolean {
        SubscribeRxChannel()
        for (a in mGattCharacteristics) {
            if (UUID.fromString(bleCallbackC!!.TXchannel) == a.uuid) {
                mBluetoothLeService!!.check = check
                mBluetoothLeService!!.tmp = ""
                mNotifyCharacteristic = a
                mNotifyCharacteristic!!.value = write
                mBluetoothLeService!!.writeCharacteristic(mNotifyCharacteristic!!)
                return true
            }
        }
        return false
    }

    fun WriteCmd(write: String, check: Int): Boolean {
        SubscribeRxChannel()
        for (a in mGattCharacteristics) {
            if (UUID.fromString(bleCallbackC!!.TXchannel) == a.uuid) {
                mBluetoothLeService!!.check = check
                mBluetoothLeService!!.tmp = ""
                mNotifyCharacteristic = a
                mNotifyCharacteristic!!.value = StringHexToByte(write)
                mBluetoothLeService!!.writeCharacteristic(mNotifyCharacteristic!!)
                return true
            }
        }
        return false
    }

    fun disconnect() {
        if (mBluetoothLeService != null) {
            mBluetoothLeService!!.disconnect()
        }
    }

    companion object {

        fun isServiceRunning(context: Context, ServiceName: String?): Boolean {
            if ("" == ServiceName || ServiceName == null)
                return false
            val myManager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningService = myManager
                .getRunningServices(30) as ArrayList<ActivityManager.RunningServiceInfo>
            for (i in runningService.indices) {
                if (runningService[i].service.className.toString() == ServiceName) {
                    return true
                }
            }
            return false
        }
    }

}
