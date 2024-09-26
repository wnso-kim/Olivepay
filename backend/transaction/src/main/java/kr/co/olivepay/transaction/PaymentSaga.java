package kr.co.olivepay.transaction;

import kr.co.olivepay.core.transaction.topic.event.payment.PaymentCreateEvent;
import kr.co.olivepay.transaction.publisher.TransactionEventPublisher;
import kr.co.olivepay.transaction.state.PaymentPending;
import kr.co.olivepay.transaction.state.PaymentState;
import lombok.Getter;
import lombok.Setter;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
public class PaymentSaga {

    private String key;
    private Long paymentId;
    private Long memberId;
    private Long franchiseId;
    private List<PaymentDetailSaga> paymentDetailSagaList;
    private TransactionEventPublisher eventPublisher;
    private PaymentState state;


    private PaymentSaga(
        String key,
        Long paymentId,
        Long memberId,
        Long franchiseId,
        List<PaymentDetailSaga> paymentDetailSagaList,
        TransactionEventPublisher eventPublisher,
        PaymentState state) {
        this.key = key;
        this.paymentId = paymentId;
        this.memberId = memberId;
        this.franchiseId = franchiseId;
        this.paymentDetailSagaList = paymentDetailSagaList;
        this.eventPublisher = eventPublisher;
        this.state = state;
    }

    public static PaymentSaga init(String key, PaymentCreateEvent event, TransactionEventPublisher eventPublisher) {
        List<PaymentDetailSaga> paymentDetailSagaList = event.paymentDetailCreateEventList()
                                                             .stream()
                                                             .map(PaymentDetailSaga::toPaymentDetailSage)
                                                             .toList();
        return new PaymentSaga(
            key,
            event.paymentId(),
            event.memberId(),
            event.franchiseId(),
            paymentDetailSagaList,
            eventPublisher,
            new PaymentPending()
        );
    }

    public void setStateAndOperate(PaymentState paymentState) {
        this.state = paymentState;
        this.operate();
    }

    public void operate() {
        state.operate(this);
    }

    public CompletableFuture<SendResult<String, String>> publishEvent(String topic, String key, Object event) {
        return eventPublisher.publishEvent(topic, key, event);
    }
}
