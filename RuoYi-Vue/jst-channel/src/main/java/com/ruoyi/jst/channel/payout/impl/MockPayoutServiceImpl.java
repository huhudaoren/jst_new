package com.ruoyi.jst.channel.payout.impl;

import com.ruoyi.jst.channel.payout.PayoutService;
import org.springframework.stereotype.Service;

/**
 * Mock payout implementation for local integration.
 */
@Service
public class MockPayoutServiceImpl implements PayoutService {

    @Override
    public String mode() {
        return "mock";
    }

    @Override
    public PayoutResult payout(PayoutRequest request) {
        PayoutResult result = new PayoutResult();
        result.setSuccess(true);
        result.setProviderNo("MOCK_PAYOUT_" + request.getSettlementId());
        result.setVoucherUrl("https://fixture.example.com/payout/" + request.getPayNo() + ".png");
        result.setMessage("mock payout success");
        return result;
    }
}
