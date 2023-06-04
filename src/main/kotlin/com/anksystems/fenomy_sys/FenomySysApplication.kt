package com.anksystems.fenomy_sys

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.HttpSecurityDsl
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.stereotype.Component
import org.springframework.web.cors.CorsConfiguration
import java.util.*
import java.util.List


@EnableConfigurationProperties(MyProperties::class)
@SpringBootApplication()
@ConfigurationPropertiesScan("com.anksystems.fenomy_sys")
@Component
class FenomySysApplication {
    private val versionProperties = Properties()

    init {
        versionProperties.load(this.javaClass.getResourceAsStream("/version.properties"))
    }

    fun getVersion() : String = versionProperties.getProperty("version") ?: "no version"

}

@EnableWebSecurity
class SecurityConfig {
    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        val corsConfiguration = CorsConfiguration()
        corsConfiguration.allowedHeaders = List.of("*")
        corsConfiguration.allowedOrigins = List.of("*")
        corsConfiguration.allowedMethods =
            List.of("GET", "POST", "PUT", "DELETE", "PUT", "OPTIONS", "PATCH", "DELETE")
        corsConfiguration.allowCredentials = true
        return http
            .cors(Customizer.withDefaults())
            .authorizeRequests { authorizeRequests ->
                authorizeRequests
                    .anyRequest().authenticated()
            }
            .build()
    }
}


fun main(args: Array<String>) {
    runApplication<FenomySysApplication>(*args)
}
