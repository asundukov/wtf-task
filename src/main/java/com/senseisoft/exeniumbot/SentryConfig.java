package com.senseisoft.exeniumbot;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("file:${settingsDir}/sentry.properties")
public class SentryConfig {

    @Value("${dsn}")
    private String dsn;

    @Value("${environment}")
    private String env;

    @Bean
    public SentryClient getSentry() {
        SentryClient client = Sentry.init(dsn);
        client.addTag("environment", env);
        return client;
    }
}
