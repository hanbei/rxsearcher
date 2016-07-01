package de.hanbei.rxsearch.model;

public class Merchant {

    private final String name;

    private final String merchantImage;

    private final String merchantId;

    private final String merchantCategory;

    private Merchant() {
        this("", "", "", "");
    }

    Merchant(String name, String merchantImage, String merchantId, String merchantCategory) {
        this.name = name;
        this.merchantImage = merchantImage;
        this.merchantId = merchantId;
        this.merchantCategory = merchantCategory;
    }

    public String getName() {
        return name;
    }

    public String getMerchantImage() {
        return merchantImage;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getMerchantCategory() {
        return merchantCategory;
    }
}
