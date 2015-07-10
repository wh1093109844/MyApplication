package com.hero.auth.listener;

import com.hero.auth.AuthUser;

public interface IAuthLoginListener {
	
	public void onCancel();
	public void onComplete(AuthUser authUser);
	public void onError(String error);

}
