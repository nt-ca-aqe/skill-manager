package skillmanagement.common.resources

import org.springframework.core.io.Resource
import java.io.BufferedReader

fun Resource.readAsString(): String =
    inputStream.bufferedReader().use(BufferedReader::readText)
