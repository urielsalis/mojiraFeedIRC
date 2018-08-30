package me.urielsalis.mojiraFeedIRC

import nedhyett.Amelia.core.users.FakeUser
import nedhyett.Amelia.core.users.User
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
        if (command == "PRIVMSG") {
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
                    if (pars.size == 1) {
                        from.sendNotice(this.id, "Need more parameters.")
                        return
                    }
                    if (IRCServer.userList.containsKey(from.username)) {
                        val string = buildString {
                            append(pars[1])
                            for (i in 2 until pars.size) {
                                append(" ").append(pars[i])
                            }
                        }

                        val feed = IRCServer.userList[from.username]
                        if (feed?.user == from) {
                            feed.addToIgnore(string)
                            Main.save()
                            from.sendNotice(this.id, "Ignored!")
                        } else {
                            from.sendNotice(this.id, "Not logged in.")
                        }
                    } else {
                        from.sendNotice(this.id, "Not logged in.")
                    }
                }
                "LOGIN" -> {
                    if (pars.size < 2) {
                        from.sendNotice(this.id, "Need more parameters.")
                        return
                    }
                    val password = pars[1]
                    if (IRCServer.userList.containsKey(from.username)) {
                        val feed = IRCServer.userList[from.username]
                        if (BCrypt.checkpw(password, feed?.password)) {
                            feed?.user = from
                            from.sendNotice(this.id, "Logged in!")
                        } else {
                            from.sendNotice(this.id, "Invalid Password!")
                        }
                    } else {
                        from.sendNotice(this.id, "User doesnt exists!")
                    }
                }
                "REGISTER" -> {
                    if (pars.size < 2) {
                        from.sendNotice(this.id, "Need more parameters.")
                        return
                    }
                    val password = pars[1]
                    if (!IRCServer.userList.containsKey(from.username)) {
                        val userFeed = UserFeed(from.username, BCrypt.hashpw(password, BCrypt.gensalt()), from)
                        IRCServer.userList[from.username] = userFeed
                        Main.save()
                        from.sendNotice(this.id, "Registered!")
                    } else {
                        from.sendNotice(this.id, "User already exists!")
                    }
                }
                else -> from.sendNotice(this.id, "I don't know what you mean by $text.")
            }
        }
    }
}