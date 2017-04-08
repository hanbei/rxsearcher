package de.hanbei.rxsearch.searcher.duckduckgo;

import com.ning.http.client.Request;
import de.hanbei.rxsearch.model.Query;
import de.hanbei.rxsearch.model.User;
import de.hanbei.rxsearch.searcher.RequestUrlBuilderTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DuckDuckGoRequestBuilderTest extends RequestUrlBuilderTest {

    @Before
    public void setUp() {
        urlBuilder = new DuckDuckGoRequestBuilder();
    }

    @Test
    public void correctRequestUrlIsBuilt() {
        Request input = urlBuilder.createRequest(Query.builder().keywords("input").requestId("id").country("de").user(User.getDefaultUser()).build());
        assertThat(input.getUrl(), is("http://api.duckduckgo.com?format=json&t=hanbeirxsearch&q=input"));
    }

}