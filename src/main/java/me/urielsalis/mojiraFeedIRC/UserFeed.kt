package me.urielsalis.mojiraFeedIRC

import nedhyett.Amelia.core.users.User
import java.io.Serializable
import java.util.*


data class UserFeed(var username: String, var password: String) : Serializable {
    var ignoreList = mutableListOf<String>()

    fun addToIgnore(str: String) {
        ignoreList.add(str)
    }
}
