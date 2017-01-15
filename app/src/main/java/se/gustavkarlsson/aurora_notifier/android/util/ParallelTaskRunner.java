package se.gustavkarlsson.aurora_notifier.android.util;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ParallelTaskRunner {
	private static final String TAG = ParallelTaskRunner.class.getSimpleName();

	private final Queue<AsyncTask> tasks = new ArrayDeque<>();
	private final Executor executor = AsyncTask.THREAD_POOL_EXECUTOR;

	public synchronized TaskExecutionReport executeInParallel(long timeoutMillis, AsyncTask... tasksToStart) {
		long startMillis = System.currentTimeMillis();
		startTasks(tasksToStart);
		return waitForTasksToFinish(timeoutMillis, startMillis);
	}

	private void startTasks(AsyncTask[] tasksToStart) {
		for (AsyncTask task : tasksToStart) {
			tasks.offer(task);
			task.executeOnExecutor(executor);
		}
	}

	@NonNull
	private TaskExecutionReport waitForTasksToFinish(long timeoutMillis, long startMillis) {
		TaskExecutionReport report = new TaskExecutionReport();
		while (!tasks.isEmpty()) {
			long timePassedMillis = System.currentTimeMillis() - startMillis;
			long timeLeft = timeoutMillis - timePassedMillis;
			AsyncTask task = tasks.poll();
			try {
				task.get(timeLeft, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				Log.e(TAG, "Thread was interrupted. This should not happen!");
			} catch (ExecutionException e) {
				report.addExecutionException(task, e);
			} catch (TimeoutException e) {
				report.addTimeoutException(task, e);
			}
		}
		return report;
	}

	public static class TaskExecutionReport {
		private Map<AsyncTask, ExecutionException> executionExceptions = new IdentityHashMap<>();
		private Map<AsyncTask, TimeoutException> timeoutExceptions = new IdentityHashMap<>();

		private void addExecutionException(AsyncTask task, ExecutionException exception) {
			executionExceptions.put(task, exception);
		}

		private void addTimeoutException(AsyncTask task, TimeoutException exception) {
			timeoutExceptions.put(task, exception);
		}

		public Map<AsyncTask, ExecutionException> getExecutionExceptions() {
			return new IdentityHashMap<>(executionExceptions);
		}

		public Map<AsyncTask, TimeoutException> getTimeoutExceptions() {
			return new IdentityHashMap<>(timeoutExceptions);
		}

		public boolean hasErrors() {
			return !executionExceptions.isEmpty() || !timeoutExceptions.isEmpty();
		}
	}
}
