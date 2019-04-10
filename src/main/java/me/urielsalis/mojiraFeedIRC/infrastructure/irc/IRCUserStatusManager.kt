package me.urielsalis.mojiraFeedIRC.infrastructure.irc

import me.urielsalis.mojiraFeedIRC.domain.UserStatusManager
import nedhyett.Amelia.core.users.User
import nedhyett.Amelia.managers.UserManager
import java.io.File

object IRCUserStatusManager: UserStatusManager {
    val userList = mutableMapOf<String, MutableList<String>>()

    override fun getAllUsers(): List<User> = UserManager.getAllUsers().toList()

    override fun getLoggedInUsers() = userList.map { UserManager.getUser(it.key) }.filterNotNull()

    override fun login(user: String) {
        userList[user] = readIgnoreList(user).toMutableList()
    }

    override fun isLoggedIn(nick: String): Boolean = userList.containsKey(nick)

    override fun logout(user: String) {
        userList.remove(user)
    }

    override fun disconnect(user: User, reason: String) {
        userList.remove(user.nick)
        user.quit(reason)
    }

    override fun isMessageIgnored(user: User, message: String): Boolean =
        userList[user.nick].orEmpty().any { message.matches(it.toRegex()) }

    override fun sendMessage(user: User, author: String, channel: String, message: String) {
        user.sendRaw(author, "PRIVMSG $channel :$message")
    }

    override fun addToIgnoreList(user: User, message: String) {
        userList[user.nick]?.add(message)
        saveIgnoreList(user.nick)
    }

    private fun saveIgnoreList(user: String) {
        val file = File("users/$user")
        file.delete()
        file.createNewFile()
        val printWritter = file.printWriter()
        userList[user].orEmpty().forEach { printWritter.println(it) }
        printWritter.close()
    }

    private fun readIgnoreList(user: String): List<String> {
        val file = File("users/$user")
        if(!file.exists()) {
            return emptyList()
        }
        return file.readLines()
    }

}