# RazorPay

### spring boot version: 4.1.0
### java version: 25


## Stampede
- A cache stampede happens when a popular cache entry expires, and many requests simultaneously find the cache empty and all hit the database/service


## Cache penetration (kind of attack)
- Cache penetration happens when a request asks for data that doesn't exist, so the request always misses the cache and goes to the database.