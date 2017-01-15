package se.gustavkarlsson.aurora_notifier.android.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

public class AuroraEvaluation {
	private AuroraChance chance = AuroraChance.HIGH;
	private final List<String> complications = new LinkedList<>();

	public void addComplication(String complication, AuroraChance newChance) {
		complications.add((complication));
		checkNotNull(newChance);
		if (newChance.ordinal() < chance.ordinal()) {
			chance = newChance;
		}
	}

	public AuroraChance getChance() {
		return chance;
	}

	public List<String> getComplications() {
		return new ArrayList<>(complications);
	}
}
