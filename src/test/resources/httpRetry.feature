Feature: Http Retries
    As a developer
    I want to retry when a http call has problems
    So that I can have a more resilient call to the client

    Scenario Outline: Retries after failure will result in success
        Given I have a http client with <numberRetries> number of retries
        When i fail <numberFails> with <failHttpCode> and <failureBody>
            And have success with <successBody>
        Then the result should be <result>
            And endpoint should be called <numberCalls> times

        Examples:
            | numberRetries | numberFails | failHttpCode  | failureBody   | successBody   | numberCalls | result    |
            | 3             | 2           | 500           | "failure"     | "success"     | 3           | "success" |
            | 0             | 0           | 500           | "failure"     | "success"     | 1           | "success" |

    Scenario Outline: Retries will lead to failure
        Given I have a http client with <numberRetries> number of retries
        When i fail <numberFails> with <failHttpCode> and <failureBody>
            And have failure with <body>
        Then the result should be <result>
            And endpoint should be called <numberCalls> times

        Examples:
            | numberRetries | numberFails | failHttpCode  | failureBody   | body       | numberCalls | result    |
            | 3             | 3           | 500           | "failure"     | "failure"  | 4           | "failure" |
            | 0             | 0           | 500           | "failure"     | "failure"  | 1           | "failure" |

