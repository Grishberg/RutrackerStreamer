package com.rest.rutracker.rutrackerrestclient.data.api.response;

import android.provider.ContactsContract;

import java.io.Serializable;

/**
 * Created by G on 05.07.15.
 */
public class DataLoginResponse implements Serializable {
	private int status;
	private String cap;
	private String capSid;
	private String capName;
	private String capUrl;

	public DataLoginResponse(int status){
		this.status	= status;
	}

	public DataLoginResponse(int status
			,String cap,String capSid, String capName, String capUrl){
		this.status		= status;
		this.cap		= cap;
		this.capSid		= capSid;
		this.capName	= capName;
	}

	public int authStatus() {
		return status;
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
