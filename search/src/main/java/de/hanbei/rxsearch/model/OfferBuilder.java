package de.hanbei.rxsearch.model;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public final class OfferBuilder {

    private OfferBuilder() {
        // construction not allowed
    }

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

        OtherStep shippingCosts(Double amount, Currency currency);

        OtherStep shippingCosts(Double amount, String currency);

        OtherStep brand(String brand);

        OtherStep eec(Offer.EEC eec);

        OtherStep type(Offer.Type type);

        OtherStep availability(Offer.Availability availability);

        OtherStep ecpc(Money ecpc);

        OtherStep ecpc(Double amount, Currency currency);

        OtherStep ecpc(Double amount, String currency);

        OtherStep listPrice(Money ecpc);

        OtherStep listPrice(Double amount, Currency currency);

        OtherStep listPrice(Double amount, String currency);

        OtherStep ean(String ean);

        OtherStep mpn(String mpn);

        OtherStep upc(String upc);

        OtherStep gtin(String gtin);

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
        Offer.EEC eec;
        Offer.Type type;
        Offer.Availability availability;
        Money ecpc;
        Money listPrice;
        List<String> eans = new ArrayList<>();
        List<String> mpns = new ArrayList<>();
        List<String> gtins = new ArrayList<>();
        List<String> upcs = new ArrayList<>();

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
        public OtherStep shippingCosts(Double amount, Currency currency) {
            return shippingCosts(new Money(amount, currency));
        }

        @Override
        public OtherStep shippingCosts(Double amount, String currency) {
            return shippingCosts(new Money(amount, currency));
        }

        @Override
        public OtherStep brand(String brand) {
            this.brand = brand;
            return this;
        }

        @Override
        public OtherStep eec(Offer.EEC eec) {
            this.eec = eec;
            return this;
        }

        @Override
        public OtherStep type(Offer.Type type) {
            this.type = type;
            return this;
        }

        @Override
        public OtherStep availability(Offer.Availability availability) {
            this.availability = availability;
            return this;
        }

        @Override
        public OtherStep ecpc(Money ecpc) {
            this.ecpc = ecpc;
            return this;
        }

        @Override
        public OtherStep ecpc(Double amount, Currency currency) {
            return ecpc(new Money(amount, currency));
        }

        @Override
        public OtherStep ecpc(Double amount, String currency) {
            return ecpc(new Money(amount, currency));
        }

        @Override
        public OtherStep listPrice(Money listPrice) {
            this.listPrice = listPrice;
            return this;
        }

        @Override
        public OtherStep listPrice(Double amount, Currency currency) {
            return listPrice(new Money(amount, currency));
        }

        @Override
        public OtherStep listPrice(Double amount, String currency) {
            return listPrice(new Money(amount, currency));
        }

        @Override
        public OtherStep ean(String ean) {
            eans.add(ean);
            return this;
        }

        @Override
        public OtherStep mpn(String mpn) {
            mpns.add(mpn);
            return this;

        }

        @Override
        public OtherStep upc(String upc) {
            upcs.add(upc);
            return this;
        }

        @Override
        public OtherStep gtin(String gtin) {
            gtins.add(gtin);
            return this;
        }

        public Offer build() {
            return new Offer(this);
        }
    }

}