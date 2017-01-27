package se.gustavkarlsson.aurora_notifier.android.util;

public class UserFriendlyException extends RuntimeException {
	private final int stringResourceId;

	public UserFriendlyException(int stringResourceId, Throwable cause) {
		super(cause);
		this.stringResourceId = stringResourceId;
	}

	public UserFriendlyException(int stringResourceId, String message, Throwable cause) {
		super(message, cause);
		this.stringResourceId = stringResourceId;
	}

	public UserFriendlyException(int stringResourceId, String message) {
		super(message);
		this.stringResourceId = stringResourceId;
	}

	public UserFriendlyException(int stringResourceId) {
		this.stringResourceId = stringResourceId;
	}

	public int getStringResourceId() {
		return stringResourceId;
	}
}
