package com.punpuf.e_usp.di

import android.content.Context
import androidx.room.Room
import com.punpuf.e_usp.Const
import com.punpuf.e_usp.background.UpdateTokenWorkerHelper
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth10aService
import com.punpuf.e_usp.BuildConfig
import com.punpuf.e_usp.db.*
import com.punpuf.e_usp.network.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO


    // ******************************************************
    // ********************** DATABASE **********************
    // ******************************************************

    @Singleton @Provides
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java,
            Const.DB_CARD_NAME
        ).build()
    }

    @Singleton @Provides
    fun provideProfilePictureDao(database: Database): ProfilePictureInfoDao = database.profilePictureInfoDao()

    @Singleton @Provides
    fun provideTokenDao(database: Database): TokenDao = database.tokenDao()

    @Singleton @Provides
    fun provideUserInfoDao(database: Database): UserInfoDao = database.userInfoDao()

    @Singleton @Provides
    fun provideProfileDao(database: Database): UserProfileDao = database.userProfileDao()

    @Singleton @Provides
    fun provideRestaurantDao(database: Database): RestaurantDao = database.restaurantDao()

    @Singleton @Provides
    fun provideMenuDao(database: Database): MenuDao = database.menuDao()

    @Singleton @Provides
    fun provideSelectedRestaurantDao(database: Database): SelectedRestaurantDao = database.selectedRestaurantDao()

    @Singleton @Provides
    fun provideBoletoDao(database: Database): BoletoDao = database.boletoDao()

    @Singleton @Provides
    fun provideBookUser(database: Database): BookUserDao = database.bookUserDao()


    // ******************************************************
    // ********************** FILE I/O **********************
    // ******************************************************

    @Provides
    fun provideFileOutputStream(@ApplicationContext context: Context): FileOutputStream {
        return context.openFileOutput(Const.FILE_NAME_PROFILE_PIC, Context.MODE_PRIVATE)
    }

    @Provides
    fun provideFileInputStream(@ApplicationContext context: Context): FileInputStream {
        return context.openFileInput(Const.FILE_NAME_PROFILE_PIC)
    }

    @Provides
    fun provideFileDirectory(@ApplicationContext context: Context): File {
        return context.filesDir
    }


    // ******************************************************
    // *********************** NETWORK **********************
    // ******************************************************
    @Singleton
    @Provides
    fun provideRetrofitClient(): OkHttpClient {
        val interceptor : HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder().apply {
            this.addInterceptor(interceptor)
        }.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Const.NETWORK_BASE_URL)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create()).apply {
                if (BuildConfig.DEBUG) client(client)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideUspNetworkService(retrofit: Retrofit): UspNetworkService {
        return retrofit.create(UspNetworkService::class.java)
    }

    @Singleton
    @Provides
    fun provideCustomNetworkService(retrofit: Retrofit): CustomNetworkService {
        return retrofit.create(CustomNetworkService::class.java)
    }

    @Singleton
    @Provides
    fun provideLibraryNetworkService(retrofit: Retrofit): LibraryNetworkService {
        return retrofit.create(LibraryNetworkService::class.java)
    }


    // NETWORK AUTH

    @Singleton
    @Provides
    fun provideAuthService(): OAuth10aService {
        return ServiceBuilder(UspApi.API_KEY)
            .apiSecret(UspApi.API_SECRET)
            .debug()
            .build(UspApi())
    }

    // WORK MANAGER

    @Singleton
    @Provides
    fun provideUpdateTokenWorkerHelper(@ApplicationContext context: Context): UpdateTokenWorkerHelper {
        return UpdateTokenWorkerHelper(context)
    }
}

