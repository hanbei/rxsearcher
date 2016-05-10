package de.hanbei.rxsearch.searcher.duckduckgo;

import com.ning.http.client.Request;
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
        Request input = urlBuilder.createRequestUrl("input");
        assertThat(input.getUrl(), is("http://api.duckduckgo.com?format=json&t=hanbeirxsearch&q=input"));
    }


}