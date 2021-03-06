package skillmanagement.runtime.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "custom.security.cors")
data class CorsConfigurationProperties(
    val allowedOrigins: List<String>
)
