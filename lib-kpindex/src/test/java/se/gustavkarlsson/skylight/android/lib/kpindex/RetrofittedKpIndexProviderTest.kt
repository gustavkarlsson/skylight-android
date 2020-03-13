package se.gustavkarlsson.skylight.android.lib.kpindex

import assertk.assert
import assertk.assertions.isBetween
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.apache.commons.io.IOUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.robolectric.RobolectricTestRunner
import org.threeten.bp.Instant
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Time

@RunWith(RobolectricTestRunner::class)
class RetrofittedKpIndexProviderTest {
    lateinit var mockedClient: OkHttpClient
    lateinit var mockedTime: Time

    @Before
    fun setUp() {
        mockedClient = mockClient(200, "fixtures/kp_index_report.json", "application/json")
        mockedTime = mock {
            on(it.now()).thenReturn(Instant.EPOCH)
        }
    }

    private fun mockClient(
        statusCode: Int,
        bodyResourcePath: String,
        mediaType: String
    ): OkHttpClient {
        val mockedCall = mockCall(statusCode, bodyResourcePath, mediaType)
        val mockedClient = mock<OkHttpClient>()
        whenever(mockedClient.newCall(ArgumentMatchers.any(Request::class.java))).thenReturn(mockedCall)
        return mockedClient
    }

    private fun mockCall(statusCode: Int, bodyResourcePath: String, mediaType: String): Call {
        val body = createResponseBody(bodyResourcePath, mediaType)
        val call = mock<Call>()
        whenever(call.execute()).thenReturn(
            Response.Builder()
                .request(Request.Builder().url("http://mocked.com/").build())
                .code(statusCode)
                .protocol(Protocol.HTTP_1_0)
                .body(body)
                .message("ok")
                .build())
        return call
    }

    private fun createResponseBody(resourcePath: String, mediaType: String): ResponseBody {
        val bytes = readResourceToByteArray(resourcePath)
        return ResponseBody.create(MediaType.parse(mediaType), bytes)
    }

    private fun readResourceToByteArray(resourcePath: String): ByteArray {
        val classLoader = javaClass.classLoader!!
        return IOUtils.toByteArray(classLoader.getResource(resourcePath).openStream())
    }

    @UnstableDefault
    @Test
    fun parsesKpIndexCorrectly() {
        val service = RetrofittedKpIndexProvider(Retrofit.Builder()
            .client(mockedClient)
            .baseUrl("http://mocked.com")
            .addConverterFactory(Json.nonstrict.asConverterFactory(MediaType.get("application/json")))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(KpIndexApi::class.java), mockedTime, 2.seconds, 5.seconds)

        val kpIndex = (service.get().blockingGet() as Report.Success).value

        assert(kpIndex.value).isBetween(1.32, 1.34)
    }
}
