package de.hanbei.rxsearch.config;

import org.junit.Test;

class ConfigBuilderTest {

    @Test
    public void testBuilder() {
        new ConfigBuilder().searchers {
            zoom(name: "zoom", baseUrl: "http://www.example.com/zoom")
            github(name: "github", repo: "hanbei/mock-httpserver")
            fred(name: "fred", serverUrl: "http://www.example.com/fred")
        };
    }

}