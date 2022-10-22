package se.gustavkarlsson.skylight.android.lib.settings

import androidx.datastore.core.Serializer
import se.gustavkarlsson.skylight.android.lib.settings.proto.SettingsMessage
import java.io.InputStream
import java.io.OutputStream

internal object SettingsSerializer : Serializer<SettingsMessage> {
    override val defaultValue: SettingsMessage = SettingsMessage.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): SettingsMessage {
        return SettingsMessage.parseFrom(input)
    }

    override suspend fun writeTo(t: SettingsMessage, output: OutputStream) {
        t.writeTo(output)
    }
}
