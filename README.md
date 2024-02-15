<h1 align="center">Stripe Integration</h1>
-

Stripe Billing | Recurring Payments & Subscription

## CARD TOKEN

<table>
    <tr>
        <th>Method</th>
        <th>Endpoint</th>
    </tr>
    <tr>
        <td>POST</td>
        <td>public/stripe/card/token</td>
    </tr>
</table>

### EXAMPLE

REQUEST:
```json
  {
    "cardNumber": "4242424242424242",
    "expMonth": "05",
    "expYear": "26",
    "cvc": "123",
    "username": "patrick"
}
```

RESPONSE:
```json
{
    "cardNumber": "4242424242424242",
    "expMonth": "05",
    "expYear": "26",
    "cvc": "123",
    "token": "tok_1Ok9JQB0FkclW1WXrdY8TFDp",
    "username": "patrick",
    "success": true
}
```
---

## CHARGE

<table>
    <tr>
        <th>Method</th>
        <th>Endpoint</th>
    </tr>
    <tr>
        <td>POST</td>
        <td>public/stripe/charge</td>
    </tr>
</table>

### EXAMPLE

REQUEST:
```json
  {
  "stripeToken": "tok_1Ok9JQB0FkclW1WXrdY8TFDp",
  "username": "zend",
  "amount": 5000,
  "additionalInfo": {
    "ID_TAG": "1234567890"
  }
}
```

RESPONSE:
```json
{
  "stripeToken": "tok_1Ok9JQB0FkclW1WXrdY8TFDp",
  "username": "zend",
  "amount": 5000.0,
  "success": true,
  "message": "Payment complete.",
  "chargeId": "ch_3Ok9JbB0FkclW1WX14RkZViB",
  "additionalInfo": {
    "ID_TAG": "1234567890"
  }
}
```
---

## SUBSCRIPTION

<table>
    <tr>
        <th>Method</th>
        <th>Endpoint</th>
    </tr>
    <tr>
        <td>POST</td>
        <td>public/stripe/customer/subscription</td>
    </tr>
</table>

### EXAMPLE

REQUEST:
```json
  {
  "cardNumber": "4242424242424242",
  "expMonth": "05",
  "expYear": "26",
  "cvc": "123",
  "email": "patrickpqdt87289@gmail.com",
  "priceId": "price_1Ok5PmB0FkclW1WXrag0Rby3",
  "username": "zend",
  "numberOfLicense": 1
}
```

RESPONSE:
```json
{
  "stripeCustomerId": "cus_PZIcBMcxPGvrEi",
  "stripeSubscriptionId": "sub_1OkA1BB0FkclW1WXvORStzo2",
  "stripePaymentMethodId": "pm_1OkA19B0FkclW1WXEKD21zUm",
  "username": "zend"
}
```
---

## CANCEL SUBSCRIPTION

<table>
    <tr>
        <th>Method</th>
        <th>Endpoint</th>
        <th>PARAM</th>
    </tr>
    <tr>
        <td>POST</td>
        <td>public/stripe/subscription/{sub_id}</td>
        <td>sub_1OkA1BB0FkclW1WXvORStzo2</td>
    </tr>
</table>

### EXAMPLE

REQUEST:
```json
  DELETE
```

RESPONSE:
```json
{
  "status": "canceled"
}
```
---