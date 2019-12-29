package com.jianzhi.jzblehelper.models

import com.jianzhi.jzblehelper.FormatConvert.bytesToHex
import java.nio.charset.StandardCharsets

class BleBinary (private var data:ByteArray){
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