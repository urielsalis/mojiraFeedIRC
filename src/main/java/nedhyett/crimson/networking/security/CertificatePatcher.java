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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Used to override the certificate trust store when connecting to remote sites not trusted by Java's built-in trust store (i.e. StartSSL certs)
 * <p>
 * Use of this class is not recommended, unless the environment is strictly controlled as certificates will not be validated.
 * An example of correct use would be in a small program that does not accept 3rd-party code. Incorrect use would be in a game
 * that supports 3rd-party modifications.
 *
 * @author Ned Hyett
 */
public class CertificatePatcher implements X509TrustManager {

	private static final MiniLogger logger = CrimsonLog.spawnLogger("DangerOps");
	private static boolean patched = false;

	/**
	 * Patch the trust store so it uses the CertificatePatcher instead of the default implementation.
	 */
	public static void patchTrustStore() {
		if(patched) return;
		try {
			final SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[]{new CertificatePatcher()}, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			patched = true;
			logger.critical("Crimson has patched HttpsURLConnection SSLSocketFactory TrustManager/TrustStore! Connections may be insecure.");
		} catch(Exception e) {
			logger.critical("Cannot override SSL Socket Factory!");
			logger.critical(e);
		}
	}

	private CertificatePatcher() {
	}

	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

	}

	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}

}
