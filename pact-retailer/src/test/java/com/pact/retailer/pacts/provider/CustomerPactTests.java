package com.pact.retailer.pacts.provider;

import au.com.dius.pact.provider.junit.Consumer;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import au.com.dius.pact.provider.spring.target.SpringBootHttpTarget;
import com.pact.retailer.core.Order;
import com.pact.retailer.core.RetailerService;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRestPactRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Provider("pactRetailer")
@Consumer("pactCustomer")
@PactBroker(host = "localhost",port = "9292")
public class CustomerPactTests {

  @TestTarget
  public Target target = new SpringBootHttpTarget();

  @State("Get order details")
  public void testGetConsumerTwo(){
    RetailerService mock = Mockito.mock(RetailerService.class);

    Order order = new Order("customerId", 1000.0, 2);

    Mockito.when(mock.getOrderDetails("79"))
            .thenReturn(order);
  }
}
