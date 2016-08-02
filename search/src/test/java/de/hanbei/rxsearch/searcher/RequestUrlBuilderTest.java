package de.hanbei.rxsearch.searcher;

import de.hanbei.rxsearch.model.Query;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public abstract class RequestUrlBuilderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    protected RequestBuilder urlBuilder;

    @Test
    public void emptyInputViolatesPrecondition() {
        expectedException.expect(IllegalArgumentException.class);
        urlBuilder.createRequest(new Query("", "id"));
    }

    @Test
    public void nullInputViolatesPrecondition() {
        expectedException.expect(IllegalArgumentException.class);
        urlBuilder.createRequest(new Query("", "id"));
    }
}
