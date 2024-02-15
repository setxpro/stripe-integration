package br.com.zendteam.gatewaypayment.services;

import br.com.zendteam.gatewaypayment.dto.StripeChargeDto;
import br.com.zendteam.gatewaypayment.dto.StripeSubscriptionDto;
import br.com.zendteam.gatewaypayment.dto.StripeSubscriptionResponse;
import br.com.zendteam.gatewaypayment.dto.StripeTokenDto;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.PaymentMethodCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class StripeService {

    private static final Logger logger = Logger.getLogger(StripeService.class.getName());
    @Value("${api.stripe.key}")
    private String stripeApiKey;

    public StripeService(@Value("${api.stripe.key}") String stripeApiKey) {
        this.stripeApiKey = stripeApiKey;
        Stripe.apiKey = stripeApiKey;
    }

    public StripeTokenDto createCardToken(StripeTokenDto model) {

        try {

            Stripe.apiKey = "pk_test_**";

            Map<String, Object> card = new HashMap<>();
            card.put("number", model.getCardNumber());
            card.put("exp_month", Integer.parseInt(model.getExpMonth()));
            card.put("exp_year", Integer.parseInt(model.getExpYear()));
            card.put("cvc", model.getCvc());
            Map<String, Object> params = new HashMap<>();
            params.put("card", card);

            Token token = Token.create(params);

            if (token != null && token.getId() != null) {
                model.setSuccess(true);
                model.setToken(token.getId());
            }
            return model;

        } catch (StripeException e) {
            logger.severe("Stripe Service (createCardToken)" + e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public StripeChargeDto charge(StripeChargeDto chargeRequest) {
        try {

            Stripe.apiKey = "sk_test_**";

            chargeRequest.setSuccess(false);
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", (int) (chargeRequest.getAmount() * 100));
            chargeParams.put("currency", "USD");
            chargeParams.put("description", "Payment for id " + chargeRequest.getAdditionalInfo().getOrDefault("ID_TAG", ""));
            chargeParams.put("source", chargeRequest.getStripeToken());
            Map<String, Object> metaData = new HashMap<>();
            metaData.put("id", chargeRequest.getChargeId());
            metaData.putAll(chargeRequest.getAdditionalInfo());
            chargeParams.put("metadata", metaData);
            Charge charge = Charge.create(chargeParams);
            chargeRequest.setMessage(charge.getOutcome().getSellerMessage());

            if (charge.getPaid()) {
                chargeRequest.setChargeId(charge.getId());
                chargeRequest.setSuccess(true);

            }
            return chargeRequest;

        } catch (StripeException e) {
            logger.severe("StripeService (charge)" + e);
            throw new RuntimeException(e.getMessage());
        }

    }

    public StripeSubscriptionResponse createSubscription(StripeSubscriptionDto subscriptionDto) {
        Stripe.apiKey = "pk_test_**";
        PaymentMethod paymentMethod = createPaymentMethod(subscriptionDto);
        Customer customer = createCustomer(paymentMethod, subscriptionDto);
        paymentMethod = attachCustomerToPaymentMethod(customer, paymentMethod);
        Subscription subscription = createSubscription(subscriptionDto, paymentMethod, customer);

        return createResponse(subscriptionDto,paymentMethod,customer,subscription);
    }

    private StripeSubscriptionResponse createResponse(StripeSubscriptionDto subscriptionDto, PaymentMethod paymentMethod, Customer customer, Subscription subscription) {
        return StripeSubscriptionResponse.builder()
                .username(subscriptionDto.getUsername())
                .stripePaymentMethodId(paymentMethod.getId())
                .stripeSubscriptionId(subscription.getId())
                .stripeCustomerId(customer.getId())
                .build();

    }

    private PaymentMethod createPaymentMethod(StripeSubscriptionDto subscriptionDto) {
        try {

            PaymentMethodCreateParams params = PaymentMethodCreateParams.builder()
                    .setType(PaymentMethodCreateParams.Type.CARD)
                    .setCard(
                            PaymentMethodCreateParams.CardDetails.builder()
                                    .setNumber(subscriptionDto.getCardNumber())
                                    .setExpMonth((long) Integer.parseInt(subscriptionDto.getExpMonth()))
                                    .setExpYear((long) Integer.parseInt(subscriptionDto.getExpYear()))
                                    .setCvc(subscriptionDto.getCvc())
                                    .build()
                    ).build();

            return PaymentMethod.create(params);

        } catch (StripeException e) {
            logger.severe("StripeService (createPaymentMethod)" + e);
            throw new RuntimeException(e.getMessage());
        }

    }

    private Customer createCustomer(PaymentMethod paymentMethod, StripeSubscriptionDto subscriptionDto) {
        try {
            Stripe.apiKey = "sk_test_**";
            Map<String, Object> customerMap = new HashMap<>();
            customerMap.put("name", subscriptionDto.getUsername());
            customerMap.put("email", subscriptionDto.getEmail());
            customerMap.put("payment_method", paymentMethod.getId());

            return Customer.create(customerMap);
        } catch (StripeException e) {
            logger.severe("StripeService (createCustomer)" + e);
            throw new RuntimeException(e.getMessage());
        }

    }

    private PaymentMethod attachCustomerToPaymentMethod(Customer customer, PaymentMethod paymentMethod) {
        try {

            paymentMethod = com.stripe.model.PaymentMethod.retrieve(paymentMethod.getId());

            Map<String, Object> params = new HashMap<>();
            params.put("customer", customer.getId());
            paymentMethod = paymentMethod.attach(params);
            return paymentMethod;


        } catch (StripeException e) {
            logger.severe("StripeService (attachCustomerToPaymentMethod)" + e);
            throw new RuntimeException(e.getMessage());
        }

    }

    private Subscription createSubscription(StripeSubscriptionDto subscriptionDto, PaymentMethod paymentMethod, Customer customer) {
        try {
            Stripe.apiKey = "sk_test_**";

            List<Object> items = new ArrayList<>();
            Map<String, Object> item1 = new HashMap<>();
            item1.put(
                    "price",
                    subscriptionDto.getPriceId()
            );
            item1.put("quantity",subscriptionDto.getNumberOfLicense());
            items.add(item1);

            Map<String, Object> params = new HashMap<>();
            params.put("customer", customer.getId());
            params.put("default_payment_method", paymentMethod.getId());
            params.put("items", items);

            return Subscription.create(params);
        } catch (StripeException e) {
            logger.severe("StripeService (createSubscription)" + e);
            throw new RuntimeException(e.getMessage());
        }

    }

    public Subscription cancelSubscription(String subscriptionId) {
        try {
            Subscription retrieve = Subscription.retrieve(subscriptionId);
            return retrieve.cancel();
        }
        catch (StripeException e) {

            logger.severe("SubscriptionService (cancelSubscription) " + e);
        }

        return null;
    }
}
