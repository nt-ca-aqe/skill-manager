package skillmanagement

import skillmanagement.test.AbstractHttpTestDriver

internal class ApplicationTestDriver(
    host: String = "localhost",
    port: Int = 8080
) : AbstractHttpTestDriver(host, port) {

    fun getInfo(authorization: String? = null): String {
        val response = get(path = "/actuator/info", authorization = authorization)
        return when (response.code) {
            200 -> response.body?.string() ?: ""
            else -> error(unmappedCase(response))
        }
    }

    fun getHealth(authorization: String? = null): String {
        val response = get(path = "/actuator/health", authorization = authorization)
        return when (response.code) {
            200 -> response.body?.string() ?: ""
            else -> error(unmappedCase(response))
        }
    }

    fun getBeans(authorization: String? = null): String {
        val response = get(path = "/actuator/beans", authorization = authorization)
        return when (response.code) {
            200 -> response.body?.string() ?: ""
            else -> error(unmappedCase(response))
        }
    }

}
