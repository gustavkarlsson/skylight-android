package se.gustavkarlsson.aurora_notifier.android.evaluation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AuroraEvaluation {
	private AuroraChance chance = AuroraChance.HIGH;
	private final List<String> complications = new LinkedList<>();

	public void updateChance(AuroraChance newChance) {
		if (newChance.ordinal() < chance.ordinal()) {
			chance = newChance;
		}
	}

	public void addComplication(String complication) {
		complications.add(complication);
	}

	public AuroraChance getChance() {
		return chance;
	}

	public List<String> getComplications() {
		return new ArrayList<>(complications);
	}
}
