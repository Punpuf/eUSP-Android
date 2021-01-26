package com.punpuf.e_bandejao.di

import android.content.Context
import androidx.room.Room
import com.punpuf.e_bandejao.Const
import com.punpuf.e_bandejao.background.UpdateTokenWorkerHelper
import com.punpuf.e_bandejao.network.LiveDataCallAdapterFactory
import com.punpuf.e_bandejao.network.UspApi
import com.punpuf.e_bandejao.network.UspNetworkService
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.oauth.OAuth10aService
import com.punpuf.e_bandejao.BuildConfig
import com.punpuf.e_bandejao.db.*
import com.punpuf.e_bandejao.network.CustomNetworkService
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


    //DATABASE

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): CardDatabase {
        return Room.databaseBuilder(
            context,
            CardDatabase::class.java,
            Const.DB_CARD_NAME
        ).build()
    }

    @Singleton
    @Provides
    fun provideProfilePictureDao(cardDatabase: CardDatabase): ProfilePictureInfoDao {
        return cardDatabase.profilePictureInfoDao()
    }

    @Singleton
    @Provides
    fun provideTokenDao(cardDatabase: CardDatabase): TokenDao {
        return cardDatabase.tokenDao()
    }

    @Singleton
    @Provides
    fun provideUserInfoDao(cardDatabase: CardDatabase): UserInfoDao {
        return cardDatabase.userInfoDao()
    }


    @Singleton
    @Provides
    fun provideProfileDao(cardDatabase: CardDatabase): UserProfileDao {
        return cardDatabase.userProfileDao()
    }

    @Singleton
    @Provides
    fun provideRestaurantDao(cardDatabase: CardDatabase): RestaurantDao {
        return cardDatabase.restaurantDao()
    }

    @Singleton
    @Provides
    fun provideMenuDao(cardDatabase: CardDatabase): MenuDao {
        return cardDatabase.menuDao()
    }

    @Singleton
    @Provides
    fun provideSelectedRestaurantDao(cardDatabase: CardDatabase): SelectedRestaurantDao {
        return cardDatabase.selectedRestaurantDao()
    }

    @Singleton
    @Provides
    fun provideBoletoDao(cardDatabase: CardDatabase): BoletoDao {
        return cardDatabase.boletoDao()
    }

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


    //NETWORK

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

