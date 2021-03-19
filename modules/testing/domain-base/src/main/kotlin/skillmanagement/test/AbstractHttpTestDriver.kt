package skillmanagement.test

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import java.net.URL
import kotlin.reflect.KClass

abstract class AbstractHttpTestDriver(
    private val host: String,
    private val port: Int
) {

    private val client: OkHttpClient = OkHttpClient.Builder().build()
    private val om = jacksonObjectMapper().registerModule(Jackson2HalModule())

    protected fun post(path: String, authorization: String? = null): Response =
        request(path, authorization)
            .post("".toRequestBody())
            .let(::execute)

    protected fun post(path: String, authorization: String? = null, bodySupplier: () -> Any): Response =
        post(path, authorization, bodySupplier())

    protected fun post(path: String, authorization: String? = null, body: Any): Response =
        request(path, authorization)
            .post(jsonRequestBody(body))
            .let(::execute)

    protected fun get(path: String, authorization: String? = null): Response =
        request(path, authorization)
            .get()
            .let(::execute)

    protected fun delete(path: String, authorization: String? = null): Response =
        request(path, authorization)
            .delete()
            .let(::execute)

    private fun request(path: String, authorization: String?): Request.Builder =
        Request.Builder()
            .url(url(path))
            .apply { if (authorization != null) addHeader(AUTHORIZATION, authorization) }

    private fun url(path: String): URL = URL("http://$host:$port$path")

    private fun jsonRequestBody(request: Any): RequestBody =
        om.writeValueAsString(request).toRequestBody(APPLICATION_JSON_VALUE.toMediaType())

    private fun execute(request: Request.Builder): Response = client.newCall(request.build()).execute()

    protected fun <T : Any> Response.readBodyAs(clazz: KClass<T>): T =
        body!!.byteStream().use { stream -> om.readValue(stream, clazz.java) }

    protected fun unmappedCase(response: Response): String =
        "Unmapped Response Code [${response.code}] with Body:\n${response.body?.string()}"

}
