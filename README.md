# Tangerine API

## Running
To run this application execute `docker compose -f docker-compose.yml up`

## Checkstyle
This project is fully compliant with Google Checks specifications.
Read more [here](https://google.github.io/styleguide/javaguide.html).

To configure this code convention in your IDE,
use the Checkstyle plugin and the [config file](config/checkstyle.xml).

To run checkstyle, execute `mvn clean verify -P checkstyle`

## Swagger(OpenAPI 3)
After running application, the Swagger UI page is available at http://localhost:8080/swagger-ui.html