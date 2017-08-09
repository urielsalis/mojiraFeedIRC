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
package nedhyett.Amelia.command;

import nedhyett.Amelia.*;
import nedhyett.Amelia.core.users.User;
import nedhyett.Amelia.enums.Replies;
import nedhyett.Amelia.managers.UserManager;
import nedhyett.crimson.logging.CrimsonLog;

/**
 * Handles the USER command.
 *
 * @author Ned
 */
public class CommandUSER implements ICommand {

    @Override
    public void exec(User user, String[] args, String rawmsg) {
	if (user.registeredToServer) {
	    user.sendRawS(Replies.ERR_ALREADYREGISTERED.format(user.nick));
	}
	String username = args[0];
	String mode = args[1];
	String servername = args[2];
	String GECOS = rawmsg.substring(rawmsg.indexOf(':') + 1);
	Amelia.config.bannedGECOS.stream().filter((bannedGECOS) -> (GECOS.matches(bannedGECOS))).map((bannedGECOS) -> {
	    CrimsonLog.warning("User (" + user.getHostAddress() + ") joined with banned GECOS (" + GECOS + ") which matched rule " + bannedGECOS);
	    return bannedGECOS;
	}).forEach((_item) -> {
	    user.quit("Invalid GECOS.");
	});
	user.username = username;
	user.GECOS = GECOS;
	if (UserManager.userExists(user.nick)) {
	    user.sendRawS(Replies.ERR_NICKNAMEINUSE.format(user.nick, user.nick));
	    user.inThread.interrupt();
	    return;
	}
	UserManager.addUser(user.nick, user);
	user.sendRawS(Replies.RPL_WELCOME.format(user.nick, user.nick, user.username, user.getHostAddress()));
	user.sendRawS(Replies.RPL_YOURHOST.format(user.nick, Amelia.config.serverHost, Amelia.getVersion()));
	user.sendRawS(Replies.RPL_CREATED.format(user.nick, Amelia.startupTime.toString()));
	user.sendRawS(Replies.RPL_MYINFO.format(user.nick, Amelia.config.serverHost, Amelia.getVersion(), "-", "-"));
	CommandRegistry.getCommand("MOTD").exec(user, args, rawmsg);
    }

}
