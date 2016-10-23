package se.gustavkarlsson.aurora_notifier.android.providers;

public class ProviderException extends Exception {

	public ProviderException() {
	}

	public ProviderException(String detailMessage) {
		super(detailMessage);
	}

	public ProviderException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ProviderException(Throwable throwable) {
		super(throwable);
	}
}
