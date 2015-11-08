package se.gustavkarlsson.aurora_notifier.android.services;

import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public interface KpIndexService {

	Timestamped<Float> getKpIndex() throws ServiceException;
}
