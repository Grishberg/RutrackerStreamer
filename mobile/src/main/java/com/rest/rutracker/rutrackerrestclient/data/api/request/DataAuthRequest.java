package com.rest.rutracker.rutrackerrestclient.data.api.request;

/**
 * Created by G on 05.07.15.
 */
public class DataAuthRequest extends DataRequest {

	private String login;
	private String password;

	public DataAuthRequest(String login, String password) {
		super();
		this.login      = login;
		this.password   = password;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}
}
