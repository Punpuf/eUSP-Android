package com.punpuf.e_usp.vo

import android.os.Parcelable
import androidx.compose.runtime.Stable
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.punpuf.e_usp.Const
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_AUTHORS
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_CALL_NO
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_DUE_DATE
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_HOLD_SEQ
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_ITEM_SEQ
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_LIBRARY_CODE
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_LIBRARY_NAME
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_MEDIA_TYPE
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_NO_RENEW
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_RETURN_DATE
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_SYS_NO
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_TITLE
import com.punpuf.e_usp.Const.Companion.TABLE_BOOK_OF_USER_TYPE
import com.punpuf.e_usp.util.StringListTypeAdapter
import kotlinx.android.parcel.Parcelize

@Stable
@Parcelize
@Entity(
    tableName = TABLE_BOOK_OF_USER,
    primaryKeys = [TABLE_BOOK_OF_USER_SYS_NO, TABLE_BOOK_OF_USER_ITEM_SEQ, TABLE_BOOK_OF_USER_HOLD_SEQ,
        TABLE_BOOK_OF_USER_CALL_NO, TABLE_BOOK_OF_USER_DUE_DATE, TABLE_BOOK_OF_USER_RETURN_DATE]
)
data class BookUser(

    @ColumnInfo(name = TABLE_BOOK_OF_USER_TYPE)
    var type: BookOfUserType? = null,

    @Expose @SerializedName("sysNo")
    @ColumnInfo(name = TABLE_BOOK_OF_USER_SYS_NO)
    val sysNo: String = "",

    @Expose @SerializedName("itemSeq")
    @ColumnInfo(name = TABLE_BOOK_OF_USER_ITEM_SEQ)
    val itemSeq: String = "",

    @Expose @SerializedName("holdSeq")
    @ColumnInfo(name = TABLE_BOOK_OF_USER_HOLD_SEQ)
    val holdSeq: String = "",

    @Expose @SerializedName("callNo")
    @ColumnInfo(name = TABLE_BOOK_OF_USER_CALL_NO)
    val callNo: String = "",



    @Expose @SerializedName("title")
    @ColumnInfo(name = TABLE_BOOK_OF_USER_TITLE)
    val title: String = "",

    @JsonAdapter(StringListTypeAdapter::class)
    @Expose @SerializedName("authors")
    @ColumnInfo(name = TABLE_BOOK_OF_USER_AUTHORS)
    val authors: String = "",

    @Expose @SerializedName("libraryCode")
    @ColumnInfo(name = TABLE_BOOK_OF_USER_LIBRARY_CODE)
    val libraryCode: String = "",

    @Expose @SerializedName("libraryName")
    @ColumnInfo(name = TABLE_BOOK_OF_USER_LIBRARY_NAME)
    val libraryName: String = "",

    @Expose @SerializedName("mediaType")
    @ColumnInfo(name = TABLE_BOOK_OF_USER_MEDIA_TYPE)
    val mediaType: String = "",

    @Expose @SerializedName("dueDate")
    @ColumnInfo(name = TABLE_BOOK_OF_USER_DUE_DATE)
    val dueDate: String = "",

    @Expose @SerializedName("returnDate")
    @ColumnInfo(name = TABLE_BOOK_OF_USER_RETURN_DATE)
    val returnDate: String = "",

    @Expose @SerializedName("noRenew")
    @ColumnInfo(name = TABLE_BOOK_OF_USER_NO_RENEW)
    val noRenew: String = "",

) : Parcelable

const val RENEW_ALLOWED = "0"

enum class BookOfUserType {
    LOAN,
    RESERVATION,
    HISTORY,
}
