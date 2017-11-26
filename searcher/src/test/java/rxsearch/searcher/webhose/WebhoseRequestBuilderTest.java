package rxsearch.searcher.webhose;

import rxsearch.searcher.RequestUrlBuilderTest;
import okhttp3.Request;
import org.junit.Before;
import org.junit.Test;
import rxsearch.searcher.SearcherRequest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WebhoseRequestBuilderTest extends RequestUrlBuilderTest {

    @Before
    public void setUp() {
        urlBuilder = new WebhoseRequestBuilder("some-key");
    }

    @Test
    public void emptyKeyViolatesNotEmptyPrecondition() {
        expectedException.expect(IllegalArgumentException.class);
        new WebhoseRequestBuilder("");
    }

    @Test
    public void nullKeyViolatesNotEmptyPrecondition() {
        expectedException.expect(IllegalArgumentException.class);
        new WebhoseRequestBuilder(null);
    }

    @Test
    public void correctRequestUrlIsBuilt() {
        SearcherRequest input = urlBuilder.createRequest(query);
        assertThat(input.url(), is("https://webhose.io/search?q=input&token=some-key&format=json&size=10"));
    }

}