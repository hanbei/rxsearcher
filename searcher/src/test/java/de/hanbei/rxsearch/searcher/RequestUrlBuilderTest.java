package de.hanbei.rxsearch.searcher;

import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.model.User;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public abstract class RequestUrlBuilderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    protected RequestBuilder urlBuilder;

    protected final Query query = Query.builder().keywords("input").requestId("id").country("de").user(User.getDefaultUser()).build();

}
