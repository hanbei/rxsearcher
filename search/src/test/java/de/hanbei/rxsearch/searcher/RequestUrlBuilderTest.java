package de.hanbei.rxsearch.searcher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public abstract class RequestUrlBuilderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    protected RequestUrlBuilder urlBuilder;

    @Test
    public void emptyInputViolatesPrecondition() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        urlBuilder.createRequestUrl("");
    }

    @Test
    public void nullInputViolatesPrecondition() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        urlBuilder.createRequestUrl("");
    }
}
