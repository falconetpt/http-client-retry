package client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HttpContext;

import java.util.concurrent.TimeUnit;

public class HttpFactory {
    public HttpClient httpClient(final int maxRetries,
                                 final int configuredInitialWaitPeriod,
                                 final int configuredTimeout)  {

        return HttpClientBuilder.create()
                .setConnectionTimeToLive(configuredTimeout, TimeUnit.SECONDS)
                .setRetryHandler((exception, executionCount, context) -> executionCount <= maxRetries)
                .setServiceUnavailableRetryStrategy(new ServiceUnavailableRetryStrategy() {
                    @Override
                    public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
                        return executionCount <= maxRetries &&
                                response.getStatusLine().getStatusCode() >= 500;
                    }

                    @Override
                    public long getRetryInterval() {
                        return configuredInitialWaitPeriod;
                    }
                })
                .build();
    }
}
