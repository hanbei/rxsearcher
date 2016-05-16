package de.hanbei.rxsearch.searcher.github;

import com.ning.http.client.Request;
import de.hanbei.rxsearch.searcher.RequestUrlBuilderTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GithubRequestBuilderTest extends RequestUrlBuilderTest {

    @Before
    public void setUp() throws Exception {
        urlBuilder = new GithubRequestBuilder("test/repo");
    }

    @Test
    public void emptyKeyViolatesNotEmptyPrecondition() {
        expectedException.expect(IllegalArgumentException.class);
        new GithubRequestBuilder("");
    }

    @Test
    public void nullKeyViolatesNotEmptyPrecondition() {
        expectedException.expect(IllegalArgumentException.class);
        new GithubRequestBuilder(null);
    }


    @Test
    public void correctRequestUrlIsBuilt() throws Exception {
        Request input = urlBuilder.createRequest("input");
        assertThat(input.getUrl(), is("https://api.github.com/search/code?q=input+repo:test/repo"));
    }

}