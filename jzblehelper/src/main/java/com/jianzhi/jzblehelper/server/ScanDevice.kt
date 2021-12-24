package com.jianzhi.jzblehelper.server

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.jianzhi.jzblehelper.BleHelper
import com.jianzhi.jzblehelper.models.BleBinary

import java.util.ArrayList

class ScanDevice( var context: Context,var blehelper: BleHelper) {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val mLeDevices = ArrayList<BluetoothDevice>()
    private val mLeScanCallback =   object :ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            try {
                blehelper.callback.scanBack(result!!.device, BleBinary(result.scanRecord!!.bytes), result.rssi)
            }catch (e:Exception){}}
        }


    fun setmBluetoothAdapter():Boolean {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (mBluetoothAdapter == null) {
//            Toast.makeText(context, "blenotsupport", Toast.LENGTH_SHORT).show()
        }
       return initPermission()
    }

    //-----------------------method1檢查權限------------------------------------------
    fun initPermission():Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val a = ArrayList<String>();
            if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                a.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                a.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            return if (a.size > 0) {
                blehelper.callback.requestPermission(a)
                false
            } else {
                if (!isLocServiceEnable(context)) {
                    blehelper.callback.needGPS()
                    false
                }else{
                    RequestPermission()
                }
            }
        } else {
          return  RequestPermission()
        }
    }

    fun RequestPermission():Boolean {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val originalBluetooth = mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled
        return if (originalBluetooth) {

            scanLeDevice(true)
            mBluetoothAdapter!!.startDiscovery()
        } else {
            scanLeDevice(true)
            mBluetoothAdapter!!.enable()
            mBluetoothAdapter!!.startDiscovery()
        }
    }

    //----------method2開始掃描
    fun scanLeDevice(enable: Boolean) {
        try{
            if (enable) {
                mLeDevices.clear()

                mBluetoothAdapter!!.bluetoothLeScanner.startScan(mLeScanCallback)
            } else {
                mBluetoothAdapter!!.bluetoothLeScanner.stopScan(mLeScanCallback)
            }
        }catch (e:Exception){}

    }

    companion object {
        fun isLocServiceEnable(context: Context): Boolean {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            return gps || network
        }
    }

}
