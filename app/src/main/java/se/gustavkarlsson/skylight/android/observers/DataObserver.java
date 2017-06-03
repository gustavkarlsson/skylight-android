package se.gustavkarlsson.skylight.android.observers;

public interface DataObserver<T> {
	void dataChanged(T newData);
}
