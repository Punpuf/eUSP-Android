package com.punpuf.e_usp.vo

import androidx.compose.runtime.Stable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH_AUTHORS
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH_BASE
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH_EDITOR
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH_IDIOM
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH_LOCATIONS
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH_MEDIA_TYPE
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH_PUBLICATION_DATE
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH_SUBJECTS
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH_SYS_NO
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_SEARCH_TITLE
import com.punpuf.e_usp.util.StringListTypeAdapter
import kotlinx.android.parcel.Parcelize

@Stable

@Entity(tableName = TABLE_BOOK_OF_SEARCH)
data class BookOfSearch(

    @PrimaryKey
    @Expose @SerializedName("sysNo")
    @ColumnInfo(name = TABLE_BOOK_OF_SEARCH_SYS_NO)
    var sysNo: String = "",

    @Expose @SerializedName("base")
    @ColumnInfo(name = TABLE_BOOK_OF_SEARCH_BASE)
    var base: String = "",

    @Expose @SerializedName("title")
    @ColumnInfo(name = TABLE_BOOK_OF_SEARCH_TITLE)
    var title: String = "",

    @JsonAdapter(StringListTypeAdapter::class)
    @Expose @SerializedName("authors")
    @ColumnInfo(name = TABLE_BOOK_OF_SEARCH_AUTHORS)
    var authors: String = "",



    @Expose @SerializedName("editor")
    @ColumnInfo(name = TABLE_BOOK_OF_SEARCH_EDITOR)
    var editor: String = "",

    @Expose @SerializedName("idiom")
    @ColumnInfo(name = TABLE_BOOK_OF_SEARCH_IDIOM)
    var idiom: String = "",

    @Expose @SerializedName("mediaType")
    @ColumnInfo(name = TABLE_BOOK_OF_SEARCH_MEDIA_TYPE)
    var mediaType: String = "",

    @Expose @SerializedName("publicationDate")
    @ColumnInfo(name = TABLE_BOOK_OF_SEARCH_PUBLICATION_DATE)
    var publicationDate: String = "",
    
    @Expose @SerializedName("subjects")
    @ColumnInfo(name = TABLE_BOOK_OF_SEARCH_SUBJECTS)
    var subjects: List<String> = emptyList(),


    
    @Expose @SerializedName("locations")
    @ColumnInfo(name = TABLE_BOOK_OF_SEARCH_LOCATIONS)
    var locations: List<BookOfSearchLocation> = emptyList(),
    
)

//todo check later if inHouse and other attr not added are actually important
