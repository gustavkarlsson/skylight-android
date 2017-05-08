package se.gustavkarlsson.aurora_notifier.android.dagger.scopes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@Scope
@Retention(RetentionPolicy.RUNTIME) public @interface BackgroundScope {}
