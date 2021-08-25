package com.example.pdf_scanner.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

/**
 * Created by Nguyen Manh Tien
 */

class SharedPrefs @Inject constructor(val context: Context) : SharedPrefsSource {

    override fun getPref(): SharedPreferences {
        return context.getSharedPreferences(
            context.packageName + "_preferences", Context.MODE_PRIVATE
        )
    }

    override fun getEditor(): SharedPreferences.Editor {
        val pref = getPref()
        return pref.edit()
    }

    override fun removeValue(key: String?) {
        val editor = getEditor()
        editor.remove(key)
        editor.commit()
    }

    override fun putString(key: String?, value: String?) {
        val editor = getEditor()
        editor.putString(key, value)
        editor.commit()
    }

    override fun getString(key: String?): String? {
        val pref = getPref()
        val defaultValue = ""
        return pref.getString(key, defaultValue)
    }

    override fun putInt(key: String?, value: Int) {
        val editor = getEditor()
        editor.putInt(key, value)
        editor.commit()
    }

    override fun getInt(key: String?): Int {
        val pref = getPref()
        val defaultValue = 0
        return pref.getInt(key, defaultValue)
    }

    override fun getInt(key: String?, deff: Int): Int {
        val pref = getPref()
        return pref.getInt(key, deff)
    }

    override fun putLong(key: String?, value: Long) {
        val editor = getEditor()
        editor.putLong(key, value)
        editor.commit()
    }

    override fun getLong(key: String?): Long {
        val pref = getPref()
        val defaultValue: Long = 0
        return pref.getLong(key, defaultValue)
    }

    override fun getBoolean(key: String?): Boolean {
        val pref = getPref()
        val defaultValue = false
        return pref.getBoolean(key, defaultValue)
    }

    override fun putBoolean(key: String?, value: Boolean) {
        val editor = getEditor()
        editor.putBoolean(key, value)
        editor.commit()
    }

    override fun putDouble(key: String?, value: Double) {
        val editor = getEditor()
        editor.putString(key, value.toString())
        editor.commit()
    }

    override fun getDouble(key: String?): Double {
        val pref = getPref()
        var value = 0.0
        val valueStr = pref.getString(key, "0")
        if (valueStr != null) {
            value = java.lang.Double.valueOf(valueStr)
        }
        return value
    }

    override fun containsKey(key: String?): Boolean {
        val pref = getPref()
        return pref.contains(key)
    }

}

interface SharedPrefsSource {
    fun getPref(): SharedPreferences
    fun getEditor(): SharedPreferences.Editor
    fun removeValue(key: String?)
    fun putString(key: String?, value: String?)
    fun getString(key: String?): String?
    fun putInt(key: String?, value: Int)
    fun getInt(key: String?): Int
    fun getInt(key: String?, deff: Int): Int
    fun putLong(key: String?, value: Long)
    fun getLong(key: String?): Long
    fun getBoolean(key: String?): Boolean
    fun putBoolean(key: String?, value: Boolean)
    fun putDouble(key: String?, value: Double)
    fun getDouble(key: String?): Double
    fun containsKey(key: String?): Boolean
}