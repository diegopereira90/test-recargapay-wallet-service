package br.com.recargapay.wallet_service.infrastructure.adapter.out.persistence.entity;

import br.com.recargapay.wallet_service.infrastructure.configuration.RequestIdFilterConfig;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.slf4j.MDC;

@MappedSuperclass
public abstract class AuditableEntity {

    @Column(name = "request_id", updatable = false)
    private String requestId;

    @PrePersist
    public void prePersist() {
        this.requestId = MDC.get(RequestIdFilterConfig.REQUEST_ID_HEADER);
    }

    @PreUpdate
    public void preUpdate() {
        if (this.requestId == null) {
            this.requestId = MDC.get(RequestIdFilterConfig.REQUEST_ID_HEADER);
        }
    }

    public String getRequestId() {
        return requestId;
    }
}
