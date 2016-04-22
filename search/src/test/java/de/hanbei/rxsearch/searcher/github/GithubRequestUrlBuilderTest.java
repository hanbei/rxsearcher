package de.hanbei.rxsearch.searcher.github;

import de.hanbei.rxsearch.searcher.RequestUrlBuilderTest;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class GithubRequestUrlBuilderTest extends RequestUrlBuilderTest {

    @Before
    public void setUp() throws Exception {
        urlBuilder = new GithubRequestUrlBuilder("test/repo");
    }

    @Test
    public void emptyKeyViolatesNotEmptyPrecondition() {
        expectedException.expect(IllegalArgumentException.class);
        new GithubRequestUrlBuilder("");
    }

    @Test
    public void nullKeyViolatesNotEmptyPrecondition() {
        expectedException.expect(IllegalArgumentException.class);
        new GithubRequestUrlBuilder(null);
    }


    @Test
    public void correctRequestUrlIsBuilt() throws Exception {
        String input = urlBuilder.createRequestUrl("input");
        assertThat(input, is("https://api.github.com/search/code?q=input+repo:test/repo"));
    }

}