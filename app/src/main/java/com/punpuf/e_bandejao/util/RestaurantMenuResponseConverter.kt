package com.punpuf.e_bandejao.util

import com.punpuf.e_bandejao.vo.DailyMenu
import com.punpuf.e_bandejao.vo.WeeklyMenu
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber.d
import timber.log.Timber.e
import java.text.SimpleDateFormat
import java.util.*

class RestaurantMenuResponseConverter {
    companion object {

        fun convertResponse(restaurantId: Int, response: JSONObject): WeeklyMenu {
            val message = response.getJSONObject("message")
            val hasError = message.getBoolean("error")
            val errorMessage = message.getString("message")
            d("Response has Error?: $hasError; $errorMessage ")

            if (hasError) return WeeklyMenu(restaurantId, null, emptyList(), null, )

            val observation = response.getJSONObject("observation").getString("observation")

            val mealArray = response.getJSONArray("meals")
            val dailyMenuList = mutableListOf<DailyMenu>()


            for (meal: JSONObject in mealArray) {
                val dinner = meal.getJSONObject("dinner")
                val dinnerMenu = dinner.getString("menu")
                val dinnerCalories = dinner.getString("calories")

                val lunch = meal.getJSONObject("lunch")
                val lunchMenu = lunch.getString("menu")
                val lunchCalories = lunch.getString("calories")

                val date = meal.getString("date")
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                if (formatter.parse(date) == null) continue

                val calendar = Calendar.getInstance().apply { time = formatter.parse(date)!! }
                val dateStart = calendar.timeInMillis
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                val dateEnd = calendar.timeInMillis

                dailyMenuList.add(
                    DailyMenu(date, dateStart, dateEnd, lunchMenu, lunchCalories, dinnerMenu, dinnerCalories,))
            }

            val sortedMenuList = dailyMenuList.sortedWith(compareBy{it.dateProcessedStart})

            return WeeklyMenu(restaurantId, observation, sortedMenuList, sortedMenuList.last().dateProcessedEnd)
        }

    }
}

private operator fun JSONArray.iterator(): Iterator<JSONObject> {
    return object : Iterator<JSONObject> {
        var current = 0
        override fun hasNext(): Boolean = current < length()
        override fun next(): JSONObject { val cObj = getJSONObject(current); current++; return cObj }
    }
}
