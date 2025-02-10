# ScrapAd Technical Documentation

## Architecture Overview

The ScrapAd Financing System is built using a layered architecture pattern with the following components:

### Layers
1. **Controller Layer** (`controller/`)
   - REST API endpoints
   - Request/Response handling
   - Input validation
   - API documentation (OpenAPI/Swagger)

2. **Service Layer** (`service/`)
   - Business logic implementation
   - Transaction management
   - Financing rules enforcement

3. **Repository Layer** (`repository/`)
   - Data access layer
   - JPA repositories
   - Database operations

4. **Model Layer** (`model/`)
   - Entity definitions
   - JPA mappings
   - Database schema

### Database Schema

```sql
organizations
├── id (TEXT, PK)
├── country (TEXT)
└── created_date (DATETIME)

ads
├── id (TEXT, PK)
├── amount (INTEGER)
├── price (INTEGER)
└── org_id (TEXT, FK -> organizations.id)

fnancing_providers
├── id (INTEGER, PK)
├── slug (TEXT)
├── payment_method (TEXT)
└── financing_percentage (INTEGER)

offers
├── id (TEXT, PK)
├── payment_method (TEXT)
├── financing_privder (INTEGER, FK -> fnancing_providers.id)
├── amount (INTEGER)
├── accepted (INTEGER)
└── price (INTEGER)
```

## Business Rules

### Financing Eligibility
1. **Bank Financing (5% fee)**
   - Organization from Spain or France
   - More than 10,000 published ads
   - Organization age > 1 year

2. **Fintech Financing (7% fee)**
   - Organization from other countries
   - More than 10,000 published ads
   - Organization age > 1 year

### Amount Calculations
- All monetary amounts are multiplied by 100 to avoid decimals
- Example: 150 represents 1.5 units
- Total price = amount * price
- Financing amount = total price - (total price * financing_percentage / 100)

## API Endpoints

### Create Offer
- **Endpoint**: POST `/api/v1/offers`
- **Purpose**: Create a new offer for an ad
- **Authentication**: Required
- **Request Validation**:
  - Valid ad ID
  - Positive amount and price
  - Valid payment method

### Get Pending Offers
- **Endpoint**: GET `/api/v1/offers/organization/{orgId}`
- **Purpose**: Retrieve pending offers for an organization
- **Authentication**: Required
- **Filters**: Only non-accepted offers

### Request Financing
- **Endpoint**: POST `/api/v1/offers/{offerId}/financing`
- **Purpose**: Request financing for an offer
- **Authentication**: Required
- **Validation**:
  - Organization eligibility
  - Valid financing provider
  - Correct amount calculations

### Accept Offer
- **Endpoint**: POST `/api/v1/offers/{offerId}/accept`
- **Purpose**: Accept an offer with optional financing
- **Authentication**: Required
- **State Changes**: 
  - Marks offer as accepted
  - Associates financing if requested

## Development Setup

### Prerequisites
- Java 17
- Maven
- Docker and Docker Compose
- SQLite

### Local Development
1. Clone repository
2. Run `mvn clean install`
3. Start application: `mvn spring-boot:run`
4. Access Swagger UI: `http://localhost:8080/swagger-ui.html`

### Testing
- Unit tests: `mvn test`
- Integration tests: `mvn verify`
- Test coverage: `mvn jacoco:report`

### Docker Deployment
1. Build image: `docker-compose build`
2. Start services: `docker-compose up`
3. Stop services: `docker-compose down`

## Monitoring and Maintenance

### Health Checks
- Endpoint: `/actuator/health`
- Checks:
  - Database connectivity
  - Application status
  - Disk space

### Logging
- Location: `logs/`
- Levels:
  - INFO: Normal operations
  - WARN: Potential issues
  - ERROR: Critical problems
  - DEBUG: Development details

### Error Handling
- Global exception handler
- Standardized error responses
- Transaction rollback on failures

## Security Considerations

### Data Protection
- Input validation
- SQL injection prevention
- XSS protection
- CORS configuration

### Best Practices
- Use HTTPS in production
- Implement rate limiting
- Regular security updates
- Audit logging

## Future Improvements

1. **Scalability**
   - Implement caching
   - Add load balancing
   - Message queues for async operations

2. **Monitoring**
   - Add Prometheus metrics
   - Grafana dashboards
   - Performance monitoring

3. **Features**
   - Additional financing providers
   - More payment methods
   - Batch operations support 