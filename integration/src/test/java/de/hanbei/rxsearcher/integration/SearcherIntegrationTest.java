package de.hanbei.rxsearcher.integration;

import guru.nidi.ramltester.RamlDefinition;
import guru.nidi.ramltester.RamlLoaders;
import guru.nidi.ramltester.httpcomponents.RamlHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static guru.nidi.ramltester.junit.RamlMatchers.checks;
import static guru.nidi.ramltester.junit.RamlMatchers.validates;
import static org.junit.Assert.assertThat;

public class SearcherIntegrationTest {

    private RamlDefinition api;

    @Before
    public void createTarget() {
        api = RamlLoaders.fromClasspath().load("docs/api.raml").assumingBaseUri("http://localhost:8080");
        assertThat(api.validate(), validates());
    }

    @Test
    public void testSearchOffers() throws IOException {
        RamlHttpClient client = api.createHttpClient();
        HttpGet get = new HttpGet("http://localhost:8080/search/offers?q=test");
        get.addHeader("X-Request-ID", "id");
        HttpResponse response = client.execute(get);

        assertThat(client.getLastReport(), checks());
    }

}
