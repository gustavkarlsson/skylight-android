package se.gustavkarlsson.skylight.android.observers;

import java.util.IdentityHashMap;
import java.util.Set;

import static java.util.Collections.newSetFromMap;
import static java8.util.stream.StreamSupport.stream;

public class ObservableData<T> {
	private final Set<DataObserver<T>> listeners = newSetFromMap(new IdentityHashMap<>(20));
	private T data;

	public ObservableData(T data) {
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
		stream(listeners).forEach(listener -> listener.dataChanged(data));
	}

	public boolean addListener(DataObserver<T> listener) {
		return listeners.add(listener);
	}

	public boolean removeListener(DataObserver<T> listener) {
		return listeners.remove(listener);
	}

}
