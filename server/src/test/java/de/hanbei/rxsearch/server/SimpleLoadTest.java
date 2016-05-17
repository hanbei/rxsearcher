package de.hanbei.rxsearch.server;


import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by hanbei on 4/17/16.
 */
public class SimpleLoadTest {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        MetricRegistry registry = new MetricRegistry();
        List<String> keywords = Lists.newArrayList("class", "duckduckgo", "response", "jquery");
        Random random = new Random(0);
        AsyncHttpClient client = new AsyncHttpClient();

        for (int i = 0; i < 1000; i++) {
            executorService.submit(new WorkerThread(registry, keywords, random, client));
        }

        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry).build();
        reporter.start(5, TimeUnit.SECONDS);
    }

    static class WorkerThread implements Runnable {

        private final MetricRegistry registry;
        private final List<String> keywords;
        private final Random random;
        private final AsyncHttpClient client;

        public WorkerThread(MetricRegistry registry, List<String> keywords, Random random, AsyncHttpClient client) {
            this.registry = registry;
            this.keywords = keywords;
            this.random = random;
            this.client = client;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(random.nextInt(200));
            } catch (InterruptedException ignored) {
                return;
            }
            String keyword = choice(keywords, random);
            client.prepareGet("http://rxsearch.herokuapp.com/search/" + keyword)
                    .execute(new AsyncCompletionHandler<Void>() {
                                 @Override
                                 public Void onCompleted(Response response) throws Exception {
                                     if (response.getStatusCode() == 200) {
                                         String header = response.getHeader("x-response-time");
                                         String substring = header.substring(0, header.length() - 2);
                                         long duration = Long.parseLong(substring);
                                         //System.out.println(header + " -> " + duration);
                                         registry.timer("requests").update(duration, TimeUnit.MILLISECONDS);
                                         registry.counter("requests-ok").inc();
                                     } else {
                                         System.err.println("Failed request: " + keyword);
                                         registry.counter("requests-" + response.getStatusCode()).inc();
                                     }
                                     return null;
                                 }

                                 @Override
                                 public void onThrowable(Throwable t) {
                                     registry.counter("requests-failed").inc();
                                 }
                             }

                    );
        }

        private String choice(List<String> keywords, Random random) {
            int random_index = random.nextInt(keywords.size());
            return keywords.get(random_index);
        }
    }
}
