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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.*;
import java.util.ArrayList;
import java.util.Map.Entry;
import nedhyett.Amelia.core.connection.IPRange;

/**
 * Reads and parses the configuration file.
 *
 * @author Ned
 */
public class ConfigReader {

    private final File configFile;

    private boolean valid = false;

    private boolean read = false;

    private final Configuration config = new Configuration();

    private JsonObject el;

    /**
     * Create a new config reader.
     *
     * @param path
     */
    public ConfigReader(String path) {
	this.configFile = new File(path);
	if (this.configFile.exists()) {
	    this.valid = true;
	}
    }

    /**
     * Read the config file from disk and parse it into a JsonObject (not readable yet).
     */
    public void read() {
	if (!this.valid) {
	    throw new IllegalStateException("Invalid config file");
	}
	try {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    BufferedReader br = new BufferedReader(new FileReader(this.configFile));
	    String line = br.readLine();
	    while (line != null) {
		if (!line.trim().startsWith("#")) {
		    baos.write(line.getBytes());
		}
		line = br.readLine();
	    }
	    JsonReader jr = new JsonReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));
	    JsonParser jp = new JsonParser();
	    this.el = jp.parse(jr).getAsJsonObject();
	    this.read = true;
	} catch (FileNotFoundException ex) {

	} catch (IOException ex) {

	}
    }

    private int getInt(JsonObject obj, String id, int def) {
	if (!obj.has(id)) {
	    return def;
	}
	return obj.get(id).getAsInt();
    }

    private boolean getBoolean(JsonObject obj, String id, boolean def) {
	if (!obj.has(id)) {
	    return def;
	}
	return obj.get(id).getAsBoolean();
    }

    private String getString(JsonObject obj, String id, String def) {
	if (!obj.has(id)) {
	    return def;
	}
	return obj.get(id).getAsString();
    }

    /**
     * Parse the configuration file into a configuration object.
     *
     * @return
     */
    public Configuration parse() {
	if (!this.read) {
	    return this.config; //Just give them a blank config.
	}
	parseServerData();
	parseAdminInfo();
	parseOpers();
	parseBannedIPRanges();
	parseReservedNicks();
	parseBannedGECOS();
	parseChannelOpts();
	parseGeneral();
	return this.config;
    }

    private void parseServerData() {
	if (!el.has("serverdata")) {
	    throw new RuntimeException("Can't have configuration file without 'serverdata' tag!");
	}
	JsonObject sd = el.get("serverdata").getAsJsonObject();
	config.serverHost = getString(sd, "name", "Amelia");
	config.description = getString(sd, "description", "Amelia");
	config.networkName = getString(sd, "networkName", "Amelia");
	config.networkDescription = getString(sd, "networkDesc", "Amelia");
	config.hub = getBoolean(sd, "hub", false);
	config.maxClients = getInt(sd, "maxClients", 512);
    }

    private void parseAdminInfo() {
	if (el.has("admin")) {
	    JsonObject ad = el.get("admin").getAsJsonObject();
	    config.adminName = getString(ad, "name", "Admin");
	    config.adminDescription = getString(ad, "description", "Main Server Administrator");
	    config.adminEmail = getString(ad, "email", "admin@example.com");
	}
    }

    private void parseOpers() {
	if (el.has("opers")) {
	    JsonObject opers = el.get("opers").getAsJsonObject();
	    config.opers = new OperConfig[opers.entrySet().size()];
	    int i = 0;
	    for (Entry<String, JsonElement> s : opers.entrySet()) {
		JsonObject operObj = s.getValue().getAsJsonObject();
		ArrayList<String> hostRegexes = new ArrayList<>();
		if (operObj.has("hosts")) {
		    JsonArray hostRegexesIn = operObj.get("hosts").getAsJsonArray();
		    for (JsonElement hr : hostRegexesIn) {
			hostRegexes.add(hr.getAsString());
		    }
		}
		JsonObject permObj = operObj.get("permissions").getAsJsonObject();
		OperConfig oper = new OperConfig(s.getKey(), hostRegexes,
			getString(operObj, "password", ""),
			getBoolean(permObj, "globalKill", false),
			getBoolean(permObj, "remote", false),
			getBoolean(permObj, "kline", false),
			getBoolean(permObj, "unkline", false),
			getBoolean(permObj, "gline", false),
			getBoolean(permObj, "nickchanges", false),
			getBoolean(permObj, "rehash", false),
			getBoolean(permObj, "die", false),
			getBoolean(permObj, "admin", false));
		config.opers[i] = oper;
		i++;
	    }
	}
    }

    private void parseBannedIPRanges() {
	if (el.has("bannedRanges")) {
	    JsonArray ranges = el.get("bannedRanges").getAsJsonArray();
	    for (JsonElement rangeEl : ranges) {
		JsonObject range = rangeEl.getAsJsonObject();
		String reason = getString(range, "reason", "No Reason");
		IPRange iprange = new IPRange(range.get("start").getAsString(), range.get("end").getAsString());
		config.bannedIPRanges.put(iprange, reason);
	    }
	}
    }

    private void parseReservedNicks() {
	if (el.has("reservedNicks")) {
	    JsonArray nicks = el.get("reservedNicks").getAsJsonArray();
	    for (JsonElement nickEl : nicks) {
		config.reservedNicks.add(nickEl.getAsString());
	    }
	}
    }

    private void parseBannedGECOS() {
	if (el.has("bannedGECOS")) {
	    JsonArray GECOS = el.get("bannedGECOS").getAsJsonArray();
	    for (JsonElement GECOSel : GECOS) {
		config.bannedGECOS.add(GECOSel.getAsString());
	    }
	}
    }

    private void parseChannelOpts() {
	if (el.has("channelOpts")) {
	    JsonObject opts = el.get("channelOpts").getAsJsonObject();
	    config.useInvex = getBoolean(opts, "useInvex", true);
	    config.useExcept = getBoolean(opts, "useExcept", true);
	    config.useKnock = getBoolean(opts, "useKnock", true);
	    config.knockDelay = getInt(opts, "knockDelay", 300);
	    config.knockDelayChannel = getInt(opts, "knockDelayChannel", 60);
	    config.maxChansPerUser = getInt(opts, "maxChansPerUser", 15);
	    config.quietOnBan = getBoolean(opts, "quietOnBan", true);
	    config.maxBans = getInt(opts, "maxBans", 2500);
	    config.disableLocalChannels = getBoolean(opts, "disableLocalChannels", false);
	    config.adminJoinsTakePriority = getBoolean(opts, "adminJoinsTakePriority", false);
	}
    }

    private void parseGeneral() {
	if (el.has("general")) {
	    JsonObject gen = el.get("general").getAsJsonObject();
	    config.floodCount = getInt(gen, "floodCount", 10);
	    config.failedOperNotice = getBoolean(gen, "failedOperNotice", true);
	    if (gen.has("antiNickFlood")) {
		JsonObject anf = gen.get("antiNickFlood").getAsJsonObject();
		config.antiNickFlood = getBoolean(anf, "enabled", true);
		config.antiNickFloodPeriod = getInt(anf, "period", 20);
		config.antiNickFloodChanges = getInt(anf, "changes", 5);
	    }
	    config.klineWithReason = getBoolean(gen, "klineWithReason", true);
	    config.klineWithConnectionClosed = getBoolean(gen, "klineWithConnectionClosed", false);
	    config.noOperFloodLimit = getBoolean(gen, "noOperFloodLimit", true);
	    config.maxClientQueueFlood = getInt(gen, "maxClientQueueFlood", 20);
	    config.throttleTime = getInt(gen, "throttleTime", 10);
	}
    }

}
