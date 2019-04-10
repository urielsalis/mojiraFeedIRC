package me.urielsalis.mojiraFeedIRC.domain

import nedhyett.Amelia.core.users.User

interface UserStatusManager {
    fun getAllUsers(): List<User>
    fun getLoggedInUsers(): List<User>
    fun login(user: String)
    fun logout(user: String)
    fun disconnect(user: User, reason: String)
    fun isMessageIgnored(user: User, message: String): Boolean
    fun addToIgnoreList(user: User, message: String)
    fun sendMessage(user: User, author: String, channel: String, message: String)
    fun isLoggedIn(nick: String): Boolean
}