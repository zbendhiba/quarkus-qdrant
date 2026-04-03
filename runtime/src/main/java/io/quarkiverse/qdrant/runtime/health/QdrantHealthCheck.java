package io.quarkiverse.qdrant.runtime.health;

import java.net.URI;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import io.quarkiverse.qdrant.runtime.QdrantHealthApi;
import io.quarkiverse.qdrant.runtime.QdrantRestClientConfig;

@Readiness
@ApplicationScoped
public class QdrantHealthCheck implements HealthCheck {

    @Inject
    QdrantRestClientConfig config;

    @Override
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named("Qdrant REST Client health check");
        try {
            String scheme = config.useTls() ? "https" : "http";
            URI baseUri = URI.create(scheme + "://" + config.host() + ":" + config.port());
            QdrantHealthApi client = RestClientBuilder.newBuilder()
                    .baseUri(baseUri)
                    .build(QdrantHealthApi.class);
            client.healthz();
            builder.up();
        } catch (Exception e) {
            return builder.down().withData("reason", e.getMessage()).build();
        }
        return builder.build();
    }
}
