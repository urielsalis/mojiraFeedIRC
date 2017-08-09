/*
 *  Copyright (c) 2014, Ned Hyett
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
 */
package nedhyett.Amelia;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import nedhyett.Amelia.core.config.ConfigReader;
import nedhyett.Amelia.core.config.Configuration;
import nedhyett.Amelia.core.connection.ConnectionThread;
import nedhyett.Amelia.managers.ChannelManager;
import nedhyett.Amelia.managers.ThreadManager;
import nedhyett.Amelia.managers.UserManager;
import nedhyett.Amelia.ping.PingPongThread;
import nedhyett.Amelia.services.NickServ;
import nedhyett.crimson.logging.CrimsonLog;

/**
 * Main File
 *
 * @author Ned
 */
public class Amelia {

    /*
     * Todo: - See CommandRegistry - See CommandMODE - Implement ADMIN - Implement DIE - Add server
     * linkage. - Implement RPL_ISUPPORT -
     */
    public static Configuration config = null;

    public static final ArrayList<String> MOTD = new ArrayList<>();

    /**
     * The Date object that represents the time the server started.
     */
    public static Date startupTime;

    /**
     * Incremented each time deprecated code is removed or major API changes.
     */
    public static final int version_major = 2;

    /**
     * Incremented each time a new IRC spec is released (to match number of spec revisions)
     */
    public static final int version_minor = 3; //Begin support for IRCv3 (http://ircv3.org)

    /**
     * Incremented each time an interface changes or a new major feature is added, reset for every
     * new IRC spec.
     */
    public static final int version_build = 7;

    /**
     * Incremented for each commit made to the repo. (number is equal to commit number)
     */
    public static final int version_revision = 20;

    public static String[] cmdargs;

    /**
     * Startup function
     *
     * @param args
     */
    public static void main(String[] args) {
	CrimsonLog.initialise("Amelia");
	cmdargs = args;

	CrimsonLog.info("Loading configuration...");
	ConfigReader cr = new ConfigReader("config.txt");
	cr.read();
	config = cr.parse();
	CrimsonLog.info("Configuration loaded!");

	CrimsonLog.info("Loading MOTD...");
	File motdf = new File("motd.conf");
	if (motdf.exists()) {
	    try {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(motdf)));
		String line = br.readLine();
		while (line != null) {
		    MOTD.add(line);
		    line = br.readLine();
		}
		CrimsonLog.info("MOTD loaded!");
	    } catch (IOException e) {
		MOTD.clear();
		CrimsonLog.warning("IOException while loading MOTD!");
	    }
	} else {
	    CrimsonLog.warning("motd.conf not found!");
	}

	CrimsonLog.info("Starting connection thread...");
	ConnectionThread ct = new ConnectionThread();
	ct.start();
	CrimsonLog.info("Connection thread started!");

	CrimsonLog.info("Starting PingPong thread...");
	PingPongThread ppt = new PingPongThread();
	ppt.start();
	CrimsonLog.info("PingPong thread started!");

	UserManager.addFakeUser("NickServ", new NickServ());

	Amelia.startupTime = new Date();
    }

    /**
     * Get the version string.
     *
     * @return
     */
    public static String getVersion() {
	return version_major + "." + version_minor + "." + version_build + "." + version_revision;
    }

    /**
     * Get the MOTD line list.
     *
     * @return
     */
    public static ArrayList<String> getMOTD() {
	return MOTD;
    }

    /**
     * Shuts down and restarts Amelia.
     */
    public static void restart() {
	//Stop the connection thread letting more users in.
	ThreadManager.interruptAllThreads();
	UserManager.getAllUsers().stream().map((u) -> {
	    u.sendRaw(Amelia.config.serverHost, "NOTICE " + u.nick + " :ERROR from " + Amelia.config.serverHost + " -- Server shutting down.");
	    return u;
	}).forEach((u) -> {
	    u.quit(Amelia.config.serverHost + " is dropping all users for restart.");
	});
	ChannelManager.getAllChannels().stream().forEach((c) -> {
	    ChannelManager.closeChannel(c.name);
	});
	MOTD.clear();
	main(cmdargs); //Call the MAIN again. Due to threaded nature, this shouldn't be an issue, right?
    }

}
