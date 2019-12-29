package com.jianzhi.jzblehelper.server

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.jianzhi.jzblehelper.BleHelper

import java.util.ArrayList

class ScanDevice( var context: Context,var blehelper: BleHelper) {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val mLeDevices = ArrayList<BluetoothDevice>()
    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
            blehelper.callback.scanBack(device)
        val stringBuilder = StringBuilder()
        for (a in scanRecord) {
            stringBuilder.append(String.format("%02X", a))
        }
        Log.d("scanrecord", stringBuilder.toString())
        try {
            Log.d("name", device.name)
        } catch (e: Exception) {
            mLeDevices.add(device)
            Log.d("name", "UNROWN")
        }
    }

    fun setmBluetoothAdapter() {
        initPermission()
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (mBluetoothAdapter == null) {
            Toast.makeText(context, "notsupport", Toast.LENGTH_SHORT).show()
        }
    }

    //-----------------------method1檢查權限------------------------------------------
    fun initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val a = ArrayList<String>();
            if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                a.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                a.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            if (a.size > 0) {
                blehelper.callback.requestPermission(a)
            } else {
                if (!isLocServiceEnable(context)) {
                    blehelper.callback.needGPS()
                }else{
                    RequestPermission()
                }
            }
        } else {
            RequestPermission()
        }
    }

    fun RequestPermission() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val originalBluetooth = mBluetoothAdapter != null && mBluetoothAdapter!!.isEnabled
        if (originalBluetooth) {
            scanLeDevice(true)
            mBluetoothAdapter!!.startDiscovery()
        } else if (originalBluetooth == false) {
            scanLeDevice(true)
            mBluetoothAdapter!!.enable()
        }
    }

    //----------method2開始掃描
    fun scanLeDevice(enable: Boolean) {
        try{
            if (enable) {
                mLeDevices.clear()
                mBluetoothAdapter!!.startLeScan(mLeScanCallback)
            } else {
                mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
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
