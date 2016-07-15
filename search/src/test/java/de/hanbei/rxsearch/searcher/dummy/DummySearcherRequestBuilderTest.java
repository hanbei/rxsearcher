package de.hanbei.rxsearch.searcher.dummy;

import com.ning.http.client.Request;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by hanbei on 5/16/16.
 */
public class DummySearcherRequestBuilderTest {

    private DummySearcherRequestBuilder requestBuilder;

    @Before
    public void setUp() throws Exception {
        requestBuilder = new DummySearcherRequestBuilder("http://www.example.de");
    }

    @Test
    public void correctRequestUrlIsBuilt() throws Exception {
        Request input = requestBuilder.createRequest("input");
        assertThat(input.getUrl(), is("http://www.example.de/search?q=input"));
    }

    @Test
    public void trailingSlashIsRemoved() throws Exception {
        requestBuilder = new DummySearcherRequestBuilder("http://www.example2.de/");
        Request input = requestBuilder.createRequest("input2");
        assertThat(input.getUrl(), is("http://www.example2.de/search?q=input2"));
    }

}