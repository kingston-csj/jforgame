package com.kingston.game.cronjob;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;

import com.kingston.logs.LoggerSystem;

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



	}

}
