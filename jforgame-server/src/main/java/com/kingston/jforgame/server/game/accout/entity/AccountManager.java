package com.kingston.jforgame.server.game.accout.entity;

import java.text.MessageFormat;

import com.kingston.jforgame.server.cache.BaseCacheService;
import com.kingston.jforgame.server.db.DbService;
import com.kingston.jforgame.server.db.DbUtils;

public class AccountManager extends BaseCacheService<Long, Account> {

	private static AccountManager instance = new AccountManager();

	public static AccountManager getInstance() {
		return instance;
	}

	@Override
	public Account load(Long accountId) throws Exception {
		String sql = "SELECT * FROM account where id = {0} ";
		sql = MessageFormat.format(sql, String.valueOf(accountId));
		Account account = DbUtils.queryOne(DbUtils.DB_USER, sql, Account.class);
		return account;
	}
	
	public Account getOrCreate(long accountId) {
		Account account = get(accountId);
		if (account != null) {
			return account;
		}
		account = new Account();
		account.setId(accountId);
		DbService.getInstance().add2Queue(account);
		return account;
	}

}
