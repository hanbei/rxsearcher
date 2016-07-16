package de.hanbei.rxsearch.searcher.webhose;

import com.ning.http.client.Request;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.searcher.RequestUrlBuilderTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WebhoseRequestBuilderTest extends RequestUrlBuilderTest {

    @Before
    public void setUp() throws Exception {
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
    public void correctRequestUrlIsBuilt() throws Exception {
        Request input = urlBuilder.createRequest(new Query("input", "id"));
        assertThat(input.getUrl(), is("https://webhose.io/search?q=input&token=some-key&format=json&size=10"));
    }

}