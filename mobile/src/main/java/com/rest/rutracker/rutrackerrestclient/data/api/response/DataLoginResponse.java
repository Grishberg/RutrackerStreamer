package com.rest.rutracker.rutrackerrestclient.data.api.response;

import android.provider.ContactsContract;

import java.io.Serializable;

/**
 * Created by G on 05.07.15.
 */
public class DataLoginResponse implements Serializable {
	private boolean auth;

	public DataLoginResponse(boolean auth){
		this.auth	= auth;
	}

	public boolean isAuth() {
		return auth;
	}
}
