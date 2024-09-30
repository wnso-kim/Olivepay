package kr.co.olivepay.core.transaction.topic.event.coupon.result;

import lombok.Builder;

@Builder
public record CouponTransferRollBackCompleteEvent(
        Long couponUserId
) {
}
