package com.punpuf.e_bandejao.util

import com.punpuf.e_bandejao.vo.BookUser

interface BookUserClickInterface {
    fun notifyBookUserClicked(bookUser: BookUser)
}