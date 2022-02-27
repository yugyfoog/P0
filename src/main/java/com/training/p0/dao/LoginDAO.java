package com.training.p0.dao;

import com.training.p0.consoleapp.Credentials;

public interface LoginDAO {
	public int newUser(Credentials credentials);
	public int validate(Credentials credentials);
	public boolean deleteUser(int userId);
	
}
