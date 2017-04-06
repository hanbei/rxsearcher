package de.hanbei.rxsearch.server;

import guru.nidi.ramltester.RamlDefinition;
import guru.nidi.ramltester.RamlLoaders;
import guru.nidi.ramltester.jaxrs.CheckingWebTarget;
import guru.nidi.ramltester.junit.RamlMatchers;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
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
import java.net.ServerSocket;

import static org.junit.Assert.assertThat;

@RunWith(VertxUnitRunner.class)
public class ApiContractTest {

    private static final String SERVER = "http://localhost:";
    private static Vertx vertx;
    private static int port;

    private RamlDefinition ramlDefinition;
    private JerseyClient client;

    @BeforeClass
    public static void startServer(TestContext context) throws InterruptedException, IOException {
        randomPort();

        DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(
                new JsonObject().put("http.port", port)
        );

        vertx = Vertx.vertx();
        vertx.deployVerticle(VertxServer.class.getName(), deploymentOptions, context.asyncAssertSuccess());
    }

    @AfterClass
    public static void stopServer(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Before
    public void loadRamlDefinition() {
        ramlDefinition = RamlLoaders.fromClasspath().load("apidocs/raml/api.raml");
        assertThat(ramlDefinition.validate(), RamlMatchers.validates());
        ramlDefinition.getRaml().setBaseUri(getServerUrl());
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
    public void testSearchOffers() throws IOException {
        CheckingWebTarget checking = ramlDefinition.createWebTarget(client.target(getServerUrl()));

        checking.path("/search/offers").queryParam("q", "test").request().get();
        assertThat(checking.getLastReport(), RamlMatchers.hasNoViolations());
    }

    private String getServerUrl() {
        return SERVER + port;
    }

    private static void randomPort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        port = socket.getLocalPort();
        socket.close();
    }
}
