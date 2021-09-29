package com.punpuf.e_usp.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.punpuf.e_usp.vo.BookOfSearchLocation
import com.punpuf.e_usp.vo.BookOfUserType
import com.punpuf.e_usp.vo.DailyMenu

class Converters {
    
    val gson = Gson()

    @TypeConverter
    fun toBookUserType(value: Int) = enumValues<BookOfUserType>()[value]

    @TypeConverter
    fun fromBookUserType(type: BookOfUserType) = type.ordinal

    @TypeConverter
    fun fromDailyMenuList(value: List<DailyMenu>): String {
        val type = object : TypeToken<List<DailyMenu>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toDailyMenuList(value: String): List<DailyMenu> {
        val type = object : TypeToken<List<DailyMenu>>() {}.type
        return gson.fromJson(value, type)
    }
    
    @TypeConverter
    fun fromSearchLocationList(value: List<BookOfSearchLocation>): String {
        val type = object : TypeToken<List<BookOfSearchLocation>>() {}.type
        return gson.toJson(value, type)
    }
    
    @TypeConverter
    fun toSearchLocationList(value: String): List<BookOfSearchLocation> {
        val type = object : TypeToken<List<DailyMenu>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }

}