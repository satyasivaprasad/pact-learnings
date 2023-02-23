package com.pact.retailer.pacts.provider;

import au.com.dius.pact.provider.junit.Consumer;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import au.com.dius.pact.provider.spring.target.SpringBootHttpTarget;
import com.pact.retailer.PactRetailerApplication;
import com.pact.retailer.core.Item;
import com.pact.retailer.core.RetailerService;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRestPactRunner.class)
@SpringBootTest(classes = PactRetailerApplication.class, webEnvironment = RANDOM_PORT, properties = "server.port=80")
@Provider("pactRetailer")
@Consumer("pactProduct")
@PactBroker(host = "localhost", port = "9292")
@ActiveProfiles("test")
public class ProductPactTests {

    @TestTarget
    public Target target = new SpringBootHttpTarget();

    @State("Get item details")
    public void testBuyerOneContract() {
        Item item = new Item("Apple", "iPad", 200.0);

        RetailerService mock = Mockito.mock(RetailerService.class);

        Mockito.when(mock.getItemDetails("1009"))
                .thenReturn(item);
    }
}
