package com.rest.rutracker.rutrackerrestclient.data.api.request;

/**
 * Created by G on 05.07.15.
 */
public class DataAuthRequest extends DataRequest {

	private String login;
	private String password;

	private String cap;
	private String capSid;
	private String capName;

	public DataAuthRequest(String login, String password) {
		super();
		this.login      = login;
		this.password   = password;
	}

	public DataAuthRequest(String login, String password
		,String cap,String capSid, String capName) {
		super();
		this.login      = login;
		this.password   = password;

		this.cap		= cap;
		this.capSid		= capSid;
		this.capName	= capName;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public String getCap() {
		return cap;
	}

	public String getCapSid() {
		return capSid;
	}

	public String getCapName() {
		return capName;
	}
}
