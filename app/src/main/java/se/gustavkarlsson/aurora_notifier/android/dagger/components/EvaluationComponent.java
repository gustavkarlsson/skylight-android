package se.gustavkarlsson.aurora_notifier.android.dagger.components;

import dagger.Component;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.EvaluationModule;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_chance.AuroraChanceFragment;

@Component(modules = EvaluationModule.class)
@SuppressWarnings("WeakerAccess")
public interface EvaluationComponent {
	void inject(AuroraChanceFragment auroraChanceFragment);
}
