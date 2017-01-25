package se.gustavkarlsson.aurora_notifier.android.evaluation;

public class Complication {
	private final int title;
	private final int description;

	public Complication(int title, int description) {
		this.title = title;
		this.description = description;
	}

	public int getTitle() {
		return title;
	}

	public int getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "Complication{" +
				"title=" + title +
				", description=" + description +
				'}';
	}
}
