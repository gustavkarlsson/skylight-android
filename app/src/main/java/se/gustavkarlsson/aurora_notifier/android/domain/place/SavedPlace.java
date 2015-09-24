package se.gustavkarlsson.aurora_notifier.android.domain.place;

public final class SavedPlace implements Place {

	private String name;
	private double latitude;
	private double longitude;

	public SavedPlace(String name, double latitude, double longitude) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Position getPosition() {
		return new Position(latitude, longitude);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		SavedPlace that = (SavedPlace) o;

		return name.equals(that.name);

	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return "SavedPlace{" +
				"name='" + name + '\'' +
				", position=" + getPosition() +
				'}';
	}
}
