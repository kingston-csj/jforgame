package com.kingston.jforgame.server.game.cronjob;

import java.util.Collection;

import com.kingston.jforgame.server.game.GameContext;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.kingston.jforgame.server.game.core.SystemParameters;
import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.game.player.DailyResetTask;
import com.kingston.jforgame.server.logs.LoggerSystem;
import com.kingston.jforgame.socket.task.TaskHandlerContext;

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

		long now = System.currentTimeMillis();

		SystemParameters.update("dailyResetTimestamp", now);
        Collection<Player> onlines = GameContext.getPlayerManager().getOnlinePlayers().values();
		for (Player player:onlines) {
			int distributeKey = player.distributeKey();
			//将事件封装成timer任务，丢回业务线程处理
			TaskHandlerContext.INSTANCE.acceptTask(new DailyResetTask(distributeKey, player));
		}

	}

}
