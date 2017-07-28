package rxsearch.model;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class HitTest extends EqualsHashcodeTest<Hit> {

    private Hit sourceHit = Hit.builder()
            .url("url").title("title").searcher("searcher")
            .image("image").description("desc")
            .build();


    @Override
    protected Hit createEqual() {
        return sourceHit;
    }

    @Override
    protected Hit createOtherEqual() {
        return sourceHit;
    }

    @Override
    protected Hit createOther() {
        return Hit.builder()
                .url("url2").title("title2").searcher("searcher")
                .image("image").description("desc").build();
    }

    @Test
    public void testMinimalCreation() {
        Hit hit = Hit.builder().url("url").title("title").searcher("searcher").build();

        assertThat(hit.getUrl(), is("url"));
        assertThat(hit.getTitle(), is("title"));
        assertThat(hit.getSearcher(), is("searcher"));

        assertThat(hit.getDescription(), is(nullValue()));
        assertThat(hit.getImage(), is(nullValue()));
    }

    @Test
    public void testCreationFromOtherOffer() {
        Hit hit = Hit.from(sourceHit).build();

        assertThat(hit.getUrl(), is(sourceHit.getUrl()));
        assertThat(hit.getTitle(), is(sourceHit.getTitle()));
        assertThat(hit.getSearcher(), is(sourceHit.getSearcher()));
        assertThat(hit.getImage(), is(sourceHit.getImage()));

        assertThat(hit.getDescription(), is(sourceHit.getDescription()));
    }


}