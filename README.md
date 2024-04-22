## Backend Project - Guilherme Campos de Melo

### Description
This system was developed during the full-stack bootcamp offered by SysMap Solutions. It offers a comprehensive solution for managing album sales, complete with a loyalty points system.

**app-user-api:** Responsible for managing users and wallets, including registration, authentication, and customer information updates.

**app-integration-api:** Handles the integration with the Spotify API (provider of albums), as well as RabbitMQ for processing messages related to sales and wallet updates.

### How to Run
1. Clone the repository

```
git clone https://github.com/bc-fullstack-04/guilherme-campos-backend.git
```
2. Build up with docker
```
docker-compose -f docker-compose.yml build
docker-compose -f docker-compose.yml up
```
> **Note:** Make sure to run the above commands in the **project root directory**.

## APIs Docs
```
/api/swagger-ui/index.html
```