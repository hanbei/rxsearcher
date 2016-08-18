package de.hanbei.rxsearch.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import java.util.Currency;

public class QueryBuilder {

    public interface KeywordStep {
        RequestIdStep keywords(String keywords);
    }

    public interface RequestIdStep {
        CountryStep requestId(String requestId);
    }

    public interface CountryStep {
        OtherStep country(String country);
    }

    public interface OtherStep extends BuildStep {
        OtherStep price(Money price);

        OtherStep price(Double amount, String currency);

        OtherStep price(Double amount, Currency currency);
    }


    public interface BuildStep {
        Query build();
    }

    static class Steps implements KeywordStep, RequestIdStep, CountryStep, BuildStep, OtherStep {

        private String keywords;
        private String requestId;
        private String country;
        private Money price;

        @Override
        public RequestIdStep keywords(String keywords) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(keywords));
            this.keywords = keywords;
            return this;
        }

        @Override
        public CountryStep requestId(String requestId) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(requestId));
            this.requestId = requestId;
            return this;
        }

        @Override
        public OtherStep country(String country) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(country));
            this.country = country;
            return this;
        }

        @Override
        public Query build() {
            return new Query(keywords, requestId, country, price);
        }

        @Override
        public OtherStep price(Money price) {
            this.price = price;
            return this;
        }

        @Override
        public OtherStep price(Double amount, String currency) {
            return price(new Money(amount, currency));
        }

        @Override
        public OtherStep price(Double amount, Currency currency) {
            return price(new Money(amount, currency));
        }
    }

    private QueryBuilder() {
        // construction via buildee
    }
}
