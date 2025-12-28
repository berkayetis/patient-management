package com.berkayyetis.billingservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class BillingMetrics {

    private final Counter requestTotal;
    private final Counter requestSuccess;
    private final Counter requestFailure;
    private final Timer requestLatency;

    public BillingMetrics(MeterRegistry registry) {
        this.requestTotal = Counter.builder("billing.grpc.requests.total")
                .description("Billing gRPC toplam istek sayısı")
                .register(registry);

        this.requestSuccess = Counter.builder("billing.grpc.requests.success.total")
                .description("Başarılı billing gRPC istekleri")
                .register(registry);

        this.requestFailure = Counter.builder("billing.grpc.requests.failure.total")
                .description("Hatalı billing gRPC istekleri")
                .register(registry);

        this.requestLatency = Timer.builder("billing.grpc.request.duration")
                .description("Billing gRPC işlem süresi")
                .publishPercentiles(0.95, 0.99)
                .register(registry);
    }

    public <T> T record(Supplier<T> supplier) {
        requestTotal.increment();
        return requestLatency.record(supplier);
    }

    public void success() {
        requestSuccess.increment();
    }

    public void failure() {
        requestFailure.increment();
    }
}

