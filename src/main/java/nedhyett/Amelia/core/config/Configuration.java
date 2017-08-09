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

package nedhyett.Amelia.core.config;

import java.util.ArrayList;
import java.util.HashMap;
import nedhyett.Amelia.core.connection.IPRange;

/**
 * Result of ConfigReader.
 * <br><br>
 * Notice: configuration options and descriptions based on ircd-hybrid, but modified for use
 * in Amelia.
 * 
 * @author Ned
 */
public final class Configuration {
    
    /**
     * Name of the server.
     */
    public String serverHost = "Amelia";
    
    /**
     * Description of the server.
     */
    public String description = "Amelia";
    
    /**
     * Network info: name of the network.
     */
    public String networkName = "Amelia";
    
    /**
     * Network info: description of the network.
     */
    public String networkDescription = "Amelia";
    
    /**
     * Allow this server to act as a hub and have multiple servers connected to it.
     */
    public boolean hub = false;
    
    /**
     * The maximum number of clients allowed to connect.
     * Setting this to -1 disables the limit.
     */
    public int maxClients = 512;
    
    
    /*
     * Channel Settings.
     */
    
    /**
     * Enable/disable the invite exception list.
     */
    public boolean useInvex = true;
    
    /**
     * Enable/disable the ban override list.
     */
    public boolean useExcept = true;
    
    /**
     * Allows users to request an invite to a channel that is locked. (invite-only, key, user limit)
     */
    public boolean useKnock = true;
    
    /**
     * The amount of time a user must wait between issuing the knock command.
     * Setting this to -1 disables the delay.
     */
    public int knockDelay = 300;
    
    /**
     * How often a channel can be "knocked", regardless of the user sending the knock.
     * Setting this to -1 disables the delay.
     */
    public int knockDelayChannel = 60;
    
    /**
     * The maximum number of channels a user can join/be on.
     * Setting this to -1 disables the limit.
     */
    public int maxChansPerUser = 15;
    
    /**
     * When a user is banned, they are not allowed to talk until kicked (they obviously couldn't
     * talk after anyway) or are unbanned.
     */
    public boolean quietOnBan = true;
    
    /**
     * Max number of banned users for a channel.
     * Setting this to -1 disables the limit.
     */
    public int maxBans = 2500;
    
    /**
     * Prevent users from joining channels starting with "&".
     */
    public boolean disableLocalChannels = false;
    
    /**
     * When an operator joins a channel which is +l and is full, kick a random non-opped / non-voiced
     * user to make space.
     */
    public boolean adminJoinsTakePriority = false;
  
    
    /*
     * General Settings
     */
    
    /**
     * The amount of lines a user may send to any other user/channel in one second.
     */
    public int floodCount = 10;
    
    /**
     * Send a NOTICE to all opers on the server when someone tries to OPER and uses the wrong
     * password, host or ident.
     */
    public boolean failedOperNotice = true;
    
    /**
     * Enable the anti-nickflood mode.
     */
    public boolean antiNickFlood = true;
    
    /**
     * The period to limit.
     */
    public int antiNickFloodPeriod = 20;
    
    /**
     * The maximum number of NICK commands in antiNickFloodPeriod.
     */
    public int antiNickFloodChanges = 5;
    
    /**
     * Show the user the reason why they are KLINED/DLINED/GLINED.
     */
    public boolean klineWithReason = true;
    
    /**
     * Make the quit message "Connection closed" instead of KLINE reason.
     */
    public boolean klineWithConnectionClosed = false;
    
    /**
     * Opers don't have a flood limit.
     */
    public boolean noOperFloodLimit = true;
    
    /**
     * Max number of lines in a queue before dropping a client for flooding.
     */
    public int maxClientQueueFlood = 20;
    
    /**
     * Minimum amount of time between connections from the same IP.
     */
    public int throttleTime = 10;
    
    
    /*
     * Admin settings.
     */
    
    /**
     * Admin name.
     */
    public String adminName = "Admin";
    
    /**
     * Admin description.
     */
    public String adminDescription = "Main Server Administrator";
    
    /**
     * Admin email.
     */
    public String adminEmail = "admin@example.com";
    
    
    /*
     * Resource Throttles
     */

    /**
     *
     */
    public int maxChannels = -1;
    
    
    /*
     * Lists
     */
    
    /**
     * HashMap of banned hosts [host] against [reason]
     */
    public HashMap<String, String> bannedHosts = new HashMap<>();
    
    /**
     * HashMap of banned IP ranges [range] against [reason]
     */
    public HashMap<IPRange, String> bannedIPRanges = new HashMap<>();
    
    /**
     * List of nicks that are not allowed to be used.
     */
    public ArrayList<String> reservedNicks = new ArrayList<>();
    
    /**
     * List of GECOS that are not allowed to be used.
     */
    public ArrayList<String> bannedGECOS = new ArrayList<>();
    
    /**
     * List of regexes that a channel cannot match in order to be created.
     */
    public ArrayList<String> bannedChans = new ArrayList<>();
    
    /**
     * Operlist.
     */
    public OperConfig[] opers = new OperConfig[0];
    
    
    /*
     * Services
     */

    /**
     *
     */
    public boolean enableServices = true;

    /**
     *
     */
    public boolean enableNickServ = true;

    /**
     *
     */
    public boolean enableChanServ = true;

    /**
     *
     */
    public boolean enableOperServ = true;

    /**
     *
     */
    public boolean enableStatServ = true;

    /**
     *
     */
    public boolean enableMemoServ = true;
    
    /**
     *
     */
    public Configuration(){
	
    }
    
    /**
     *
     * @param id
     * @return
     */
    public OperConfig getOper(String id){
	for(OperConfig oc : this.opers){
	    if(oc.name.equals(id)){
		return oc;
	    }
	}
	return null;
    }
    
}
