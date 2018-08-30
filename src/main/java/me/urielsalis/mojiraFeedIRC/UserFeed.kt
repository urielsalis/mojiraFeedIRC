package me.urielsalis.mojiraFeedIRC

import nedhyett.Amelia.core.users.User
import java.io.Serializable
import java.util.*


data class UserFeed(var username: String, var password: String, @field:Transient var user: User?) : Serializable {
    var ignoreList = ArrayList<String>()

    fun addToIgnore(str: String) {
        ignoreList.add(str)
    }
}
