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

import java.util.Date;
import nedhyett.Amelia.Amelia;
import nedhyett.Amelia.ICommand;
import nedhyett.Amelia.Util;
import nedhyett.Amelia.core.users.User;
import nedhyett.Amelia.enums.Replies;
import nedhyett.Amelia.managers.UserManager;

/**
 * Handles the STATS command. (not very well however)
 *
 * @author Ned
 */
public class CommandSTATS implements ICommand {

    @Override
    public void exec(User user, String[] args, String rawmsg) {
	if (args.length < 1) {
	    user.sendRawS(Replies.RPL_ENDOFSTATS.format(user.nick, ""));
	    return;
	}
	for (int i = 0; i < args[0].length(); i++) {
	    switch (Character.toString(args[0].charAt(i))) {
		case "u":
		    Date now = new Date();
		    long[] diff = Util.getElapsed(Amelia.startupTime, now);
		    String[] difftext = new String[diff.length];
		    difftext[0] = Long.toString(diff[0]);
		    if (diff[1] < 10) {
			difftext[1] = "0" + Long.toString(diff[1]);
		    } else {
			difftext[1] = Long.toString(diff[1]);
		    }
		    if (diff[2] < 10) {
			difftext[2] = "0" + Long.toString(diff[2]);
		    } else {
			difftext[2] = Long.toString(diff[2]);
		    }
		    if (diff[3] < 10) {
			difftext[3] = "0" + Long.toString(diff[3]);
		    } else {
			difftext[3] = Long.toString(diff[3]);
		    }

		    user.sendRawS(Replies.RPL_STATSUPTIME.format(user.nick, difftext[0], difftext[1], difftext[2], difftext[3]));
		    break;
		case "l":
		    for (User u : UserManager.getAllUsers()) {
			//user.sendRawS(Replies.RPL_STATSLINKINFO.format(user.nick, u.getID(), "-1", "-1", u.getKbWritten() + "", "-1", u.getKbRead() + "", (Util.getMicroTime() - u.connectionOpened) + ""));
		    }
		    break;
	    }
	}
    }

}
