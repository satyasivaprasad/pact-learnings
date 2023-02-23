package com.pact.consumer.producer;

import au.com.dius.pact.core.model.*;
import au.com.dius.pact.provider.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PactProviderTest {

    ProviderInfo providerInfo;
    ConsumerInfo consumerInfo;
    static Pact consumerPact;

    @LocalServerPort
    int port;

    @BeforeEach
    void setup() {
        providerInfo = new ProviderInfo("UserService");
        providerInfo.setProtocol("http");
        providerInfo.setHost("localhost");
        providerInfo.setPort(port);
        providerInfo.setPath("/");

        consumerInfo = new ConsumerInfo("UserClient");
        consumerInfo.setName("consumer_client");
        consumerInfo.setPactSource(new FileSource(new File("build/pacts/UserConsumer-UserService.json")));
        //noinspection ConstantConditions
        consumerPact = DefaultPactReader.INSTANCE.loadPact(consumerInfo.getPactSource());
    }

    @Test
    void runConsumerPacts() {
        // grab the first interaction from the pact with consumer
        Interaction interaction = consumerPact.getInteractions().get(0);

        // setup the verifier
        ProviderVerifier verifier = setupVerifier(interaction, providerInfo, consumerInfo);

        // setup any provider state

        // setup the client and interaction to fire against the provider
        ProviderClient client = new ProviderClient(providerInfo, new HttpClientFactory());
        Map<String, Object> failures = new HashMap<>();
        //noinspection ConstantConditions

        VerificationResult result = verifier.verifyResponseFromProvider(
                providerInfo,
                interaction.asSynchronousRequestResponse(),
                interaction.getDescription(),
                failures,
                client);

        if (!(result instanceof VerificationResult.Ok)) {
            verifier.displayFailures(List.of((VerificationResult.Failed) result));
        }
        assertThat(result).isInstanceOf(VerificationResult.Ok.class);
    }

    private ProviderVerifier setupVerifier(Interaction interaction, ProviderInfo provider, ConsumerInfo consumer) {
        ProviderVerifier verifier = new ProviderVerifier();

        verifier.initialiseReporters(provider);

        if (!interaction.getProviderStates().isEmpty()) {
            for (ProviderState providerState : interaction.getProviderStates()) {
                //noinspection ConstantConditions
                verifier.reportStateForInteraction(providerState.getName(), provider, consumer, true);
            }
        }

        verifier.reportInteractionDescription(interaction);

        return verifier;
    }
}
