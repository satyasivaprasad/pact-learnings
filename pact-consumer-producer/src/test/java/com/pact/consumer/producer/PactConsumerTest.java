package com.pact.consumer.producer;

import au.com.dius.pact.consumer.ConsumerPactBuilder;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.PactTestExecutionContext;
import au.com.dius.pact.consumer.PactVerificationResult;
import au.com.dius.pact.consumer.model.MockProviderConfig;
import au.com.dius.pact.core.model.RequestResponsePact;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PactConsumerTest {

    @Test
    public void getAllUsersPact() {
        String expectedUsers = """
                [
                    {
                        "id": 1, 
                        "name": "Jane Doe"
                    },
                    {
                        "id": 2, 
                        "name": "John Doe"
                    }
                ]
                """;

        RequestResponsePact pact = ConsumerPactBuilder
                .consumer("UserConsumer")
                .hasPactWith("UserService")
                .uponReceiving("A request for a user list")
                .path("/users")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(expectedUsers)
                .toPact();

        MockProviderConfig config = MockProviderConfig.createDefault();
        PactVerificationResult result = runConsumerTest(pact, config, PactConsumerTest::getAllUsers);

        if (result instanceof PactVerificationResult.Error) {
            throw new RuntimeException(((PactVerificationResult.Error) result).getError());
        }

        assertEquals(new PactVerificationResult.Ok(
                Arrays.asList(
                        new User(1, "Jane Doe"),
                        new User(2, "John Doe")
                )), result);
    }

    @Test
    public void getSpecificUserPact() {
        String expectedUser = """
                {
                    "id": 1, 
                    "name": "Jane Doe"
                }
                """;

        RequestResponsePact pact = ConsumerPactBuilder
                .consumer("UserConsumer")
                .hasPactWith("UserService")
                .uponReceiving("A request for a user")
                .path("/users/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(expectedUser)
                .toPact();

        MockProviderConfig config = MockProviderConfig.createDefault();
        PactVerificationResult result = runConsumerTest(pact, config, PactConsumerTest::getUser);

        if (result instanceof PactVerificationResult.Error) {
            throw new RuntimeException(((PactVerificationResult.Error) result).getError());
        }

        assertEquals(new PactVerificationResult.Ok(new User(1, "Jane Doe")), result);
    }

    private static User getUser(MockServer mockServer, PactTestExecutionContext context) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        return restTemplate.getForObject("/users/1", User.class);
    }

    private static List<User> getAllUsers(MockServer mockServer, PactTestExecutionContext context) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        User[] resultingUsers = restTemplate.getForObject("/users", User[].class);
        if (resultingUsers != null) {
            return Arrays.stream(resultingUsers).toList();
        } else {
            return Collections.emptyList();
        }
    }

}
