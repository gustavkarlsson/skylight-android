package se.gustavkarlsson.aurora_notifier.android.evaluation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AuroraEvaluation {
	private AuroraChance chance = AuroraChance.HIGH;
	private final List<Complication> complications = new LinkedList<>();

	public void updateChance(AuroraChance newChance) {
		if (newChance.ordinal() < chance.ordinal()) {
			chance = newChance;
		}
	}

	public void addComplication(Complication complication) {
		complications.add(complication);
	}

	public void addComplication(int title, int description) {
		addComplication(new Complication(title, description));
	}

	public AuroraChance getChance() {
		return chance;
	}

	public List<Complication> getComplications() {
		return new ArrayList<>(complications);
	}
}
