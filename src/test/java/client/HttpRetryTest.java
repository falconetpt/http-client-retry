package client;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"classpath:httpRetry.feature"},
        glue = "client",
        plugin = {"pretty", "json:target/reports/json/day1.json"}
)
public class HttpRetryTest {
}
