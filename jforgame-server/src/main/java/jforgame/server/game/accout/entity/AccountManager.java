package jforgame.server.game.accout.entity;

import jforgame.server.cache.BaseCacheService;
import jforgame.server.db.DbService;
import jforgame.server.db.DbUtils;
import jforgame.server.game.GameContext;

public class AccountManager extends BaseCacheService<Long, AccountEnt> {

	@Override
	public AccountEnt load(Long accountId) throws Exception {
		String sql = "SELECT * FROM accountent WHERE id = ? ";
		AccountEnt account = DbUtils.queryOneById(DbUtils.DB_USER, sql, AccountEnt.class, String.valueOf(accountId));
		return account;
	}
	
	public AccountEnt getOrCreate(long accountId) {
		AccountEnt account = get(accountId);
		if (account != null) {
			return account;
		}
		
		AccountEnt newAccount = new AccountEnt();
		newAccount.setId(accountId);
		DbService.getInstance().insertOrUpdate(newAccount);
		GameContext.playerManager.addAccountProfile(newAccount);
		return newAccount;
	}
	
}
