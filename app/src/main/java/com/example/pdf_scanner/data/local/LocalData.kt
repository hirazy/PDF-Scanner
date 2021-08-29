package com.example.pdf_scanner.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.pdf_scanner.KEY_CONNECTION_DATA
import com.example.pdf_scanner.KEY_LANGUAGE_OCR
import com.example.pdf_scanner.data.Resource
import com.example.pdf_scanner.data.dto.LanguageOCR
import com.example.pdf_scanner.utils.SharedPrefsSource
import javax.inject.Inject

/**
 * Created by Nguyen Manh Tien
 */
class LocalData @Inject
constructor(val context: Context, val sharedPrefs: SharedPrefsSource) {

//    fun getCacheApps(): Resource<Set<String>> {
//        val sharedPref = context.getSharedPreferences(KEY_CONNECTION_DATA, 0)
//        return Resource.Success(sharedPref.getStringSet(KEY_MANAGE_APPS, setOf()) ?: setOf())
//        return Resource.Success()
//    }

    fun cacheApps(name: Set<String>): Resource<Boolean> {
//        val sharedPref = context.getSharedPreferences(KEY_CONNECTION_DATA, 0)
//        val editor: SharedPreferences.Editor = sharedPref.edit()
//        editor.putStringSet(KEY_MANAGE_APPS, name)
//        editor.apply()
//        val isSuccess = editor.commit()
//        return Resource.Success(isSuccess)
        return Resource.Success(true)
    }

    fun removeFromFile(name: String): Resource<Boolean> {
//        val sharedPref = context.getSharedPreferences(KEY_CONNECTION_DATA, 0)
//        var set = sharedPref.getStringSet(KEY_MANAGE_APPS, mutableSetOf<String>())?.toMutableSet()
//            ?: mutableSetOf()
//        if (set.contains(name)) {
//            set.remove(name)
//        }
//        val editor: SharedPreferences.Editor = sharedPref.edit()
//        editor.clear()
//        editor.apply()
//        editor.commit()
//        editor.putStringSet(KEY_MANAGE_APPS, set)
//        editor.apply()
//        val isSuccess = editor.commit()
//        return Resource.Success(isSuccess)
        return Resource.Success(true)
    }

    fun getCacheSettings(keySettings: String): Resource<Boolean> {
//        val sharedPref = context.getSharedPreferences(KEY_CONNECTION_DATA, 0)
//        return Resource.Success(sharedPref.getBoolean(keySettings, true))
        return Resource.Success(true)
    }

    fun getCacheSettingsDefaultFalse(keySettings: String): Resource<Boolean> {
        // val sharedPref = context.getSharedPreferences(KEY_CONNECTION_DATA, 0)
        // return Resource.Success(sharedPref.getBoolean(keySettings, false))
        return Resource.Success(true)
    }

    fun cacheSettings(keySettings: String, isEnabled: Boolean): Resource<Boolean> {
//        val sharedPref = context.getSharedPreferences(KEY_CONNECTION_DATA, 0)
//        val editor: SharedPreferences.Editor = sharedPref.edit()
//        editor.putBoolean(keySettings, isEnabled)
//        editor.apply()
//        val isSuccess = editor.commit()
//        return Resource.Success(isSuccess)
        return Resource.Success(true)
    }

    fun getCacheLanguageOCR(): Resource<Set<String>>{
        val sharedPrefs = context.getSharedPreferences(KEY_CONNECTION_DATA, 0)
        return Resource.Success(sharedPrefs.getStringSet(KEY_LANGUAGE_OCR, setOf()) ?: setOf())
    }

    fun cacheLanguageOCR(languageOCR: Set<String>): Resource<Boolean>{
        val sharedPref = context.getSharedPreferences(KEY_CONNECTION_DATA, 0)
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putStringSet(KEY_LANGUAGE_OCR, languageOCR)
        editor.apply()
        val isSuccess = editor.commit()
        return Resource.Success(isSuccess)
    }

    fun removeLanguageOCR(language: String): Resource<Boolean>{
        val sharedPref = context.getSharedPreferences(KEY_CONNECTION_DATA, 0)
        var set = sharedPref.getStringSet(KEY_LANGUAGE_OCR, mutableSetOf<String>())?.toMutableSet()
            ?: mutableSetOf()
        if (set.contains(language)) {
            set.remove(language)
        }
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.apply()
        editor.commit()
        editor.putStringSet(KEY_LANGUAGE_OCR, set)
        editor.apply()
        val isSuccess = editor.commit()
        return Resource.Success(isSuccess)
    }
}