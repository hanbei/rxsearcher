package de.hanbei.rxsearch.model;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

public class OfferTest extends EqualsHashcodeTest<Offer> {

    private Offer sourceOffer = Offer.builder()
            .url("url").title("title").price(0.0, "USD").searcher("searcher")
            .image("image").category("category").description("desc").manufacturer("manufacturer")
            .originalUrl("originalUrl").shippingCosts(new Money(1.0, "EUR"))
            .merchant(new Merchant("ebay", "mimage", "1234", "mcategory"))
            .brand("brand").build();


    @Override
    protected Offer createEqual() {
        return sourceOffer;
    }

    @Override
    protected Offer createOtherEqual() {
        return sourceOffer;
    }

    @Override
    protected Offer createOther() {
        return Offer.builder()
                .url("url2").title("title2").price(0.0, "EUR").searcher("searcher")
                .image("image").category("category").description("desc").manufacturer("manufacturer")
                .originalUrl("originalUrl").shippingCosts(new Money(1.0, "EUR"))
                .merchant(new Merchant("ebay", "mimage", "1234", "mcategory"))
                .brand("brand").build();
    }

    @Test
    public void testMinimalCreation() {
        Offer offer = Offer.builder().url("url").title("title").price(0.0, "USD").searcher("searcher").build();

        assertThat(offer.getUrl(), is("url"));
        assertThat(offer.getTitle(), is("title"));
        assertThat(offer.getPrice(), is(new Money(0.0, "USD")));
        assertThat(offer.getSearcher(), is("searcher"));

        assertThat(offer.getCategory(), is(""));
        assertThat(offer.getDescription(), is(""));
        assertThat(offer.getImage(), is(""));
        assertThat(offer.getManufacturer(), is(nullValue()));
        assertThat(offer.getMerchant(), is(nullValue()));
        assertThat(offer.getOriginalUrl(), is(nullValue()));
        assertThat(offer.getShippingCosts(), is(nullValue()));
    }

    @Test
    public void testCreationFromOtherOffer() {
        Offer offer = Offer.from(sourceOffer).build();

        assertThat(offer.getUrl(), is(sourceOffer.getUrl()));
        assertThat(offer.getTitle(), is(sourceOffer.getTitle()));
        assertThat(offer.getPrice(), is(sourceOffer.getPrice()));
        assertThat(offer.getSearcher(), is(sourceOffer.getSearcher()));
        assertThat(offer.getCategory(), is(sourceOffer.getCategory()));
        assertThat(offer.getImage(), is(sourceOffer.getImage()));

        assertThat(offer.getDescription(), is(sourceOffer.getDescription()));
        assertThat(offer.getManufacturer(), is(sourceOffer.getManufacturer()));
        assertThat(offer.getMerchant(), is(sourceOffer.getMerchant()));
        assertThat(offer.getOriginalUrl(), is(sourceOffer.getOriginalUrl()));
        assertThat(offer.getShippingCosts(), is(sourceOffer.getShippingCosts()));
        assertThat(offer.getBrand(), is(sourceOffer.getBrand()));
    }


}