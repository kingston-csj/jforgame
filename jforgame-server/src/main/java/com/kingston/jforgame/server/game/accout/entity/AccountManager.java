package com.kingston.jforgame.server.game.accout.entity;

import com.kingston.jforgame.server.cache.BaseCacheService;
import com.kingston.jforgame.server.db.DbService;
import com.kingston.jforgame.server.db.DbUtils;
import com.kingston.jforgame.server.game.GameContext;

public class AccountManager extends BaseCacheService<Long, Account> {

	@Override
	public Account load(Long accountId) throws Exception {
		String sql = "SELECT * FROM account WHERE id = ? ";
		Account account = DbUtils.queryOneById(DbUtils.DB_USER, sql, Account.class, String.valueOf(accountId));
		return account;
	}
	
	public Account getOrCreate(long accountId) {
		Account account = get(accountId);
		if (account != null) {
			return account;
		}
		
		Account newAccount = new Account();
		newAccount.setId(accountId);
		DbService.getInstance().insertOrUpdate(newAccount);
		GameContext.getPlayerManager().addAccountProfile(newAccount);
		return newAccount;
	}
	
}
