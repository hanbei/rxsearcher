package de.hanbei.rxsearch.server;

import guru.nidi.ramltester.RamlDefinition;
import guru.nidi.ramltester.RamlLoaders;
import guru.nidi.ramltester.httpcomponents.RamlHttpClient;
import guru.nidi.ramltester.junit.RamlMatchers;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.apache.http.client.methods.HttpGet;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertThat;

@RunWith(VertxUnitRunner.class)
public class ApiContractTest {

    private RamlDefinition api;
    private static Vertx vertx;

    @BeforeClass
    public static void startServer(TestContext context) throws InterruptedException {
        vertx = Vertx.vertx();
        vertx.deployVerticle(VertxServer.class.getName(), context.asyncAssertSuccess());
    }

    @AfterClass
    public static void stopServer(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Before
    public void loadRamlDefinition() {
        api = RamlLoaders.fromClasspath().load("apidocs/raml/api.raml").assumingBaseUri("http://localhost:8080");
        assertThat(api.validate(), RamlMatchers.validates());
    }

    @Test
    public void testSearchOffers() throws IOException {
        RamlHttpClient client = api.createHttpClient();
        HttpGet get = new HttpGet("http://localhost:8080/search/offers?q=test");
        get.addHeader("X-Request-ID", "id");
        client.execute(get);

        assertThat(client.getLastReport(), RamlMatchers.checks());
    }


}
