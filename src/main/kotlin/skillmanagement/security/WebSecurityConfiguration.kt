package skillmanagement.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.web.filter.ForwardedHeaderFilter

@Configuration
class WebSecurityConfiguration : WebSecurityConfigurerAdapter() {

    private val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    override fun configure(http: HttpSecurity) {
        with(http) {
            httpBasic()
            sessionManagement().sessionCreationPolicy(STATELESS)
            authorizeRequests().anyRequest().authenticated()

            disableUnwantedFeatures()
        }
    }

    private fun HttpSecurity.disableUnwantedFeatures() {
        cors().disable()
        csrf().disable()
        formLogin().disable()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        with(auth.inMemoryAuthentication()) {
            withUser(user("user", USER))
            withUser(user("admin", USER, ADMIN))
            withUser(user("slu", USER, ADMIN))
        }
    }

    private fun user(username: String, vararg roles: String) = User.withUsername(username)
        .password(passwordEncoder.encode(username.reversed()))
        .roles(*roles)
        .build()

    @Bean
    fun forwardedHeaderFilter(): ForwardedHeaderFilter = ForwardedHeaderFilter()

}
