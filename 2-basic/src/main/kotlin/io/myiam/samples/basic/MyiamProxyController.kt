package io.myiam.samples.basic

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientResponseException

@RestController
@RequestMapping("/api/myiam")
class MyiamProxyController(props: MyiamProperties) {

    private val api = RestClient.builder()
        .baseUrl(props.baseUrl)
        .defaultHeader("My-Key", "KEY ${props.apiKey}")
        .defaultHeader("My-Service", "UID ${props.serviceUid}")
        .build()

    // --- Token API ---

    @GetMapping("/token/info")
    fun getTokenInfo(@RegisteredOAuth2AuthorizedClient("myiam") c: OAuth2AuthorizedClient): Any =
        authed(c).get().uri("/api/v0/token/info").retrieve().body(Any::class.java)!!

    @PostMapping("/token/delete")
    fun deleteToken(
        @RegisteredOAuth2AuthorizedClient("myiam") c: OAuth2AuthorizedClient,
        @RequestParam(required = false) target: String?,
        @RequestParam(required = false) refresh_token_delete: Boolean?,
    ): Any = authed(c).post()
        .uri { b ->
            b.path("/api/v0/token/delete")
            target?.let { b.queryParam("target", it) }
            refresh_token_delete?.let { b.queryParam("refresh_token_delete", it) }
            b.build()
        }
        .retrieve().body(Any::class.java)!!

    // --- User API ---

    @GetMapping("/user/me")
    fun getUser(@RegisteredOAuth2AuthorizedClient("myiam") c: OAuth2AuthorizedClient): Any =
        authed(c).get().uri("/api/v0/user/me").retrieve().body(Any::class.java)!!

    @PostMapping("/user/action")
    fun prepareUserAction(
        @RegisteredOAuth2AuthorizedClient("myiam") c: OAuth2AuthorizedClient,
        @RequestParam type: String,
        @RequestBody body: Map<String, Any>,
    ): Any = authed(c).post()
        .uri("/api/v0/user/prepare/$type")
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .retrieve().body(Any::class.java)!!

    // --- Service User API ---

    @GetMapping("/service-user/get")
    fun getServiceUser(
        @RegisteredOAuth2AuthorizedClient("myiam") c: OAuth2AuthorizedClient,
        @RequestParam serviceUserUid: String,
    ): Any = authed(c).get()
        .uri("/api/v0/service-user/get?serviceUserUid={uid}", serviceUserUid)
        .retrieve().body(Any::class.java)!!

    @GetMapping("/service-user/profile/get")
    fun getServiceUserProfile(
        @RegisteredOAuth2AuthorizedClient("myiam") c: OAuth2AuthorizedClient,
        @RequestParam serviceUserUid: String,
    ): Any = authed(c).get()
        .uri("/api/v0/service-user/profile/get?serviceUserUid={uid}", serviceUserUid)
        .retrieve().body(Any::class.java)!!

    // --- Helper ---

    private fun authed(c: OAuth2AuthorizedClient) = api.mutate()
        .defaultHeader("Authorization", "Bearer ${c.accessToken.tokenValue}")
        .build()

    @ExceptionHandler(RestClientResponseException::class)
    fun error(e: RestClientResponseException): ResponseEntity<String> =
        ResponseEntity.status(e.statusCode)
            .contentType(MediaType.APPLICATION_JSON)
            .body(e.responseBodyAsString)
}
