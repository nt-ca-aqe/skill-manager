package skillmanagement.test

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule
import org.springframework.http.MediaType
import java.net.URL
import kotlin.reflect.KClass

abstract class AbstractHttpTestDriver(
    private val host: String,
    private val port: Int
) {

    private val client: OkHttpClient = OkHttpClient.Builder().build()
    private val om = jacksonObjectMapper().registerModule(Jackson2HalModule())

    protected fun post(path: String, bodySupplier: () -> Any): Response =
        post(path, bodySupplier())

    protected fun post(path: String, body: Any): Response = request(path)
        .post(jsonRequestBody(body))
        .let(::execute)

    protected fun get(path: String): Response = request(path)
        .get()
        .let(::execute)

    protected fun delete(path: String): Response = request(path)
        .delete()
        .let(::execute)

    private fun request(path: String): Request.Builder =
        Request.Builder()
            .url(url(path))

    private fun url(path: String): URL = URL("http://$host:$port$path")

    private fun jsonRequestBody(request: Any) =
        om.writeValueAsString(request).toRequestBody(MediaType.APPLICATION_JSON_VALUE.toMediaType())

    private fun execute(request: Request.Builder): Response = client.newCall(request.build()).execute()

    protected fun <T : Any> Response.readBodyAs(clazz: KClass<T>): T =
        body!!.byteStream().use { stream -> om.readValue(stream, clazz.java) }

    protected fun unmappedCase(response: Response): String =
        "Unmapped Response Code [${response.code}] with Body:\n${response.body?.string()}"

}
