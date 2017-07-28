package rxsearch.searcher.github;

import rxsearch.searcher.RequestUrlBuilderTest;
import okhttp3.Request;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GithubRequestBuilderTest extends RequestUrlBuilderTest {

    @Before
    public void setUp() {
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
    public void correctRequestUrlIsBuilt() {
        Request input = urlBuilder.createRequest(query);
        assertThat(input.url().toString(), is("https://api.github.com/search/code?q=input%2Brepo:test/repo"));
    }

}