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

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import java.io.InputStreamReader;
import nedhyett.Amelia.core.users.FakeUser;
import nedhyett.Amelia.core.users.User;

/**
 *
 * @author Ned
 */
public abstract class ServicesUser extends FakeUser {

    /**
     * Userdata storage object.
     */
    protected static final ServicesStorage storage = new ServicesStorage();

    protected JsonObject helpFile;

    public ServicesUser(String nick, String username, String hostmask, String realname, String helppath) {
	super(nick, username, hostmask, realname);
	try {
	    helpFile = (new JsonParser().parse(new JsonReader(new InputStreamReader(NickServ.class.getResourceAsStream(helppath))))).getAsJsonObject();
	} catch (JsonIOException | JsonSyntaxException e) {
	    e.printStackTrace();
	}
    }

    /**
     * Prints the help text to the user in a series of notices.
     *
     * @param text
     * @param from
     */
    public void printHelp(String text, User from) {
	String[] pars = text.split(" ");
	if (pars.length == 1) {
	    pars = new String[]{"help", "help"};
	}
	if (!helpFile.has(pars[1].toLowerCase())) {
	    from.sendNotice(this.getID(), "No help for " + text.substring(text.indexOf(" ") + 1));
	    return;
	}
	JsonElement je = helpFile.get(pars[1].toLowerCase());
	for (int i = 2; i < pars.length; i++) {
	    if (!je.getAsJsonObject().has(pars[i].toLowerCase())) {
		from.sendNotice(this.getID(), "No help for " + text.substring(text.indexOf(" ") + 1));
		return;
	    }
	    je = je.getAsJsonObject().get(pars[i].toLowerCase());
	}
	if (je.getAsJsonObject().get("operonly").getAsBoolean() && !from.isOper) {
	    from.sendNotice(this.getID(), "Cannot display help: you are not oper!");
	    return;
	}
	if (from.isOper && je.getAsJsonObject().has("operContent")) {
	    for (JsonElement jael : je.getAsJsonObject().get("operContent").getAsJsonArray()) {
		from.sendNotice(this.getID(), jael.getAsString());
	    }
	}
	for (JsonElement jael : je.getAsJsonObject().get("content").getAsJsonArray()) {
	    from.sendNotice(this.getID(), jael.getAsString());
	}
    }

}
