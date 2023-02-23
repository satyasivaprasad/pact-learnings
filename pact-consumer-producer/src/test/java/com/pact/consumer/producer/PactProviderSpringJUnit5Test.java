package com.pact.consumer.producer;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import au.com.dius.pact.provider.spring.junit5.MockMvcTestTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Provider("UserServiceJUnit5")
@PactFolder("build/pacts")
public class PactProviderSpringJUnit5Test {

    @TestConfiguration
    static class Config {

        @Bean
        public UserRepository userRepo() {
            return new UserRepository();
        }
    }

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void setup(PactVerificationContext context) {
        context.setTarget(new MockMvcTestTarget(mockMvc));
    }

    // see: https://docs.pact.io/getting_started/provider_states
    @State("A running user service")
    void setupUserService() {
        // no state setup ATM
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }
}
