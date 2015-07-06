package com.rest.rutracker.rutrackerrestclient.data.api.response;

import android.provider.ContactsContract;

import java.io.Serializable;

/**
 * Created by G on 05.07.15.
 */
public class DataLoginResponse implements Serializable {
	private int status;

	private String capSid;
	private String capName;
	private String capUrl;

	public DataLoginResponse(int status){
		this.status	= status;
	}

	public DataLoginResponse(int status
			,String capSid, String capName, String capUrl){
		this.status		= status;
		this.capUrl		= capUrl;
		this.capSid		= capSid;
		this.capName	= capName;
	}

	public int authStatus() {
		return status;
	}

	public String getCapUrl() {
		return capUrl;
	}

	public String getCapSid() {
		return capSid;
	}

	public String getCapName() {
		return capName;
	}
}
