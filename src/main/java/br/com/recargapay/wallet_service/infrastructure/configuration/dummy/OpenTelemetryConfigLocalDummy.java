package br.com.recargapay.wallet_service.infrastructure.configuration.dummy;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.logs.data.LogRecordData;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.metrics.InstrumentType;
import io.opentelemetry.sdk.metrics.data.AggregationTemporality;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Collection;

@Configuration
@Profile("local")
public class OpenTelemetryConfigLocalDummy {

    @Bean
    public SpanExporter otlpSpanExporterDummy() {
        return new SpanExporter() {
            @Override
            public CompletableResultCode export(Collection<SpanData> spans) {
                System.out.println("[DUMMY SPAN EXPORTER] export called with spans=" + spans.size());
                return CompletableResultCode.ofSuccess();
            }

            @Override
            public CompletableResultCode flush() {
                return CompletableResultCode.ofSuccess();
            }

            @Override
            public CompletableResultCode shutdown() {
                return CompletableResultCode.ofSuccess();
            }
        };
    }

    @Bean
    public LogRecordExporter otlpLogRecordExporterDummy() {
        return new LogRecordExporter() {
            @Override
            public CompletableResultCode export(Collection<LogRecordData> logs) {
                System.out.println("[DUMMY LOG EXPORTER] export called with logs=" + logs.size());
                return CompletableResultCode.ofSuccess();
            }

            @Override
            public CompletableResultCode flush() {
                return CompletableResultCode.ofSuccess();
            }

            @Override
            public CompletableResultCode shutdown() {
                return CompletableResultCode.ofSuccess();
            }
        };
    }

    @Bean
    public MetricExporter otlpMetricExporterDummy() {
        return new MetricExporter() {
            @Override
            public CompletableResultCode export(Collection<MetricData> metrics) {
                System.out.println("[DUMMY METRIC EXPORTER] export called with metrics=" + metrics.size());
                return CompletableResultCode.ofSuccess();
            }

            @Override
            public AggregationTemporality getAggregationTemporality(InstrumentType instrumentType) {
                return AggregationTemporality.CUMULATIVE;
            }

            @Override
            public CompletableResultCode flush() {
                return CompletableResultCode.ofSuccess();
            }

            @Override
            public CompletableResultCode shutdown() {
                return CompletableResultCode.ofSuccess();
            }
        };
    }

    @Bean
    public SdkTracerProvider sdkTracerProvider() {
        return SdkTracerProvider.builder().build();
    }
}
