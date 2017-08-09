/*
 *  Amelia
 *  Copyright (c) 2014, Ned Hyett
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import nedhyett.Amelia.Util;
import nedhyett.crimson.database.ABDatabase;

/**
 *
 * @author Ned Hyett
 */
public class ServicesStorage extends ABDatabase {

    public static ArrayList<ServicesUserData> userDatas = new ArrayList<>();

    @Override
    public void save(DataOutputStream out) throws IOException {
	out.writeInt(userDatas.size());
	userDatas.forEach((ServicesUserData ud) -> {
	    try {
		out.writeUTF(ud.accountname);
		Util.saveArrayList(ud.nicks, out);
		out.writeUTF(ud.email);
		Util.saveArrayList(ud.notes, out);
		out.writeBoolean(ud.frozen);
		out.writeUTF(ud.language);
		out.writeUTF(ud.password);
	    } catch (IOException e) {

	    }
	});
    }

    @Override
    public void load(DataInputStream in, int version) throws IOException {
	userDatas.clear();
	int num = in.readInt();
	for (int i = 0; i < num; i++) {
	    ServicesUserData ud = new ServicesUserData();
	    ud.accountname = in.readUTF();
	    ud.nicks = Util.readArrayList(in);
	    ud.email = in.readUTF();
	    ud.notes = Util.readArrayList(in);
	    ud.frozen = in.readBoolean();
	    ud.language = in.readUTF();
	    ud.password = in.readUTF();
	}
    }

    @Override
    public String getFilename() {
	return "db/services.db";
    }

    @Override
    public int getVersion() {
	return 1;
    }

}
