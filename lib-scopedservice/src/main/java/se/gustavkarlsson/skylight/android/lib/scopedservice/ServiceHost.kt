package se.gustavkarlsson.skylight.android.lib.scopedservice

import se.gustavkarlsson.skylight.android.services.ServiceRegistry

interface ServiceHost {
    val serviceRegistry: ServiceRegistry
}
