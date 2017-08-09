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
package nedhyett.Amelia.services;

import nedhyett.Amelia.core.users.User;

/**
 * NickServ implementation.
 *
 * @author Ned
 */
public class NickServ extends ServicesUser {

    public NickServ() {
		super("NickServ", "NickServ", "Urielsalis", "Urielsalis IRC Services", "nickserv.hlp");
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
			case "HELP":
				this.printHelp(text, from);
				break;
			case "DROP":
				break;
			case "FDROP":
				break;
			case "FREEZE":
				break;
			case "GHOST":
				break;
			case "GROUP":
				break;
			case "HOLIDAY":
				break;
			case "IDENTIFY":
				break;
			case "INFO":
				break;
			case "LIST":
				break;
			case "LISTCHANS":
				break;
			case "LISTMAIL":
				break;
			case "LOGOUT":
				break;
			case "MARK":
				break;
			case "REGISTER":
				break;
			case "RESETPASSWD":
				break;
			case "RESTRICT":
				break;
			case "RETURN":
				break;
			case "SENDPASSWD":
				break;
			case "SET":
				break;
			case "SETPASSWD":
				break;
			case "STATUS":
				break;
			case "TAXONOMY":
				break;
			case "UNGROUP":
				break;
			case "VERIFY":
				break;

			default:
				from.sendNotice(this.getID(), "I don't know what you mean by " + text + ".");
				break;
			}
		}
    }

}
