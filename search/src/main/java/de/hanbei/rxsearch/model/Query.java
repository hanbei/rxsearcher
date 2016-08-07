package de.hanbei.rxsearch.model;

import java.util.Objects;

public class Query {

    private final String keywords;
    private final String requestId;
    private final String country;

    Query(String keywords, String requestId, String country) {
        this.keywords = keywords;
        this.requestId = requestId;
        this.country = country;
    }

    public static QueryBuilder.KeywordStep builder() {
        return new QueryBuilder.Steps();
    }

    public String keywords() {
        return keywords;
    }

    public String requestId() {
        return requestId;
    }

    public String country() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Query query = (Query) o;
        return Objects.equals(keywords, query.keywords) &&
                Objects.equals(requestId, query.requestId) &&
                Objects.equals(country, query.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keywords, requestId, country);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{\"Query\":{")
                .append("\"keywords\":\"").append(keywords).append('"')
                .append(", ")
                .append("\"requestId\":\"").append(requestId).append('"')
                .append(", ")
                .append("\"country\":\"").append(country).append('"')
                .append("}}");
        return sb.toString();
    }
}
