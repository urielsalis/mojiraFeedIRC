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

import nedhyett.Amelia.Amelia;
import nedhyett.Amelia.ICommand;
import nedhyett.Amelia.Util;
import nedhyett.Amelia.core.config.OperConfig;
import nedhyett.Amelia.core.users.User;
import nedhyett.Amelia.enums.Replies;

/**
 * Processes a request from the user to authenticate as an IRCOp
 *
 * @author Ned
 */
public class CommandOPER implements ICommand {

    @Override
    public void exec(User user, String[] args, String rawmsg) {
	if (args.length < 2) {
	    user.sendRawS(Replies.ERR_NEEDMOREPARAMS.format(user.nick, "OPER", "<sername> <password>"));
	    return;
	}
	if (user.isOper) {
	    user.sendRawS(Replies.RPL_YOUREOPER + " " + user.nick + " :You are already OPER!");
	    return;
	}
	String provpass = Util.digestString(args[1]);
	for (OperConfig oc : Amelia.config.opers) {
	    if (oc.name.equals(args[0])) {
		if (oc.passwordMD5.equals(provpass)) {
		    user.sendRawS(Replies.RPL_YOUREOPER.format(user.nick));
		    user.operID = args[0];
		    user.isOper = true;
		    return;
		} else {
		    user.sendRawS(Replies.ERR_PASSWDMISMATCH.format(user.nick));
		    return;
		}
	    }
	}
	//User failed.
	user.sendRawS(Replies.ERR_NOOPERHOST.format(user.nick));
    }

}
