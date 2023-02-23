package com.pact.consumer.producer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
public class PactConsumerJUnit5Test {

    @Pact(provider = "UserServiceJUnit5", consumer = "UserConsumer")
    public V4Pact getAllUsers(PactBuilder builder) {

        //noinspection ConstantConditions
        return builder
                .usingLegacyDsl()
                .given("A running user service")
                .uponReceiving("A request for a user list")
                .path("/users")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonArray()
                        .object()
                        .integerMatching("id", "[0-9]*", 1)
                        .stringMatcher("name", "[a-zA-Z ]*", "Jane Doe")
                        .closeObject()
                        .object()
                        .integerMatching("id", "[0-9]*", 2)
                        .stringMatcher("name", "[a-zA-Z ]*", "John Doe")
                        .closeObject()
                )
                .toPact()
                .asV4Pact()
                .get();
    }

    @Pact(provider = "UserServiceJUnit5", consumer = "UserConsumer")
    public V4Pact getUserById(PactBuilder builder) {

        return builder
                .usingLegacyDsl()
                .given("A running user service")
                .uponReceiving("A request for a user")
                .path("/users/1")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                        .integerMatching("id", "[0-9]*", 1)
                        .stringMatcher("name", "[a-zA-Z ]*", "Jane Doe")
                )
                .toPact()
                .asV4Pact()
                .get();
    }

    @Test
    @PactTestFor(providerName = "UserServiceJUnit5", pactMethod = "getUserById")
    void getUserById(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        User user = restTemplate.getForObject("/users/1", User.class);

        assertThat(user).isNotNull();
        assertThat(user.id()).isNotNegative();
        assertThat(user.name()).isNotEmpty();
    }

    @Test
    @PactTestFor(providerName = "UserServiceJUnit5", pactMethod = "getAllUsers")
    void getAllUsers(MockServer mockServer) {
        RestTemplate restTemplate = new RestTemplateBuilder().rootUri(mockServer.getUrl()).build();
        List<User> users = Arrays.asList(restTemplate.getForObject("/users", User[].class));

        assertThat(users)
                .isNotNull()
                .hasSize(2);

        users.forEach(user -> {
            assertThat(user.id()).isNotNegative();
            assertThat(user.name()).isNotEmpty();
        });
    }

}
