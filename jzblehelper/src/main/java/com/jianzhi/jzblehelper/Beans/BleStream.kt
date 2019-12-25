package com.jianzhi.jzblehelper.Beans

import com.jianzhi.jzblehelper.FormatConvert.bytesToHex
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class BleStream (private var data:ByteArray){
    fun ReadUTF():String{
        return String(data, StandardCharsets.UTF_8);
    }
    fun ReadBytes():ByteArray{
        return data
    }
    fun ReadHEX():String{
        return bytesToHex(data)
    }
}