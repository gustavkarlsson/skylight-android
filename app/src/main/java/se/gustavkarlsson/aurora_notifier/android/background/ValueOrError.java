package se.gustavkarlsson.aurora_notifier.android.background;

import static java.util.Objects.requireNonNull;

public final class ValueOrError<T> {
	private final T value;
	private final Integer errorStringResource;

	private ValueOrError(T value, Integer errorStringResource) {
		this.value = value;
		this.errorStringResource = errorStringResource;
	}

	public static <T> ValueOrError<T> value(T value) {
		return new ValueOrError<>(value, null);
	}

	public static <T> ValueOrError<T> error(int errorStringResource) {
		return new ValueOrError<>(null, errorStringResource);
	}

	public boolean isValue() {
		return value != null;
	}

	public T getValue() {
		return requireNonNull(value, "value is null");
	}

	public int getErrorStringResource() {
		return requireNonNull(errorStringResource, "errorStringResource is null");
	}
}
