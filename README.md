# FluxLimit 🛡️
A simple and lightweight API rate limiter built using Spring Boot, Redis, and Lua.

## How It Works
* **Token Bucket Logic:** Limits incoming requests based on a configured bucket size and refill rate.
* **Lazy Math Refill:** Calculates token refills directly inside a Redis Lua script using the time gap since the last request (`now - last_updated`). No background thread is needed.
* **Single-Step Update:** Because Redis runs Lua scripts as a single atomic unit, multiple incoming requests cannot mess up the token count.
* **Clean Annotation:** Placed on top of controller methods to block extra traffic with a standard `429 Too Many Requests` status.

## Usage
```java
@PostMapping("/api/orders")
@RateLimit(capacity = 5, refillRate = 1)
public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(request.getAmount()));
}
