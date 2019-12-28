package com.jianzhi.jzblehelper.Beans

import com.jianzhi.jzblehelper.FormatConvert.bytesToHex
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class BleStream (private var data:ByteArray){
    fun readUTF():String{
        return String(data, StandardCharsets.UTF_8);
    }
    fun readBytes():ByteArray{
        return data
    }
    fun readHEX():String{
        return bytesToHex(data)
    }
}