package de.hanbei.rxsearch.searcher.duckduckgo;

import de.hanbei.rxsearch.searcher.RequestUrlBuilderTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DuckDuckGoRequestUrlBuilderTest extends RequestUrlBuilderTest {

    @Before
    public void setUp() {
        urlBuilder = new DuckDuckGoRequestUrlBuilder();
    }

    @Test
    public void correctRequestUrlIsBuilt() throws Exception {
        String input = urlBuilder.createRequestUrl("input");
        assertThat(input, is("http://api.duckduckgo.com/?format=json&t=hanbeirxsearch&q=input"));
    }


}