package jforgame.demo.game.accout.entity;

import jforgame.demo.cache.BaseCacheService;
import jforgame.demo.db.DbService;
import jforgame.demo.db.DbUtils;
import jforgame.demo.game.GameContext;

public class AccountManager extends BaseCacheService<Long, AccountEnt> {

	@Override
	public AccountEnt load(Long accountId) throws Exception {
		String sql = "SELECT * FROM accountent WHERE id = ? ";
		AccountEnt account = DbUtils.queryOne(DbUtils.DB_USER, sql, AccountEnt.class, String.valueOf(accountId));
		return account;
	}
	
	public AccountEnt getOrCreate(long accountId) {
		AccountEnt account = get(accountId);
		if (account != null) {
			return account;
		}
		
		AccountEnt newAccount = new AccountEnt();
		newAccount.setId(accountId);
		DbService.getInstance().saveToDb(newAccount);
		GameContext.playerManager.addAccountProfile(newAccount);
		return newAccount;
	}
	
}
