package com.punpuf.e_usp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.punpuf.e_usp.Const.Companion.DB_CARD_VERSION
import com.punpuf.e_usp.vo.*

@Database(
    entities = [
        Token::class, UserProfile::class, UserInfo ::class, ProfilePictureInfo::class,
        Restaurant::class, WeeklyMenu::class, SelectedRestaurant::class, Boleto::class, BookUser::class,
        BookOfSearch::class,
    ],
    version = DB_CARD_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun userInfoDao(): UserInfoDao
    abstract fun profilePictureInfoDao(): ProfilePictureInfoDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun menuDao(): MenuDao
    abstract fun selectedRestaurantDao(): SelectedRestaurantDao
    abstract fun boletoDao(): BoletoDao
    abstract fun bookUserDao(): BookOfUserDao
    abstract fun bookOfSearchDao(): BookOfSearchDao
}