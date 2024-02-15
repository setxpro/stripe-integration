package br.com.zendteam.gatewaypayment.controllers;

import br.com.zendteam.gatewaypayment.dto.*;
import br.com.zendteam.gatewaypayment.services.StripeService;
import com.stripe.model.Subscription;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.nonNull;

@RestController
    @RequestMapping("/public/stripe")
@AllArgsConstructor
public class StripeController {
    private final StripeService stripeService;


    @PostMapping("/card/token")
    @ResponseBody
    public StripeTokenDto createCardToken(@RequestBody StripeTokenDto model) {


        return stripeService.createCardToken(model);
    }

    @PostMapping("/charge")
    @ResponseBody
    public StripeChargeDto charge(@RequestBody StripeChargeDto model) {
        return stripeService.charge(model);
    }

    @PostMapping("/customer/subscription")
    @ResponseBody
    public StripeSubscriptionResponse subscription(@RequestBody StripeSubscriptionDto subscriptionDto) {
        return stripeService.createSubscription(subscriptionDto);
    }

    @DeleteMapping("/subscription/{id}")
    @ResponseBody
    public SubscriptionCancelRecord cancelSubscription(@PathVariable(value = "id") String id) {
        Subscription subscription = stripeService.cancelSubscription(id);

        if (nonNull(subscription)) {
            return new SubscriptionCancelRecord(subscription.getStatus());
        }

        return null;
    }
}
