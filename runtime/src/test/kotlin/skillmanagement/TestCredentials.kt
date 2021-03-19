package skillmanagement

import okhttp3.Credentials

object TestCredentials {
    val admin = Credentials.basic("admin", "admin")
    val actuator = Credentials.basic("actuator", "actuator")
}
