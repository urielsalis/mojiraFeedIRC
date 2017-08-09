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
package nedhyett.Amelia.core.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import nedhyett.Amelia.AmeliaThread;
import nedhyett.Amelia.Util;
import nedhyett.Amelia.core.users.User;
import nedhyett.crimson.logging.CrimsonLog;

/**
 *
 * @author Ned
 */
public class ConnectionThread extends AmeliaThread {

    private static final HashMap<String, Long> disallowTemp = new HashMap<>();

    public static void disallow(String addr, int seconds) {
	synchronized (disallowTemp) {
	    CrimsonLog.info("Blocking " + addr + " for 1hr");
	    disallowTemp.put(addr, Util.getMicroTime() + (seconds * 1000));
	}
    }

    /**
     *
     */
    public ConnectionThread() {
	super();
	this.setDaemon(false);
	this.setName("Amelia:ConnectionThread");
    }

    @Override
    public void run() {
	try {
	    ServerSocket ss = new ServerSocket(6667);
	    while (!this.isInterrupted()) {
		Socket s = ss.accept();
		CrimsonLog.info("Connection from " + s.getInetAddress().getHostAddress());
		boolean ignore = false;
		for (String ia : disallowTemp.keySet()) {
		    if (ia.equalsIgnoreCase(s.getInetAddress().getHostAddress())) {
			if (disallowTemp.get(ia) > Util.getMicroTime()) {
			    s.close();
			    ignore = true;
			    break;
			} else {
			    disallowTemp.remove(ia);
			    CrimsonLog.debug("Removing " + ia + " timeout expired.");
			    break;
			}
		    }
		}
		if (ignore) {
		    continue;
		}
		register(s);
	    }
	} catch (IOException e) {
	    if (e.getMessage().contains("Address already in use")) {
		CrimsonLog.fatal("Error: It seems that something is already using port 6667...");
	    } else {
		CrimsonLog.fatal(e);
	    }
	}
    }

    /**
     *
     * @param socket
     */
    public void register(Socket socket) {
	User u = new User(socket);
	u.inThread.start();
    }

}
