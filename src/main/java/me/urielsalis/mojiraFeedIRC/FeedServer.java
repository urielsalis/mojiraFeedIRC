package me.urielsalis.mojiraFeedIRC;

import nedhyett.Amelia.core.users.FakeUser;
import nedhyett.Amelia.core.users.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Arrays;

/**
 * mojiraFeedIRC
 * <p>
 * Created by urielsalis on 09/08/17.
 */
public class FeedServer extends FakeUser {
    /**
     * Create a new FakeUser
     *
     * @param nick     Nickname to give this user
     * @param username The username to give this user
     * @param hostmask The hostmask to give this user
     * @param realname The realname to give this user
     */
    public FeedServer(String nick, String username, String hostmask, String realname) {
        super(nick, username, hostmask, realname);
    }

    @Override
    public void handleInput(User from, String command, String raw) {
        if (command.equals("PRIVMSG")) {
            String text = raw.substring(raw.indexOf(":") + 1);
            String[] pars = text.split(" ");
            if (pars.length == 0) {
                from.sendNotice(this.getID(), "Need more parameters.");
                return;
            }
            switch (pars[0].toUpperCase()) {
                case "IGNORE":
                    if(pars.length == 1) {
                        from.sendNotice(this.getID(), "Need more parameters.");
                        return;
                    }
                    if(IRCServer.userList.containsKey(from.username)) {
                        StringBuffer sbf = new StringBuffer();
                        sbf.append(pars[1]);
                        for(int i=2; i < pars.length; i++){
                            sbf.append(" ").append(pars[i]);
                        }

                        UserFeed feed = IRCServer.userList.get(from.username);
                        if(feed.user.equals(from)) {
                            feed.addToIgnore(sbf.toString());
                            Main.save();
                            from.sendNotice(this.getID(), "Ignored!");
                        } else {
                            from.sendNotice(this.getID(), "Not logged in.");
                        }
                    } else {
                        from.sendNotice(this.getID(), "Not logged in.");
                    }
                    break;
                case "LOGIN": {
                    if(pars.length<2) {
                        from.sendNotice(this.getID(), "Need more parameters.");
                        return;
                    }
                    String password = pars[1];
                    if(IRCServer.userList.containsKey(from.username)) {
                        UserFeed feed = IRCServer.userList.get(from.username);
                        if(BCrypt.checkpw(password, feed.password)) {
                            feed.user = from;
                            from.sendNotice(this.getID(), "Logged in!");
                        } else {
                            from.sendNotice(this.getID(), "Invalid Password!");
                        }
                    } else {
                        from.sendNotice(this.getID(), "User doesnt exists!");
                    }
                    break;}
                case "REGISTER":{
                    if(pars.length<2) {
                        from.sendNotice(this.getID(), "Need more parameters.");
                        return;
                    }
                    String password = pars[1];
                    if(!IRCServer.userList.containsKey(from.username)) {
                        UserFeed userFeed = new UserFeed(from.username, BCrypt.hashpw(password, BCrypt.gensalt()), from);
                        IRCServer.userList.put(from.username, userFeed);
                        Main.save();
                        from.sendNotice(this.getID(), "Registered!");
                    } else {
                        from.sendNotice(this.getID(), "User already exists!");
                    }
                    break;}
                default:
                    from.sendNotice(this.getID(), "I don't know what you mean by " + text + ".");
                    break;
            }
        }
    }
}
