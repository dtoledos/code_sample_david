package com.us.myfree.agent.common.extensions

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast

fun SharedPreferences.Editor.putDouble(key: String, double: Double) =
        putLong(key, java.lang.Double.doubleToRawLongBits(double))

fun SharedPreferences.putDouble2(key: String, double: Double) =
        edit().putLong(key, java.lang.Double.doubleToRawLongBits(double))

fun SharedPreferences.getDouble2(key: String, default: Double) =
        getLong(key, java.lang.Double.doubleToRawLongBits(default))

fun SharedPreferences.getDouble(key: String, default: Double) =
        java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToRawLongBits(default)))


// add entry in shared preference
fun SharedPreferences.putAny(name: String, any: Any) {
    when (any) {
        is String -> edit().putString(name, any).apply()
        is Int -> edit().putInt(name, any).apply()
        is Boolean -> edit().putBoolean(name,any).apply()

        // also accepts Float, Long & StringSet
    }
}

// remove entry from shared preference
fun SharedPreferences.remove(name:String){
    edit().remove(name).apply()
}


