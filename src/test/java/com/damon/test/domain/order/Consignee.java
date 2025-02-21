package com.damon.test.domain.order;

import cn.hutool.core.util.PhoneUtil;
import lombok.Data;
import lombok.NonNull;

@Data
public class Consignee {

    private String name;
    private String shippingAddress;
    private String mobile;

    public Consignee(@NonNull String name, @NonNull String shippingAddress, @NonNull String mobile) {
        if (!PhoneUtil.isMobile(mobile)) {
            throw new IllegalArgumentException("无效的手机号码:" + mobile);
        }
        this.name = name;
        this.shippingAddress = shippingAddress;
        this.mobile = mobile;
    }

    public Consignee() {
    }

    public void changeShippingAddress(@NonNull String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
}
