package com.alphaseries.game.catalog;

import com.alphaseries.dao.mysql.UserDao;
import com.alphaseries.dao.mysql.VoucherDao;
import com.alphaseries.messages.outgoing.VoucherPayloads;
import com.alphaseries.util.StringUtils;

public record VoucherRedemption(
    String responsePayload,
    boolean redeemed,
    boolean creditsRefreshRequired,
    boolean activityPointRefreshRequired
) {
    public VoucherRedemption {
        responsePayload = StringUtils.text(responsePayload);
    }

    /**
     * Original function: Proc_6_137_766470.
     */
    public static VoucherRedemption redeem(
        String voucherCode,
        long userId,
        VoucherDao vouchers,
        UserDao users,
        ProductCache productCache
    ) {
        try {
            if (StringUtils.text(voucherCode).length() != 8 || userId <= 0L
                || vouchers == null || users == null || productCache == null) {
                return invalid(voucherCode);
            }
            VoucherDao.VoucherReward voucherReward = vouchers.reward(voucherCode).orElse(null);
            if (voucherReward == null) {
                return invalid(voucherCode);
            }
            long productId = rewardProductId(voucherReward, vouchers);
            long creditsValue = voucherReward.credits();
            long shellsValue = voucherReward.shells();
            if (creditsValue != 0L) {
                users.addCredits(userId, creditsValue);
            }
            if (shellsValue != 0L) {
                users.addActivityPoints(userId, 0L, shellsValue);
            }
            vouchers.deleteVoucher(voucherCode);
            return new VoucherRedemption(
                VoucherPayloads.redeemed(productCache, productId),
                true,
                creditsValue != 0L,
                shellsValue != 0L);
        } catch (Exception ignored) {
            return invalid(voucherCode);
        }
    }

    private static VoucherRedemption invalid(String voucherCode) {
        return new VoucherRedemption(VoucherPayloads.invalid(voucherCode), false, false, false);
    }

    private static long rewardProductId(VoucherDao.VoucherReward voucherReward, VoucherDao vouchers)
        throws Exception {

        String productSprite = StringUtils.text(voucherReward.productSprite());
        if (productSprite.length() <= 2) {
            return 0L;
        }
        return vouchers.catalogProductProductIdBySprite(productSprite);
    }
}
