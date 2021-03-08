package com.punpuf.e_usp.vo

data class Publication(
    val sysNo: String,
    val base: String,
    val authors: String,
    val editor: String,
    val idiom: String,
    val mediaType: String, //todo check later if inHouse and other attr not added are actually important
    val publicationDate: String,
    val subjects: String,
    val title: String,
    val locations: List<PublicationLocation>,
)
