package me.urielsalis.mojiraFeedIRC

import nedhyett.Amelia.core.users.FakeUser
import nedhyett.Amelia.core.users.User
import nedhyett.Amelia.managers.ChannelManager
import nedhyett.Amelia.managers.UserManager
import org.mindrot.jbcrypt.BCrypt

/**
 * Create a new FakeUser
 *
 * @param nick     Nickname to give this user
 * @param username The username to give this user
 * @param hostmask The hostmask to give this user
 * @param realname The realname to give this user
 */
class FeedServer(nick: String, username: String, hostmask: String, realname: String) : FakeUser(nick, username, hostmask, realname) {

    override fun handleInput(from: User, command: String, raw: String) {
        when (command) {
            "JOIN" -> {
                if (from != this) {
                    if (!IRCServer.userList.containsKey(from.nick)) {
                        val feedChannel = ChannelManager.getChannel("#feed")
                        feedChannel.leave(from, "Need to login to join #feed")
                        from.sendNotice(this.id, "Need to login to join #feed")
                    } else {
                        from.sendNotice(this.id, "Welcome to #feed. You are logged in as ${from.nick}")
                    }
                }
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
                        if (IRCServer.userList.containsKey(from.nick)) {
                            val string = buildString {
                                append(pars[1])
                                for (i in 2 until pars.size) {
                                    append(" ").append(pars[i])
                                }
                            }

                            val feed = IRCServer.userList[from.nick]
                            feed?.addToIgnore(string)
                            Main.save()
                            from.sendNotice(this.id, "Ignored!")
                        } else {
                            from.sendNotice(this.id, "Not logged in.")
                        }
                    }
                    "LOGIN" -> {
                        //LOGIN <password>
                        if (pars.size < 2) {
                            from.sendNotice(this.id, "Need more parameters.")
                            return
                        }
                        val password = pars[1]
                        val feedChannel = ChannelManager.getChannel("#feed")
                        if (IRCServer.userList.containsKey(from.nick)) {
                            val feed = IRCServer.userList[from.nick]
                            if (BCrypt.checkpw(password, feed?.password)) {
                                from.sendNotice(this.id, "Logged in!")
                                feedChannel.join(from)
                                feedChannel.voice(from)

                            } else {
                                from.sendNotice(this.id, "Invalid Password!")
                                feedChannel.devoice(from)
                            }
                        } else {
                            from.sendNotice(this.id, "User doesnt exists!")
                        }
                    }
                    "REGISTER" -> {
                        //REGISTER <password>
                        if (pars.size < 2) {
                            from.sendNotice(this.id, "Need more parameters.")
                            return
                        }
                        val password = pars[1]
                        if (!IRCServer.userList.containsKey(from.nick)) {
                            val userFeed = UserFeed(from.nick, BCrypt.hashpw(password, BCrypt.gensalt()))
                            IRCServer.userList[from.nick] = userFeed
                            Main.save()
                            from.sendNotice(this.id, "Registered! Please login")
                        } else {
                            from.sendNotice(this.id, "User already exists!")
                        }
                    }
                    else -> from.sendNotice(this.id, "I don't know what you mean by $text.")
                }
            }
        }
    }
}