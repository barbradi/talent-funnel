- added MDC context through MDCFilter, these kind of keys are very helpful when monitoring in datadog
- move to gradle / kotlin I'd say it's more readable
- handle validations thourgh jakarta
- integration tests for all methods in the controller including error scenarios
- slice webmvc tests for all methods in the controller
- skipped service and repository testing, it's easy
- skipped rate limiter, I'd need to investigate this further pretty sure spring supports it, probably something similar to circuit breaker

## Run the application
./gradlew bootRun

## Documentation
once the application is running go to 
http://localhost:8080/swagger-ui/index.html
