package de.hanbei.rxsearch.server;

import guru.nidi.ramltester.RamlDefinition;
import guru.nidi.ramltester.RamlLoaders;
import guru.nidi.ramltester.jaxrs.CheckingWebTarget;
import guru.nidi.ramltester.junit.RamlMatchers;
import io.vertx.core.Vertx;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static org.junit.Assert.assertThat;

@RunWith(VertxUnitRunner.class)
public class ApiContractTest {

    private static final String SERVER = "http://localhost:8080";
    private static Vertx vertx;

    private RamlDefinition ramlDefinition;
    private JerseyClient client;

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
        ramlDefinition = RamlLoaders.fromClasspath().load("apidocs/raml/api.raml");
        assertThat(ramlDefinition.validate(), RamlMatchers.validates());
        ramlDefinition.getRaml().setBaseUri(SERVER);
    }

    @Before
    public void setupClient() {
        client = new JerseyClientBuilder().build();
    }

    @After
    public void closeClient() {
        client.close();
    }

    @Test
    public void testSearchOffersJersey() throws IOException {
        CheckingWebTarget checking = ramlDefinition.createWebTarget(client.target(SERVER));

        checking.path("/search/offers").queryParam("q", "test").request().get();
        assertThat(checking.getLastReport(), RamlMatchers.hasNoViolations());
    }
}
