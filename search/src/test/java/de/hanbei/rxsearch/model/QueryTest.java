package de.hanbei.rxsearch.model;

public class QueryTest extends EqualsHashcodeTest<Query> {

    private static final String CURRENCY = "EUR";
    private static final String COUNTRY = "de";
    private static final String ID = "id";
    private static final String KEYWORDS = "keywords";

    @Override
    protected Query createEqual() {
        return Query.builder().keywords(KEYWORDS).requestId(ID).country(COUNTRY).build();
    }

    @Override
    protected Query createOther() {
        return Query.builder().keywords("other_keywords").requestId("other_id").country("_other_country").build();
    }

    @Override
    protected Query createOtherEqual() {
        return Query.builder().keywords(KEYWORDS).requestId(ID).country(COUNTRY).build();
    }
}