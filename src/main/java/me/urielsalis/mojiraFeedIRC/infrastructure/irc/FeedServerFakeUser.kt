package me.urielsalis.mojiraFeedIRC.infrastructure.irc

import me.urielsalis.mojiraFeedIRC.domain.UserStatusManager
import nedhyett.Amelia.core.users.FakeUser
import nedhyett.Amelia.core.users.User
import nedhyett.Amelia.managers.ChannelManager
import org.mindrot.jbcrypt.BCrypt
import java.io.File

class FeedServer(nick: String, username: String, hostmask: String, realname: String, val userStatusManager: UserStatusManager) : FakeUser(nick, username, hostmask, realname) {
    override fun handleInput(from: User, command: String, raw: String) {
        when (command) {
            "JOIN" -> {
                if(from.nick == "FeedServer") {
                    return
                }
                if(userStatusManager.isLoggedIn(from.nick)) {
                    from.sendNotice(this.id, "Welcome to #feed. You are logged in as ${from.nick}")
                } else {
                    val feedChannel = ChannelManager.getChannel("#feed")
                    feedChannel.leave(from, "Need to login to join #feed")
                    from.sendNotice(this.id, "Need to login to join #feed")
                }
            }
            "QUIT" -> {
                userStatusManager.logout(from.nick)
            }
            "PART" -> {
                userStatusManager.logout(from.nick)
            }
            "PRIVMSG" -> {
                val text = raw.substring(raw.indexOf(":") + 1)
                val pars = text
                        .split("\\s".toRegex())
                        .dropLastWhile { it.isEmpty() }

                if (pars.isEmpty()) {
                    from.sendNotice(this.id, "Need more parameters.")
                    return
                }

                when (pars[0].toUpperCase()) {
                    "IGNORE" -> {
                        //IGNORE <regex>
                        if (pars.size == 1) {
                            from.sendNotice(this.id, "Need more parameters.")
                            return
                        }
                        if(!userStatusManager.isLoggedIn(from.nick)) {
                            from.sendNotice(this.id, "Not logged in.")
                            return
                        }
                        val string = buildString {
                            append(pars[1])
                            for (i in 2 until pars.size) {
                                append(" ").append(pars[i])
                            }
                        }
                        userStatusManager.addToIgnoreList(from, string)
                        from.sendNotice(this.id, "Ignored!")
                    }
                    "LOGIN" -> {
                        //LOGIN <password>
                        if (pars.size < 2) {
                            from.sendNotice(this.id, "Need more parameters.")
                            return
                        }
                        val password = pars[1]
                        val file = File("passwords/${from.nick}")
                        if(!file.exists()) {
                            from.sendNotice(this.id, "User doesnt exists!")
                            return
                        }
                        if(BCrypt.checkpw(password, file.readText())) {
                            userStatusManager.login(from.nick)
                            from.sendNotice(this.id, "Logged in!")
                        } else {
                            from.sendNotice(this.id, "Invalid Password!")
                        }
                    }
                    "REGISTER" -> {
                        //REGISTER <password>
                        if (pars.size < 2) {
                            from.sendNotice(this.id, "Need more parameters.")
                            return
                        }
                        val password = pars[1]
                        val file = File("passwords/${from.nick}")
                        if(file.exists()) {
                            from.sendNotice(this.id, "User already exists!")
                            return
                        }
                        file.createNewFile()
                        file.writeText(BCrypt.hashpw(password, BCrypt.gensalt()))
                        from.sendNotice(this.id, "Registered! Please login")
                    }
                    else -> from.sendNotice(this.id, "I don't know what you mean by $text.")
                }
            }
        }
    }
}