package com.punpuf.e_usp.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.punpuf.e_usp.vo.BookUserType
import com.punpuf.e_usp.vo.DailyMenu

class Converters {

    @TypeConverter
    fun toBookUserType(value: Int) = enumValues<BookUserType>()[value]

    @TypeConverter
    fun fromBookUserType(type: BookUserType) = type.ordinal

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