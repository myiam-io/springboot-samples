package io.myiam.samples.quickstart

import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

@RestController
class ApiController(props: MyiamProperties) {
    private val api = RestClient.builder()
        .baseUrl(props.baseUrl)
        .defaultHeader("My-Key", "KEY ${props.apiKey}")
        .defaultHeader("My-Service", "UID ${props.serviceUid}")
        .build()

    @GetMapping("/api/token/info")
    fun tokenInfo(@RegisteredOAuth2AuthorizedClient("myiam") c: OAuth2AuthorizedClient): Any =
        api.get().uri("/api/v0/token/info")
            .header("Authorization", "Bearer ${c.accessToken.tokenValue}")
            .retrieve().body(Any::class.java)!!

    @GetMapping("/api/debug/oidc-user")
    fun oidcUser(@AuthenticationPrincipal user: OidcUser) = mapOf(
        "subject" to user.subject, "name" to user.name,
        "claims" to user.claims, "authorities" to user.authorities.map { it.authority },
    )

    @ExceptionHandler(RestClientResponseException::class)
    fun error(e: RestClientResponseException): ResponseEntity<String> =
        ResponseEntity.status(e.statusCode).body(e.responseBodyAsString)
}
