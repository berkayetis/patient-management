package com.berkayyetis.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import com.berkayyetis.billingservice.metrics.BillingMetrics;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase {
    private final BillingMetrics metrics;
    private static final Logger log = LoggerFactory.getLogger(BillingGrpcService.class.getName());

    public BillingGrpcService(BillingMetrics metrics) {
        this.metrics = metrics;
    }

    @Override
    public void createBillingAccount(BillingRequest request, StreamObserver<BillingResponse> responseObserver) {
        metrics.record(() -> {
            try {
                log.info("createBillingAccount request received: {}", request);

                simulateWork();

                BillingResponse response = BillingResponse.newBuilder()
                        .setAccountId("12345")
                        .setStatus("ACTIVE")
                        .build();

                responseObserver.onNext(response);
                responseObserver.onCompleted();

                metrics.success();
                return null;

            } catch (Exception e) {
                metrics.failure();
                responseObserver.onError(e);
                throw e;
            }
        });
    }
    private void simulateWork() {
        try {
            Thread.sleep((long) (Math.random() * 500));
        } catch (InterruptedException ignored) {}
    }
}
