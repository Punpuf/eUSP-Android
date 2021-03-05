package com.punpuf.e_usp.util

import com.punpuf.e_usp.vo.BookUser

interface BookUserClickInterface {
    fun notifyBookUserClicked(bookUser: BookUser)
}