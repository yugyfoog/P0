package com.training.p0.dao;

import java.util.List;

import com.training.p0.consoleapp.AccountInfo;
import com.training.p0.consoleapp.User;

public interface DBaseDAO {
	User getUser(int userId);
	boolean addUser(User user);
	boolean newAccount(int userId, int amount);
	List<AccountInfo> viewAccount(int userId);
}
