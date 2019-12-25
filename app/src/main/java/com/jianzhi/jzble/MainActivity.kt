package com.jianzhi.jzble

import android.bluetooth.BluetoothDevice
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.jianzhi.jzblehelper.Ble_Helper

class MainActivity : AppCompatActivity(), Ble_Helper.Ble_CallBack {
    var RxChannel = "00008D81-0000-1000-8000-00805F9B34FB"
    var TxChannel = "00008D82-0000-1000-8000-00805F9B34FB"
    override fun Connecting() {
        //當ble開始連線時觸發
        Log.d("JzBleMessage", "藍牙正在連線中")
    }

    override fun ConnectFalse() {
        //當ble連線失敗時觸發
        Log.d("JzBleMessage", "藍牙斷線")
    }

    override fun ConnectSuccess() {
        //當ble連線成功時觸發
        Log.d("JzBleMessage", "藍牙連線")
    }

    override fun RX(a: String) {
        //當ble收到消息時觸發String為(HexString(16進位字串表示法))
        Log.d("JzBleMessage", "收到藍牙消息$a")
    }

    override fun TX(b: String) {
        //當ble傳送訊息時觸發String為(HexString(16進位字串表示法))
        Log.d("JzBleMessage", "傳送藍牙消息$b")
    }

    override fun ScanBack(device: BluetoothDevice) {
        //當掃描到新裝置時觸發
        Log.d("JzBleMessage", "掃描到裝置:名稱${device.name}/地址:${device.address}")
        //當獲取到device.address即可儲存下來，藍牙連線時會使用到
    }

    override fun RequestPermission(permission: ArrayList<String>) {
        //當藍牙權限不足時觸發
        for (i in permission) {
            Log.e("JzBleMessage", "權限不足請先請求權限${i}")
        }
        ActivityCompat.requestPermissions(this, permission.toTypedArray(), 10)
    }

    override fun NeedGps() {
        //6.0以上的手機必須打開手機定位才能取得藍牙權限，監聽到此function即可提醒使用者打開定位
        Log.d("JzBleMessage", "請打開定位系統")
    }

    lateinit var Ble_Helper: Ble_Helper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Ble_Helper = Ble_Helper(this, this)
        //設定溝通通道

    }

    fun onclick(view: View) {
        when (view.id) {
            R.id.start -> {
                Ble_Helper.StartScan()
            }
            R.id.stop -> {
                Ble_Helper.StopScan()
            }
            R.id.connect -> {
                Ble_Helper.Connect("00:C0:BF:13:05:C7",TxChannel,RxChannel,10)
            }
            R.id.disconnect->{
                Ble_Helper.Disconnect()
            }
            R.id.send->{
                //傳送Hello Ble的訊息
                Ble_Helper.WriteHex("48656C6C6F20426C65")
                Ble_Helper.WriteUtf("Hello Ble")
                Ble_Helper.WriteBytes(byteArrayOf(0x48,0x65,0x6C,0x6C,0x6F,0x20,0x42,0x6C,0x65))
            }
        }
    }
}
