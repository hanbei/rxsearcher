package de.hanbei.rxsearch.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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