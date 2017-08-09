/*
 * The MIT License
 *
 * Copyright 2017 Ned Hyett.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nedhyett.crimson.networking.security;

import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.logging.MiniLogger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.util.ArrayList;

/**
 * Created by ned on 20/04/2015.
 * <p>
 * Used to override the certificate trust store when connecting to remote sites not trusted by Java's built-in trust store (i.e. StartSSL certs)
 * <p>
 * Use of this class is not recommended, unless the environment is strictly controlled as certificates will not be validated.
 * An example of correct use would be in a small program that does not accept 3rd-party code. Incorrect use would be in a game
 * that supports 3rd-party modifications.
 *
 * @author Ned Hyett
 */
public class HostnameVerifierPatcher implements HostnameVerifier {

	private static final MiniLogger logger = CrimsonLog.spawnLogger("DangerOps");

	private static boolean patched = false;
	private static final ArrayList<String> validHostNames = new ArrayList<>();
    private final HostnameVerifier delegate;

    public HostnameVerifierPatcher(HostnameVerifier delegate) {
	    this.delegate = delegate;
    }

	public static void patchVerifier() {
		patchVerifier(true);
	}

	/**
	 * Patch the hostname verifier for HttpsURLConnection so that it allows domains that would otherwise be blocked
	 * to be accessed via SSL.
	 */
	public static void patchVerifier(boolean safe) {
		if(patched) return;
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifierPatcher(safe ? HttpsURLConnection.getDefaultHostnameVerifier() : null));
		logger.critical("Crimson has patched HttpsURLConnection HostnameVerifier! Connections insecure.");
		patched = true;
	}

	/**
	 * Add a new hostname to the list of allowed hostnames.
	 *
	 * @param hostname the hostname to add.
	 */
	public static void addHostname(String hostname) {
		validHostNames.add(hostname);
		logger.warning("%s has been added to HttpsURLConnection trusted hostnames!", hostname);
	}

	/**
	 * Remove a hostname from the list of allowed hostnames.
	 *
	 * @param hostname the hostname to remove.
	 */
	public static void removeHostname(String hostname) {
		validHostNames.remove(hostname);
	}

	/**
	 * Check if the patcher has a hostname in the list of valid hostnames.
	 *
	 * @param hostname the hostname to check.
	 *
	 * @return if the patcher has the hostname.
	 */
	public static boolean hasHostname(String hostname) {
		return validHostNames.contains(hostname);
	}

	private HostnameVerifierPatcher() {
		delegate = null;
	}


	@Override
	public boolean verify(String s, SSLSession sslSession) {
	    if(delegate != null && delegate.verify(s, sslSession)) return true;
		if(validHostNames.isEmpty()) return true;
		for(String hn : validHostNames) if(hn.equals(s)) return true;
		return false;
	}
}
