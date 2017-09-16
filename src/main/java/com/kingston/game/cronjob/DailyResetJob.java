package com.kingston.game.cronjob;

import java.util.Collection;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.kingston.game.database.user.player.Player;
import com.kingston.game.player.DailyResetTask;
import com.kingston.game.player.PlayerManager;
import com.kingston.logs.LoggerSystem;
import com.kingston.net.context.TaskHandlerContext;

/**
 * 每日５点定时job
 * @author kingston
 */
@DisallowConcurrentExecution
public class DailyResetJob implements Job {

	private Logger logger = LoggerSystem.CRON_JOB.getLogger();

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("每日５点定时任务开始");

		Collection<Player> onlines = PlayerManager.getInstance().getOnlinePlayers().values();

		for (Player player:onlines) {
			int distributeKey = player.distributeKey();
			TaskHandlerContext.INSTANCE.acceptTask(new DailyResetTask(distributeKey, player));
		}

	}

}
