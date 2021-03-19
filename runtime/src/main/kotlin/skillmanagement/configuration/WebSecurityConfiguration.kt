package skillmanagement.configuration

import org.apache.http.HttpHeaders.AUTHORIZATION
import org.apache.http.HttpHeaders.CACHE_CONTROL
import org.apache.http.HttpHeaders.CONTENT_TYPE
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest.to
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest.toAnyEndpoint
import org.springframework.boot.actuate.health.HealthEndpoint
import org.springframework.boot.actuate.info.InfoEndpoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.factory.PasswordEncoderFactories.createDelegatingPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class WebSecurityConfiguration(
    val corsProperties: CorsConfigurationProperties
) {

    @Profile("!unsecured")
    @Configuration
    @EnableWebSecurity
    class DefaultWebSecurityConfiguration : WebSecurityConfigurerAdapter() {

        private val encoder = createDelegatingPasswordEncoder();

        override fun configure(http: HttpSecurity): Unit = with(http) {
            cors()
            csrf().disable()
            httpBasic()
            sessionManagement().sessionCreationPolicy(STATELESS)
            authorizeRequests { requests ->
                requests.requestMatchers(to(InfoEndpoint::class.java, HealthEndpoint::class.java)).permitAll()
                requests.requestMatchers(toAnyEndpoint()).hasRole("ACTUATOR")
                requests.anyRequest().fullyAuthenticated()
            }
        }

        override fun configure(auth: AuthenticationManagerBuilder): Unit = with(auth) {
            inMemoryAuthentication {
                withUser(
                    User.withUsername("admin")
                        .password(encoder.encode("admin"))
                        .roles("ADMIN")
                        .build()
                )
                withUser(
                    User.withUsername("actuator")
                        .password(encoder.encode("actuator"))
                        .roles("ACTUATOR")
                        .build()
                )
            }
        }

        @Bean
        override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

        private fun AuthenticationManagerBuilder.inMemoryAuthentication(
            body: InMemoryUserDetailsManagerConfigurer<*>.() -> Unit
        ) = body(this.inMemoryAuthentication())
    }

    @Profile("unsecured")
    @Configuration
    @EnableWebSecurity
    class UnsecuredWebSecurityConfiguration : WebSecurityConfigurerAdapter() {

        override fun configure(http: HttpSecurity): Unit = with(http) {
            cors()
            csrf().disable()
            sessionManagement().sessionCreationPolicy(STATELESS)
            authorizeRequests().anyRequest().permitAll()
        }

    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = corsProperties.allowedOrigins
        configuration.allowedMethods = HttpMethod.values().map { it.name }
        configuration.allowCredentials = true
        configuration.allowedHeaders = listOf(AUTHORIZATION, CACHE_CONTROL, CONTENT_TYPE)
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}
