package br.com.recargapay.wallet_service.infrastructure.adapter.out.messaging.event;

import br.com.recargapay.wallet_service.infrastructure.configuration.RequestIdFilterConfig;
import org.slf4j.MDC;

public abstract class BaseEvent {

    private final String requestId;

    public BaseEvent() {
        this.requestId = MDC.get(RequestIdFilterConfig.REQUEST_ID_HEADER);
    }

    public String getRequestId() {
        return requestId;
    }

}
