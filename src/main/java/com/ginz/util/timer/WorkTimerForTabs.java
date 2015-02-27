package com.ginz.util.timer;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.ginz.action.account.SystemAction;

public class WorkTimerForTabs implements Runnable  {

	public static void main(String[] args){
		new WorkTimerForTabs();
	}

	/**
	 * 时间调度器
	 */
	private final static ScheduledThreadPoolExecutor schedual = new ScheduledThreadPoolExecutor(
			1);

	/**
	 * 设置调度shutdown后停止执行任务
	 */
	static {
		schedual.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		schedual.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
	}

	/**
	 * 执行结果
	 */
	private ScheduledFuture<?> scheduledFuture;

	public WorkTimerForTabs() {
		int worktime = 60*60*24 ;//每天执行一次
		scheduledFuture = schedual.scheduleWithFixedDelay(this, 5, worktime,
				TimeUnit.SECONDS); // 程序开始后5秒执行
	}

	public void run() {
		try {
			/*TimeTask tt = new TimeTask();
			tt.getPersonalTabs();
			tt.getEventLabel();*/
		} catch (Exception e) {
			e.printStackTrace();
			schedual.shutdown();
			new WorkTimerForTabs();
		}
	}
	
}
