package com.punpuf.e_usp.vo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BookOfSearchLocation(
    
    @Expose @SerializedName("sysNo")
    val sysNo: String,
    
    @Expose @SerializedName("base")
    val base: String,
    
    @Expose @SerializedName("barCode")
    val barcode: String,
    
    @Expose @SerializedName("calling")
    val calling: String,

    @Expose @SerializedName("libraryCampus")
    val libraryCampusName: String,
    
    @Expose @SerializedName("libraryCampusCode")
    val libraryCampusCode: String,
    
    @Expose @SerializedName("libraryCode")
    val libraryName: String,
    
    @Expose @SerializedName("libraryName")
    val libraryCode: String,
    
    @Expose @SerializedName("status")
    val statusMsg: String,
    
    @Expose @SerializedName("statusCode")
    val statusCode: Int,
)