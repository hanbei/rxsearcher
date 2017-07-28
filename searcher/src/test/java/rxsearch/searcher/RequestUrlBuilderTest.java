package rxsearch.searcher;

import rxsearch.model.Query;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public abstract class RequestUrlBuilderTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    protected RequestBuilder urlBuilder;

    protected final Query query = Query.builder().keywords("input").requestId("id").country("de").build();

}
