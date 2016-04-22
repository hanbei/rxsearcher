package de.hanbei.rxsearch.searcher.webhose;

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
        String input = urlBuilder.createRequestUrl("input");
        assertThat(input, is("https://webhose.io/search?token=some-key&format=json&size=10&q=input"));
    }

}