package io.myiam.samples.quickstart

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
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
    fun dashboard(@RegisteredOAuth2AuthorizedClient("myiam") client: OAuth2AuthorizedClient, model: Model): String {
        model.addAttribute("accessTokenPreview", client.accessToken.tokenValue.take(20) + "...")
        model.addAttribute("expiresAt", client.accessToken.expiresAt
            ?.atZone(ZoneId.of("Asia/Seoul"))
            ?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) ?: "N/A")
        return "dashboard"
    }
}
