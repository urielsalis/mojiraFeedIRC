/*
 * Copyright (c) 2014, Ned Hyett
 *  All rights reserved.
 * 
 *  By using this program/package/library you agree to be completely and unconditionally
 *  bound by the agreement displayed below. Any deviation from this agreement will not
 *  be tolerated.
 * 
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 * 
 *  1. Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this
 *  list of conditions and the following disclaimer in the documentation and/or other
 *  materials provided with the distribution.
 *  3. The redistribution is not sold, unless permission is granted from the copyright holder.
 *  4. The redistribution must contain reference to the original author, and this page.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package nedhyett.Amelia.managers;

import java.util.ArrayList;
import java.util.HashMap;
import nedhyett.Amelia.Amelia;
import nedhyett.Amelia.core.users.FakeUser;
import nedhyett.Amelia.core.users.User;
import nedhyett.crimson.logging.CrimsonLog;

/**
 * Manages users
 *
 * @author Ned
 */
public class UserManager {

    /**
     * List of banned nicks (usually those reserved by the protocol or services)
     */
    public static final String[] bannedNicks = {
	"anonymous", "nickserv", "chanserv", "memoserv", "statsserv", "operserv"
    };

    /**
     * List of active users.
     */
    private static final HashMap<String, User> users = new HashMap<>();

    /**
     * List of active fake users.
     */
    private static final HashMap<String, FakeUser> fakeUsers = new HashMap<>();

    /**
     * Get a user instance.
     *
     * @param nick
     *
     * @return
     */
    public static User getUser(String nick) {
	return getUserCaseInsensitive(nick);
    }

    private static User getUserCaseInsensitive(String nick) {
	if (fakeUserExists(nick)) {
	    for (String s : fakeUsers.keySet()) {
		if (s.equalsIgnoreCase(nick)) {
		    return fakeUsers.get(s);
		}
	    }
	} else {
	    for (String s : users.keySet()) {
		if (s.equalsIgnoreCase(nick)) {
		    return users.get(s);
		}
	    }
	}
	return null;
    }

    /**
     * Add a new user.
     *
     * @param nick
     * @param user
     */
    public static void addUser(String nick, User user) {
	if (!isNickValid(nick)) {
	    user.quit("Bad nickname (" + nick + ")");
	    return;
	}
	users.put(nick, user);
    }

    /**
     * Add a new fake user.
     *
     * @param nick
     * @param user
     */
    public static void addFakeUser(String nick, FakeUser user) {
	if (!isNickValid(nick, true)) {
	    user.quit("Bad nickname (" + nick + ")");
	    return;
	}
	CrimsonLog.debug("Registering FakeUser " + nick);
	fakeUsers.put(nick, user);
    }

    /**
     * Drop a user. Doesn't drop fake users.
     *
     * @param nick
     */
    public static void dropUser(String nick) {
	for (User u : users.values()) {
	    if (u.nick.equalsIgnoreCase(nick)) {
		users.remove(u.nick);
	    }
	}
    }

    /**
     * Check if a user exists.
     *
     * @param nick
     *
     * @return
     */
    public static boolean userExists(String nick) {
	if (fakeUserExists(nick)) {
	    return true;
	}
	for (User u : users.values()) {
	    if (u.nick.equalsIgnoreCase(nick)) {
		return true;
	    }
	}
	return false;
    }

    public static boolean fakeUserExists(String nick) {
	for (FakeUser u : fakeUsers.values()) {
	    if (u.nick.equalsIgnoreCase(nick)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Get a copy of the users list (excluding fake users)
     *
     * @return
     */
    public static ArrayList<User> getAllUsers() {
	return new ArrayList<>(users.values());
    }

    /**
     * Get the number of registered users.
     *
     * @return
     */
    public static int countUsers() {
	return users.size();
    }

    /**
     * Checks if the given nick is valid. If ignoreReserved is true, we will not return false
     * if the nick matches bannedNicks or config.reservedNicks.
     *
     * @param nick
     * @param ignoreReserved
     *
     * @return
     */
    public static boolean isNickValid(String nick, boolean ignoreReserved) {
	if (nick == null) {
	    return false;
	}
	if (!ignoreReserved) {
	    for (String s : bannedNicks) {
		if (nick.equalsIgnoreCase(s)) {
		    return false;
		}
	    }
	    if (!Amelia.config.reservedNicks.stream().noneMatch((s) -> (nick.equalsIgnoreCase(s)))) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Checks if the given nick is valid.
     *
     * @param nick
     *
     * @return
     */
    public static boolean isNickValid(String nick) {
	return isNickValid(nick, false);
    }

    /**
     *
     * @param id
     *
     * @return
     */
    public static User getFromID(String id) {
	for (User u : users.values()) {
	    if (u.getID().equals(id)) {
		return u;
	    }
	}
	for (User u : fakeUsers.values()) {
	    if (u.getID().equals(id)) {
		return u;
	    }
	}
	return null;
    }

}
