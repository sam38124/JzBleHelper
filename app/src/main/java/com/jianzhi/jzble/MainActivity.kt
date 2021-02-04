package com.jianzhi.jzble

import android.bluetooth.BluetoothDevice
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.jianzhi.jzblehelper.models.BleBinary
import com.jianzhi.jzblehelper.BleHelper
import com.jianzhi.jzblehelper.callback.BleCallBack
import com.jianzhi.jzblehelper.callback.ConnectResult

class MainActivity : AppCompatActivity(), BleCallBack {
    var RxChannel = "00008D81-0000-1000-8000-00805F9B34FB"
    var TxChannel = "00008D82-0000-1000-8000-00805F9B34FB"
    override fun onConnecting() {
        //當ble開始連線時觸發
        Log.d("JzBleMessage", "藍牙正在連線中")
    }

    override fun onConnectFalse() {
        //當ble連線失敗時觸發
        Log.d("JzBleMessage", "連線失敗")
    }

    override fun onDisconnect() {
        //當藍牙斷線時觸發
        Log.d("JzBleMessage", "藍牙斷線")
    }

    override fun onConnectSuccess() {
        //當ble連線成功時觸發
        Log.d("JzBleMessage", "藍牙連線")
    }

    override fun rx(a: BleBinary) {
        //三種Format方式接收藍牙訊息
        //1.readUTF()
        //2.readHEX()
        //3.readBytes()
        Log.d("JzBleMessage", "收到藍牙消息${a.readHEX()}")
    }

    override fun tx(b: BleBinary) {
        //當ble傳送訊息時觸發
        //1.readUTF()
        //2.readHEX()
        //3.readBytes()
        Log.d("JzBleMessage", "傳送藍牙消息${b.readUTF()}")
    }

    override fun scanBack(device: BluetoothDevice,bleBinary: BleBinary) {
        //當掃描到新裝置時觸發
        Log.d("JzBleMessage", "掃描到裝置:名稱${device.name}/地址:${device.address}")
        //當獲取到device.address即可儲存下來，藍牙連線時會使用到
    }

    override fun requestPermission(permission: ArrayList<String>) {
        //當藍牙權限不足時觸發
        for (i in permission) {
            Log.e("JzBleMessage", "權限不足請先請求權限${i}")
        }
        ActivityCompat.requestPermissions(this, permission.toTypedArray(), 10)
    }

    override fun needGPS() {
        //6.0以上的手機必須打開手機定位才能取得藍牙權限，監聽到此function即可提醒使用者打開定位，或者跳轉至設定頁面提醒打開定位
        Log.d("JzBleMessage", "請打開定位系統")
    }

    lateinit var BleHelper: BleHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BleHelper = BleHelper(this, this)
    }

    fun onclick(view: View) {
        when (view.id) {
            R.id.start -> {
                BleHelper.startScan()
            }
            R.id.stop -> {
                BleHelper.stopScan()
            }
            R.id.connect -> {
                BleHelper.connect("00:C0:BF:13:05:C7",10,ConnectResult{})
            }
            R.id.disconnect->{
                BleHelper.disconnect()
            }
            R.id.send->{
                //傳送Hello Ble的訊息
                //RxChannel為接收資料的特徵值，TxChannel為傳送資料的特徵值
//                BleHelper.WriteHex("48656C6C6F20426C65",RxChannel,TxChannel)
//                BleHelper.WriteUtf("Hello Ble",RxChannel,TxChannel)
//                BleHelper.WriteBytes(byteArrayOf(0x48,0x65,0x6C,0x6C,0x6F,0x20,0x42,0x6C,0x65),RxChannel,TxChannel)
                BleHelper.writeHex("0AFE03000754504D539CC8F5",RxChannel,TxChannel)
            }
        }
    }
}
