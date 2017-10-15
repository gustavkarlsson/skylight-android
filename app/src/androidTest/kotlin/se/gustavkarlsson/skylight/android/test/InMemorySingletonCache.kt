package se.gustavkarlsson.skylight.android.test

import se.gustavkarlsson.skylight.android.services.SingletonCache

class InMemorySingletonCache<T>(override var value: T) : SingletonCache<T>
