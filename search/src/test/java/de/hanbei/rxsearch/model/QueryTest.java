package de.hanbei.rxsearch.model;

import org.junit.Test;

public class QueryTest {

    @Test(expected = IllegalArgumentException.class)
    public void emptyKeywordsViolatesPrecondition() {
        new Query("", "id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullKeywordsViolatesPrecondition() {
        new Query(null, "id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyIdViolatesPrecondition() {
        new Query("keyword", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullIdViolatesPrecondition() {
        new Query("keyword", null);
    }

}