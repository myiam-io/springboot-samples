package io.myiam.samples.quickstart

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "myiam")
data class MyiamProperties(val baseUrl: String, val serviceUid: String, val apiKey: String)
