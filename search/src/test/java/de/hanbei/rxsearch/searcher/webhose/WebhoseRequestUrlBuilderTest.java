package de.hanbei.rxsearch.searcher.webhose;

import com.ning.http.client.Request;
import de.hanbei.rxsearch.searcher.RequestUrlBuilderTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class WebhoseRequestUrlBuilderTest extends RequestUrlBuilderTest {

    @Before
    public void setUp() throws Exception {
        urlBuilder = new WebhoseRequestUrlBuilder("some-key");
    }


    @Test
    public void emptyKeyViolatesNotEmptyPrecondition() {
        expectedException.expect(IllegalArgumentException.class);
        new WebhoseRequestUrlBuilder("");
    }

    @Test
    public void nullKeyViolatesNotEmptyPrecondition() {
        expectedException.expect(IllegalArgumentException.class);
        new WebhoseRequestUrlBuilder(null);
    }


    @Test
    public void correctRequestUrlIsBuilt() throws Exception {
        Request input = urlBuilder.createRequestUrl("input");
        assertThat(input.getUrl(), is("https://webhose.io/search?q=input&token=some-key&format=json&size=10"));
    }

}