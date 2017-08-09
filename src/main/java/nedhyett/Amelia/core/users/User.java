/*
 *
 *
 * Copyright (c) 2014, Ned Hyett
 * All rights reserved.
 *
 * By using this program/package/library you agree to be completely and unconditionally
 * bound by the agreement displayed below. Any deviation from this agreement will not
 * be tolerated.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. No part of this text may be modified
 *    by anyone other than the original copyright holder.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this
 *    list of conditions and the following disclaimer in the documentation and/or other
 *    materials provided with the distribution.
 * 3. The redistribution is not sold, unless permission is granted from the copyright holder.
 * 4. The redistribution must contain reference to the original author and provide a
 *    link (or other means) to aquire the original source code from the original copyright holder.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 */
package nedhyett.Amelia.core.users;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import nedhyett.Amelia.Amelia;
import nedhyett.Amelia.Util;
import nedhyett.Amelia.core.connection.InputThread;
import nedhyett.Amelia.managers.ChannelManager;
import nedhyett.Amelia.managers.UserManager;
import nedhyett.crimson.logging.CrimsonLog;

/**
 * Represents a user connected via a socket.
 *
 * @author Ned
 */
public class User {

    /**
     * The input thread for this user.
     */
    public final InputThread inThread;

    /**
     * The socket for this user.
     */
    public final Socket socket;

    /**
     * The username for this user.
     */
    public String username;

    /**
     * The unique nickname for this user.
     */
    public String nick;

    /**
     * The GECOS for this user.
     */
    public String GECOS;

    /**
     * Has the USER command been sent yet?
     */
    public boolean registeredToServer = false;

    /**
     * Has this user identified by using OPER?
     */
    public boolean isOper = false;

    /**
     * The operator name that this user has OPER'd as. Blank if not OPER'd.
     */
    public String operID = "";

    /**
     * Did this user respond to the last ping message or do we need to kick them?
     */
    public boolean respondedToLastPing = true;

    /**
     * The time of the last PRIVMSG.
     */
    public long lastAction = 0L;

    /**
     * When the connection was opened.
     */
    public final long connectionOpened;

    /**
     * Create a blank user (used for FakeUser)
     */
    protected User() {
	inThread = null;
	socket = null;
	connectionOpened = Util.getMicroTime();
    }

    /**
     * Create a new user based around a socket.
     *
     * @param socket
     */
    public User(Socket socket) {
	InputStream inStream = null;
	try {
	    inStream = socket.getInputStream();
	} catch (IOException e) {
	    CrimsonLog.severe("Error getting input stream for user");
	}
	this.inThread = new InputThread(this, inStream);
	this.socket = socket;
	connectionOpened = Util.getMicroTime();
    }

    /**
     * Get the client identifier (or hostmask) for this user.
     *
     * @return
     */
    public String getID() {
	return nick + "!" + username + "@" + this.getHostAddress();
    }

    /**
     * Get the host address (override to prevent NPEs on FakeUsers)
     *
     * @return
     */
    public String getHostAddress() {
	return this.socket.getInetAddress().getHostAddress();
    }

    /**
     * Send a raw string to this user. (automatically appends the \r\n to the end of the line)
     *
     * @param origin
     * @param str
     */
    public void sendRaw(String origin, String str) {
	if (this.socket.isClosed()) {
	    CrimsonLog.warning("Error: cannot send text to user " + this.nick + ". Socket closed.");
	    return;
	}
	if (this.socket.isOutputShutdown()) {
	    CrimsonLog.warning("Error: cannot send text to user " + this.nick + ". Output stream closed.");
	    return;
	}
	try {
	    if (origin == null) {
			CrimsonLog.debug("Sending " + str + " (to " + this.getID() + ")");
			this.socket.getOutputStream().write((str + "\r\n").getBytes());
			//this.bytesSent += (str + "\r\n").getBytes().length;
			return;
	    }
	    CrimsonLog.debug("Sending " + ":" + origin + " " + str + " (to " + this.getID() + ")");
	    this.socket.getOutputStream().write((":" + origin + " " + str + "\r\n").getBytes());
	    //this.bytesSent += (":" + origin + " " + str + "\r\n").getBytes().length;
	} catch (IOException ex) {
	    this.inThread.interrupt();
	    ChannelManager.getAllChannelsWithUser(this).stream().map((c) -> {
		c.sendRawExcept(this.getID(), "QUIT :" + ex.getMessage(), this);
		return c;
	    }).forEach((c) -> {
		c.leaveNoAnnounce(this);
	    });
	    UserManager.dropUser(this.nick);
	}
    }

    /**
     * Send a raw string to this user from the Server Host.
     *
     * @param str
     */
    public void sendRawS(String str) {
	this.sendRaw(Amelia.config.serverHost, str);
    }

    /**
     * Send a PRIVMSG to this user.
     *
     * @param origin
     * @param message
     */
    public void sendMessage(String origin, String message) {
	sendRaw(origin, "PRIVMSG " + this.nick + " :" + message);
    }

    /**
     * Send a NOTICE to this user.
     *
     * @param origin
     * @param message
     */
    public void sendNotice(String origin, String message) {
	sendRaw(origin, "NOTICE " + this.nick + " :" + message);
    }

    /**
     * Broadcast a QUIT message with the specified reason, close the connection and drop the user
     * from the UserManager.
     *
     * @param message
     */
    public void quit(String message) {
	ChannelManager.getAllChannelsWithUser(this).stream().map((c) -> {
	    c.sendRawExcept(this.getID(), "QUIT :" + message, this);
	    return c;
	}).forEach((c) -> {
	    c.leaveNoAnnounce(this);
	});
	this.inThread.interrupt();
	UserManager.dropUser(this.nick);
    }

}
