# ScrapAd Architecture Documentation

## 1. System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Applications                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │   Web App    │  │ Mobile App   │  │   3rd Party  │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                           API Gateway                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │   Routing    │  │    Auth      │  │  Rate Limit  │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                     ScrapAd Financing Service                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │  Controller │    │   Service   │    │ Repository  │         │
│  │    Layer    │◄──►│    Layer    │◄──►│   Layer    │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│         ▲                  ▲                  ▲                  │
│         │                  │                  │                  │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │    DTOs     │    │   Models    │    │   Cache     │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│                                                                 │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                         SQLite Database                          │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐         │
│  │Organizations│    │    Ads      │    │   Offers    │         │
│  └─────────────┘    └─────────────┘    └─────────────┘         │
│         ▲                  ▲                  ▲                  │
│         └──────────────────┴──────────────────┘                 │
│  ┌─────────────────────────────┐                               │
│  │    Financing Providers      │                               │
│  └─────────────────────────────┘                               │
└─────────────────────────────────────────────────────────────────┘
```

## 2. Detailed Component Flow

```
┌──────────────┐  1.Request   ┌──────────────┐
│    Client    ├─────────────►│  Controller  │
└──────────────┘             └──────┬───────┘
                                    │
                                    │ 2.Validate
                                    ▼
┌──────────────┐  3.Process   ┌──────────────┐
│  Repository  │◄────────────►│   Service    │
└──────┬───────┘             └──────┬───────┘
       │                            │
       │ 4.Cache                    │ 5.Transform
       ▼                            ▼
┌──────────────┐  6.Response  ┌──────────────┐
│   Database   │             │     DTOs     │
└──────────────┘             └──────────────┘
```

## 3. Domain Model

```
┌───────────────────┐     ┌───────────────────┐
│    Organization   │     │        Ad         │
├───────────────────┤     ├───────────────────┤
│ - id             │     │ - id              │
│ - country        │ 1:n │ - amount          │
│ - createdDate    ├────►│ - price           │
└───────────────────┘     │ - organization    │
                          └────────┬──────────┘
                                  │
                                  │ 1:n
                                  ▼
┌───────────────────┐     ┌───────────────────┐
│ FinancingProvider │     │      Offer        │
├───────────────────┤     ├───────────────────┤
│ - id             │     │ - id              │
│ - slug           │ 1:n │ - paymentMethod   │
│ - paymentMethod  ├────►│ - amount          │
│ - percentage     │     │ - price           │
└───────────────────┘     │ - accepted        │
                          └───────────────────┘
```

## 4. Security Architecture

```
┌────────────────────────────────────────────┐
│               Client Request               │
└───────────────────────┬────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────┐
│              API Gateway Layer             │
├────────────────────────────────────────────┤
│ ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│ │  HTTPS   │  │  CORS    │  │  Auth    │  │
│ └──────────┘  └──────────┘  └──────────┘  │
└───────────────────────┬────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────┐
│            Application Layer               │
├────────────────────────────────────────────┤
│ ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│ │ Input    │  │ Business │  │ Output   │  │
│ │ Validate │  │ Logic    │  │ Sanitize │  │
│ └──────────┘  └──────────┘  └──────────┘  │
└───────────────────────┬────────────────────┘
                        │
                        ▼
┌────────────────────────────────────────────┐
│               Data Layer                   │
├────────────────────────────────────────────┤
│ ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│ │ SQL      │  │ Data     │  │ Access   │  │
│ │ Escape   │  │ Encrypt  │  │ Control  │  │
│ └──────────┘  └──────────┘  └──────────┘  │
└────────────────────────────────────────────┘
```

## 5. Key Components Description

### API Gateway
- Request routing
- Authentication/Authorization
- Rate limiting
- Request/Response transformation
- CORS handling

### Service Layer
- Business logic implementation
- Transaction management
- Data validation
- Event handling
- Error management

### Data Access Layer
- Entity management
- Cache handling
- Query optimization
- Connection pooling
- Transaction coordination

### Security Components
- Input validation
- SQL injection prevention
- XSS protection
- CORS configuration
- Authentication
- Authorization
- Data encryption

## 6. Data Flow Sequence

1. **Client Request**
   - Authentication
   - Input validation
   - Rate limiting

2. **Business Processing**
   - Service orchestration
   - Rule application
   - Transaction management

3. **Data Operations**
   - Cache check/update
   - Database operations
   - Data transformation

4. **Response Handling**
   - Output formatting
   - Error handling
   - Response compression

## 7. Scalability Considerations

### Horizontal Scaling
```
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│ Instance 1  │  │ Instance 2  │  │ Instance N  │
└──────┬──────┘  └──────┬──────┘  └──────┬──────┘
       │               │                │
       └───────────────┴────────────────┘
                      │
              ┌───────┴───────┐
              │  Load         │
              │  Balancer     │
              └───────────────┘
```

### Cache Strategy
```
┌─────────────┐     ┌─────────────┐
│  Service    │     │   Cache     │
│  Instance   ├────►│   Layer     │
└─────────────┘     └──────┬──────┘
                          │
                    ┌─────┴──────┐
                    │  Database  │
                    └────────────┘
``` 