package de.hanbei.rxsearch.model;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class Query {

    private final String keywords;
    private final String requestId;

    public Query(String keywords, String requestId) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(keywords));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(requestId));
        this.keywords = keywords;
        this.requestId = requestId;
    }

    public String keywords() {
        return keywords;
    }

    public String requestId() {
        return requestId;
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

        if (!keywords.equals(query.keywords)) {
            return false;
        }
        return requestId.equals(query.requestId);

    }

    @Override
    public int hashCode() {
        int result = keywords.hashCode();
        result = 31 * result + requestId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Query{" +
                "requestId='" + requestId + '\'' +
                ", keywords='" + keywords + '\'' +
                '}';
    }
}
