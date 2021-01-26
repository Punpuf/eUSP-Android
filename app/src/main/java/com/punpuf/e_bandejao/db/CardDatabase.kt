package com.punpuf.e_bandejao.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.punpuf.e_bandejao.Const.Companion.DB_CARD_VERSION
import com.punpuf.e_bandejao.util.DataConverter
import com.punpuf.e_bandejao.vo.*

@Database(
    entities = [Token::class, UserProfile::class, UserInfo ::class, ProfilePictureInfo::class,
    Restaurant::class, WeeklyMenu::class, SelectedRestaurant::class, Boleto::class],
    version = DB_CARD_VERSION,
    exportSchema = false
)
@TypeConverters(DataConverter::class)
abstract class CardDatabase : RoomDatabase() {
    abstract fun tokenDao(): TokenDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun userInfoDao(): UserInfoDao
    abstract fun profilePictureInfoDao(): ProfilePictureInfoDao
    abstract fun restaurantDao(): RestaurantDao
    abstract fun menuDao(): MenuDao
    abstract fun selectedRestaurantDao(): SelectedRestaurantDao
    abstract fun boletoDao(): BoletoDao
}