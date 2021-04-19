package jforgame.server.game.cron.job;

import jforgame.server.game.GameContext;
import jforgame.server.game.core.SystemParameters;
import jforgame.server.game.database.user.PlayerEnt;
import jforgame.server.game.player.DailyResetTask;
import jforgame.server.logs.LoggerSystem;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import java.util.Collection;

/**
 * 每日５点定时job
 * @author kinson
 */
@DisallowConcurrentExecution
public class DailyResetJob implements Job {

	private Logger logger = LoggerSystem.CRON_JOB.getLogger();

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		logger.info("每日５点定时任务开始");

		long now = System.currentTimeMillis();

		SystemParameters.update("dailyResetTimestamp", now);
        Collection<PlayerEnt> onlines = GameContext.playerManager.getOnlinePlayers().values();
		for (PlayerEnt player:onlines) {
			//将事件封装成任务，丢回业务线程处理
			player.tell(new DailyResetTask(player));
		}
	}

}
