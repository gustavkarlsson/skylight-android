package se.gustavkarlsson.skylight.android.feature.addplace

import com.ioki.textref.TextRef
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.extensions.seconds

val featureAddPlaceModule = module {

    single("addplace_errors") {
        PublishRelay.create<TextRef>()
    }

    single<Consumer<TextRef>>("addplace_errors") {
        get<PublishRelay<TextRef>>("addplace_errors")
    }

    single<Observable<TextRef>>("addplace_errors") {
        get<PublishRelay<TextRef>>("addplace_errors")
    }

    factory {
        AddPlaceViewModel(
            placesRepository = get(),
            knot = get("addplace"),
            errorMessages = get("addplace_errors")
        )
    }

    factory("addplace") {
        createKnot(
            geocoder = get(),
            querySampleDelay = 1.seconds,
            errorMessageConsumer = get("addplace_errors")
        )
    }
}
