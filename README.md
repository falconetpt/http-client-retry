## Overview

### US Link
(Link to the US of jira process)

### Type of change
- [ ] Bug fix (non-breaking change which fixes an issue)
- [X] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Refactoring
- [ ] This change requires a documentation update

### What is the solution?
1. Use `setRetryHandler` to define the maximum number of retries
1. Use `setServiceUnavailableRetryStrategy` to configure the waiting time
between request to avoid overflowing the client with calls, this can be configured to use an incrementing
time between calls

### What are the main changes this MR?
1. Changes the Apache Http client in order to allow retries and time between retries.
1. Adds Cucumber tests for multiple scenarios

## Reviewing

### Which order of files makes the most sense for the reviewer?
1. `src/test/resources/httpRetry.feature`
1. `HttpFactory.java`

## Other Notes
1. Take a look into the `src/test/resources/httpRetry.feature` to see the scenarios specified
they include: 
    * retry until success
    * retry leads into failure
    * success at first try
1. Cucumber tests steps implemented using lambdas
1. Timeouts not configured for each step
1. In order to make timeout incremental with each retry we should use
`setServiceUnavailableRetryStrategy::retryRequest` to increment a new variable on
the `setServiceUnavailableRetryStrategy` and return it on `setServiceUnavailableRetryStrategy::getRetryInterval`.

