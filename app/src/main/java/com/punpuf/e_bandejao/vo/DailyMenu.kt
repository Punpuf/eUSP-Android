package com.punpuf.e_bandejao.vo

data class DailyMenu(
    val date: String,
    val dateProcessedStart: Long,
    val dateProcessedEnd: Long,
    val lunchMenu: String,
    val lunchCalories: String,
    val dinnerMenu: String ,
    val dinnerCalories: String,
)