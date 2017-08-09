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
package nedhyett.Amelia.core.users;

import nedhyett.Amelia.managers.UserManager;

/**
 * Represents a user that is not connected via a socket.
 *
 * @author Ned
 */
public abstract class FakeUser extends User {

    private final String hostmask;

    /**
     * Create a new FakeUser
     *
     * @param nick     Nickname to give this user
     * @param username The username to give this user
     * @param hostmask The hostmask to give this user
     * @param realname The realname to give this user
     */
    public FakeUser(String nick, String username, String hostmask, String realname) {
	super();
	this.nick = nick;
	this.username = username;
	this.GECOS = realname;
	this.hostmask = hostmask;
    }

    @Override
    public String getHostAddress() {
	return this.hostmask;
    }

    @Override
    public void sendRaw(String origin, String str) {
	User u = UserManager.getFromID(origin);
	String command;
	if (u != null) {
	    command = str.substring(0, str.indexOf(" "));
	    handleInput(u, command, str);
	} else {
	    //Do something?
	}
    }

    /**
     * Called when someone tries to direct a command towards this user.
     *
     * @param from    The user that sent this message
     * @param command The command that they sent
     * @param raw     The full raw message (excluding origin)
     */
    public abstract void handleInput(User from, String command, String raw);

    @Override
    public void quit(String message) {
	//Can't quit.
    }

}
