package io.myiam.samples.basic

import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val props: MyiamProperties,
    private val registrations: ClientRegistrationRepository,
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain = http
        .authorizeHttpRequests {
            it.requestMatchers("/", "/error").permitAll().anyRequest().authenticated()
        }
        .oauth2Login {
            it.authorizationEndpoint { ae -> ae.authorizationRequestResolver(requestResolver()) }
            it.defaultSuccessUrl("/dashboard", true)
            it.failureHandler { req, res, _ -> req.session.invalidate(); res.sendRedirect("/") }
        }
        .logout {
            it.logoutRequestMatcher(AntPathRequestMatcher("/api/auth/logout"))
            it.logoutSuccessHandler { req, res, _ ->
                val origin = "${req.scheme}://${req.serverName}:${req.serverPort}"
                val base = registrations.findByRegistrationId("myiam")
                    .providerDetails.authorizationUri.substringBefore("/oauth2/")
                res.sendRedirect("$base/logout/form?service_uid=${props.serviceUid}&logout_redirect_uri=$origin")
            }
        }
        .build()

    private fun requestResolver() = object : OAuth2AuthorizationRequestResolver {

        private val delegate = DefaultOAuth2AuthorizationRequestResolver(registrations, "/oauth2/authorization")

        override fun resolve(req: HttpServletRequest) = customize(delegate.resolve(req), req)

        override fun resolve(req: HttpServletRequest, id: String) = customize(delegate.resolve(req, id), req)

        private fun customize(authReq: OAuth2AuthorizationRequest?, req: HttpServletRequest) = authReq?.let {
            val base = it.authorizationUri.substringBefore("/oauth2/")
            val path = if (req.getParameter("type") == "signup") "signup" else "login"
            OAuth2AuthorizationRequest.from(it)
                .authorizationUri("$base/s/${props.serviceUid}/$path")
                .additionalParameters(it.additionalParameters + ("prompt" to "consent"))
                .build()
        }
    }
}
