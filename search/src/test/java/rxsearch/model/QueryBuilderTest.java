package rxsearch.model;

import org.junit.Test;

public class QueryBuilderTest {

    @Test(expected = IllegalArgumentException.class)
    public void emptyKeywordsViolatesPrecondition() {
        Query.builder().keywords("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullKeywordsViolatesPrecondition() {
        Query.builder().keywords(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyIdViolatesPrecondition() {
        Query.builder().keywords("keyword").requestId("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullIdViolatesPrecondition() {
        Query.builder().keywords("keyword").requestId(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void emptyCountryViolatesPrecondition() {
        Query.builder().keywords("keyword").requestId("id").country("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCountryViolatesPrecondition() {
        Query.builder().keywords("keyword").requestId("id").country(null);
    }

}