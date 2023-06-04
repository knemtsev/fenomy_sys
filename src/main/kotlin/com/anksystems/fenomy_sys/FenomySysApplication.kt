package com.anksystems.fenomy_sys

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.*


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

@Configuration
class CorsConfig {
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/sys/v1/client/**")
                    .allowedOrigins("https://localhost:3000")
                    .allowedMethods("GET", "POST", "OPTIONS")
                    .allowedHeaders("Content-Type", "Access-Control-Allow-Headers")
                    .allowCredentials(true)
                    .maxAge(3600)
            }
        }
    }
}
/*
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
*/


fun main(args: Array<String>) {
    runApplication<FenomySysApplication>(*args)
}
