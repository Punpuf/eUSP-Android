package com.punpuf.e_usp.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.punpuf.e_usp.vo.DailyMenu

class DataConverter {
    @TypeConverter
    fun fromCountryLangList(value: List<DailyMenu>): String {
        val gson = Gson()
        val type = object : TypeToken<List<DailyMenu>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toCountryLangList(value: String): List<DailyMenu> {
        val gson = Gson()
        val type = object : TypeToken<List<DailyMenu>>() {}.type
        return gson.fromJson(value, type)
    }
}