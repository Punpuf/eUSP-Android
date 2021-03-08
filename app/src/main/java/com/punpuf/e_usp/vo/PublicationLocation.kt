package com.punpuf.e_usp.vo

data class PublicationLocation(
    val base: String,
    val sysNo: String,
    val barcode: String,
    val calling: String,
    
    val libraryCampusName: String,
    val libraryCampusCode: String,
    val libraryName: String,
    val libraryCode: String,
    
    val statusMsg: String,
    val statusCode: Int,
)