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
        OtherStep productCategory(String category);

        OtherStep image(String image);
    }

    public interface BuildStep {
        Offer build();
    }

    static class Steps implements UrlStep, TitleStep, OtherStep, PriceStep, SearcherStep, BuildStep {
        Money price;
        String url;
        String productName;
        String searcher;
        String category = "";
        String image = "";

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

        public OtherStep productCategory(String category) {
            this.category = category;
            return this;
        }

        public OtherStep image(String image) {
            this.image = image;
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