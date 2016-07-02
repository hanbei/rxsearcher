package de.hanbei.rxsearch.model;

import java.util.Currency;

public class OfferBuilder {

    public interface UrlStep {
        TitleStep url(String url);
    }

    public interface TitleStep {
        PriceStep title(String url);
    }

    public interface PriceStep {
        SearcherStep price(Double amount, Currency currency);

        SearcherStep price(Double amount, String currency);

        SearcherStep price(Money price);
    }

    public interface SearcherStep {
        OtherStep searcher(String url);
    }

    public interface OtherStep extends BuildStep {
        OtherStep category(String category);

        OtherStep image(String image);

        OtherStep manufacturer(String manufacturer);

        OtherStep merchant(Merchant merchant);

        OtherStep description(String description);

        OtherStep originalUrl(String originalUrl);

        OtherStep shippingCosts(Money shippingCosts);
        OtherStep brand(String brand);

    }

    public interface BuildStep {
        Offer build();
    }

    static class Steps implements UrlStep, TitleStep, OtherStep, PriceStep, SearcherStep, BuildStep {
        Money price;
        String url;
        String productName;
        String searcher;
        String manufacturer;
        Merchant merchant;

        String category = "";
        String image = "";
        String description = "";
        String originalUrl;
        Money shippingCosts = null;
        String brand = null;

        public SearcherStep price(Double amount, Currency currency) {
            price = new Money(amount, currency);
            return this;
        }

        public SearcherStep price(Double amount, String currency) {
            price = new Money(amount, currency);
            return this;
        }

        public SearcherStep price(Money price) {
            this.price = price;
            return this;
        }

        public TitleStep url(String url) {
            this.url = url;
            return this;
        }

        public PriceStep title(String productName) {
            this.productName = productName;
            return this;
        }

        public OtherStep searcher(String searcher) {
            this.searcher = searcher;
            return this;
        }

        public OtherStep category(String category) {
            this.category = category;
            return this;
        }

        public OtherStep image(String image) {
            this.image = image;
            return this;
        }

        @Override
        public OtherStep manufacturer(String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        @Override
        public OtherStep merchant(Merchant merchant) {
            this.merchant = merchant;
            return this;
        }

        @Override
        public OtherStep description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public OtherStep originalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
            return this;
        }

        @Override
        public OtherStep shippingCosts(Money shippingCosts) {
            this.shippingCosts = shippingCosts;
            return this;
        }

        @Override
        public OtherStep brand(String brand) {
            this.brand = brand;
            return this;
        }

        public Offer build() {
            return new Offer(this);
        }
    }

    private OfferBuilder() {
        // not allowed to be constructed.
    }

}