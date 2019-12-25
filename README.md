# JzBleHelper
一套高效且敏捷的Ble溝通框架，幫助開發者快速部署你的藍牙應用．
## 目錄
* [如何導入到專案](#Import)
* [快速使用](#Use)
* [藍牙掃描以及連線](#scan)
* [訊息傳送](#send)
* [關於我](#About)

<a name="Import"></a>
## 如何導入到項目
> 支持jcenter。 <br/>

### jcenter導入方式
在app專案包的build.gradle中添加
```kotlin
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

在需要用到這個庫的module中的build.gradle中的dependencies中加入
```kotlin
dependencies {
implementation 'com.github.sam38124:JzBleHelper:1.0'
}
```
<a name="Use"></a>
## 如何使用

### 在要監聽藍牙的地方繼承Ble_Helper.Ble_CallBack
#### 1.Activity範例
```kotlin
class MainActivity : AppCompatActivity(), Ble_Helper.Ble_CallBack {
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

    override fun RX(a: BleStream) {
        //三種Format方式接收藍牙訊息
        //1.ReadUTF()
        //2.ReadHEX()
        //3.ReadBytes()
        Log.d("JzBleMessage", "收到藍牙消息${a.ReadHEX()}")
    }

    override fun TX(b: BleStream) {
        //當ble傳送訊息時觸發
        Log.d("JzBleMessage", "傳送藍牙消息${b.ReadUTF()}")
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
        //6.0以上的手機必須打開手機定位才能取得藍牙權限，監聽到此function即可提醒使用者打開定位，或者跳轉至設定頁面提醒打開定位
        Log.d("JzBleMessage", "請打開定位系統")
    }

lateinit var Ble_Helper: Ble_Helper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Ble_Helper = Ble_Helper(this, this)
    }

```
<a name="scan"></a>
## 藍牙掃描以及連線
#### 開始掃描藍牙

```kotlin
Ble_Helper.StartScan()
```
#### 停止掃描藍牙

```kotlin
Ble_Helper.StopScan()
```
#### 藍牙連線

```kotlin
//嘗試連線10秒，Address替換為搜尋到的藍牙地址，例如：00:C0:BF:13:05:C7
Ble_Helper.Connect("Address",10)
```
#### 藍牙斷線
```kotlin
Ble_Helper.Disconnect()
```
<a name="send"></a>
## 訊息傳送
#### 三種方式向藍牙傳送Hello Ble的訊息，RxChannel為要接收資料的特徵值，TxChannel為要傳送資料的特徵值！
##### TxChannel以及RxChannel的UUID必需由藍牙的開發者定義<br>
##### 格式範例:00008D81-0000-1000-8000-00805F9B34FB<br>
##### HexString
```kotlin
 Ble_Helper.WriteHex("48656C6C6F20426C65",RxChannel,TxChannel)
```
##### UTF-8
```kotlin
Ble_Helper.WriteUtf("Hello Ble",RxChannel,TxChannel)
```
##### Bytes
```kotlin
Ble_Helper.WriteBytes(byteArrayOf(0x48,0x65,0x6C,0x6C,0x6F,0x20,0x42,0x6C,0x65),RxChannel,TxChannel)
```

<a name="About"></a>
### 關於我
橙的電子Android and Ios Developer

*line:sam38124

*gmail:sam38124@gmail.com