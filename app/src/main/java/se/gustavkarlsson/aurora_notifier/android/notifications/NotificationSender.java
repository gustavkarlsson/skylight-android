package se.gustavkarlsson.aurora_notifier.android.notifications;

public interface NotificationSender<T> {

	void notify(T data);
}
