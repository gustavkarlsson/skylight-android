package se.gustavkarlsson.aurora_notifier.android.services;

public class ServiceException extends Exception {

	public ServiceException() {
	}

	public ServiceException(String detailMessage) {
		super(detailMessage);
	}

	public ServiceException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ServiceException(Throwable throwable) {
		super(throwable);
	}
}
