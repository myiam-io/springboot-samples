package io.myiam.samples.basic

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Controller
class PageController {
    @GetMapping("/")
    fun home() = "index"

    @GetMapping("/dashboard")
    fun dashboard(
        @AuthenticationPrincipal user: OidcUser,
        @RegisteredOAuth2AuthorizedClient("myiam") client: OAuth2AuthorizedClient,
        model: Model,
    ): String {
        model.addAttribute("username", user.getAttribute<String>("username"))
        model.addAttribute("userUid", user.name)
        model.addAttribute("expiresAt", client.accessToken.expiresAt
            ?.atZone(ZoneId.of("Asia/Seoul"))
            ?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) ?: "N/A")
        return "dashboard"
    }
}
