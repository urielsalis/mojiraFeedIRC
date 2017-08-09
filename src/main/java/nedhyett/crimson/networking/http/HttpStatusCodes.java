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

package nedhyett.crimson.networking.http;

/**
 * List of status codes known to the server.
 *
 * @author Ned Hyett
 */
public enum HttpStatusCodes {

	/**
	 *
	 */
	_100("Continue"),

	/**
	 *
	 */
	_101("Switching Protocols"),

	/**
	 *
	 */
	_102("Processing"),

	/**
	 *
	 */
	_200("OK"),

	/**
	 *
	 */
	_201("Created"),

	/**
	 *
	 */
	_202("Accepted"),

	/**
	 *
	 */
	_203("Non-Authoritative Information"),

	/**
	 *
	 */
	_204("No Content"),

	/**
	 *
	 */
	_205("Reset Content"),

	/**
	 *
	 */
	_206("Partial Content"),

	/**
	 *
	 */
	_207("Multi-Status"),

	/**
	 *
	 */
	_208("Already Reported"),

	/**
	 *
	 */
	_226("IM Used"),

	/**
	 *
	 */
	_300("Multiple Choices"),

	/**
	 *
	 */
	_301("Moved Permanently", "You need to go find this page somewhere else!"),

	/**
	 *
	 */
	_302("Found"),

	/**
	 *
	 */
	_303("See Other"),

	/**
	 *
	 */
	_304("Not Modified"),

	/**
	 *
	 */
	_305("Use Proxy"),

	/**
	 *
	 */
	_306("Switch Proxy"),

	/**
	 *
	 */
	_307("Temporary Redirect"),

	/**
	 *
	 */
	_308("Permanent Redirect"),

	/**
	 *
	 */
	_400("Bad Request", "You messed up."),

	/**
	 *
	 */
	_401("Unauthorized", "You aren't allowed here."),

	/**
	 *
	 */
	_402("Payment Required", "Paywalls!"),

	/**
	 *
	 */
	_403("Forbidden", "You are not supposed to be here..."),

	/**
	 *
	 */
	_404("Not Found", "Oops! I just dropped the page!"),

	/**
	 *
	 */
	_405("Method Not Allowed", "Use another method!"),

	/**
	 *
	 */
	_406("Not Acceptable", "This is UNACCEPTABLE!"),

	/**
	 *
	 */
	_407("Proxy Authentication Required", "Authenticate with your proxy."),

	/**
	 *
	 */
	_408("Request Timeout", "Something took too long..."),

	/**
	 *
	 */
	_409("Conflict", "Something conflicted with something else..."),

	/**
	 *
	 */
	_410("Gone", "Check somewhere else..."),

	/**
	 *
	 */
	_411("Length Required", "Please send a length with your request."),

	/**
	 *
	 */
	_412("Precondition Failed", "Yes, you know that thing called a \"precondition\"? Well, it just failed. Have no clue what it means, I just know that it failed."),

	/**
	 *
	 */
	_413("Request Entity Too Large", "I can't lift that!"),

	/**
	 *
	 */
	_414("Request-URI Too Long", "Too much data to process!"),

	/**
	 *
	 */
	_415("Unsupported Media Type", "I can't show you this."),

	/**
	 *
	 */
	_416("Requested Range Not Satisfiable", "Try again!"),

	/**
	 *
	 */
	_417("Expectation Failed", "I had better expectations of you, I can't believe you did this!"),

	/**
	 *
	 */
	_418("I'm a teapot", "Short and Stout!"), //Yes you are.

	/**
	 *
	 */
	_419("Authentication Timeout", "Too long!"),

	/**
	 *
	 */
	_422("Unprocessable Entity", "I <span style=\"text-decoration: underline\">refuse</span> to process this entity!"),

	/**
	 *
	 */
	_423("Locked", "I don't want you seeing that..."),

	/**
	 *
	 */
	_424("Failed Dependency", "Yes, the dependency just failed. Well, time to get a new one!"),

	/**
	 *
	 */
	_425("Unordered Collection", "I am terrible at organization..."),

	/**
	 *
	 */
	_426("Upgrade Required", "Upgrade time!"),

	/**
	 *
	 */
	_428("Precondition Required", "I require a precondition. All your preconditions are belong to us!"),

	/**
	 *
	 */
	_429("Too Many Requests", "Stop, please!"),

	/**
	 *
	 */
	_431("Request Header Fields Too Large", "Chop off the request header fields."),

	/**
	 *
	 */
	_451("Unavailable For Legal Reasons", "Someone said I can't show that to you or I would get in trouble. Sorry..."),

	/**
	 *
	 */
	_500("Internal Server Error", "Hrm... I'm sure that wasn't supposed to happen..."),

	/**
	 *
	 */
	_501("Not Implemented", "Nope, I don't support that."),

	/**
	 *
	 */
	_502("Bad Gateway", "Something went wrong..."),

	/**
	 *
	 */
	_503("Service Unavailable", "Yeah, I don't wish to talk to you right now..."),

	/**
	 *
	 */
	_504("Gateway Timeout", "Something took too long..."),

	/**
	 *
	 */
	_505("HTTP Version Not Supported", "Upgrade the HTTP version now."),

	/**
	 *
	 */
	_506("Variant Also Negotiates", "What?"),

	/**
	 *
	 */
	_509("Bandwidth Limit Exceeded", "Too much bandwidth used."),

	/**
	 *
	 */
	_510("Not Extended", "... Just why?"),

	/**
	 *
	 */
	_511("Network Authentication Required", "Authenticate already!");


	private final String codeData;
	private final String page_header;
	private String page_subtext;
	private String page_title;

	HttpStatusCodes(String codeData) {
		this.codeData = codeData;
		page_header = codeData + " (" + this.toString().replace("_", "") + ")";
		page_subtext = "That's all we know...";
		page_title = this.toString().replace("_", "") + " - Crimson HTTP Server";
	}

	HttpStatusCodes(String codeData, String page_subtext) {
		this(codeData);
		this.page_subtext = page_subtext;
	}

	HttpStatusCodes(String codeData, String page_subtext, String page_title) {
		this(codeData, page_subtext);
		this.page_title = page_title;
	}

	/**
	 * @return
	 */
	public String getCode() {
		return "HTTP/1.1 " + this.toString().replace("_", "") + " " + codeData;
	}

	public void generatePage(HttpResponse response) {
		response.addResponseCode(this);
		response.addResponseBodyLine("<!DOCTYPE html>");
		response.addResponseBodyLine("<html>");
		response.addResponseBodyLine("<head>");
		response.addResponseBodyLine("<title>" + page_title + "</title>");
		response.addResponseBodyLine("</head>");
		response.addResponseBodyLine("<body>");
		response.addResponseBodyLine("<h1>" + page_header + "</h1>");
		response.addResponseBodyLine("<p>" + page_subtext + "</p>");
		response.addResponseBodyLine("</body>");
		response.addResponseBodyLine("</html>");
	}

	public static HttpStatusCodes getCodeForNumber(int number) {
		for(HttpStatusCodes code : values()) {
			if(code.toString().replace("_", "").equalsIgnoreCase(Integer.toString(number))) return code;
		}
		return null;
	}

}
