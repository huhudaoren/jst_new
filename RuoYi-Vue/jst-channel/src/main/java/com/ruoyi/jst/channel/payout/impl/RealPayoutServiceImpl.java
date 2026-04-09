package com.ruoyi.jst.channel.payout.impl;

import com.ruoyi.jst.channel.payout.PayoutService;
import org.springframework.stereotype.Service;

/**
 * Placeholder for real payout integration.
 */
@Service
public class RealPayoutServiceImpl implements PayoutService {

    @Override
    public String mode() {
        return "real";
    }

    @Override
    public PayoutResult payout(PayoutRequest request) {
        throw new UnsupportedOperationException("Real payout is not implemented yet");
    }
}
