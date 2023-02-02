# coupon-system

A backend application that enables companies to issue and manage coupons that can be purchased by customers.
Built using Spring Boot, uses OAUTH2 Resource Server with JWT for authentication and authorization. Database is managed by MySQL.
Utilises caching with Caffeine as the cache implemenatation - https://github.com/ben-manes/caffeine.

## How to run

You need to have Java 17 or higher & MySQL installed. The database schema is automatically created (if it doesn't already exist).
In the application.properties file you need to set your MySQL username and password.
