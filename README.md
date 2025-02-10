# ScrapAd Backend Assignment

Este proyecto implementa un sistema de financiación para el marketplace ScrapAd, permitiendo a los vendedores recibir financiación basada en reglas de negocio específicas.

## Instalación

1. Clonar el repositorio:
```bash
git clone https://github.com/laboralThings1288/scrap-ad-financing.git
cd scrap-ad-financing
```

2. Copiar el archivo de base de datos proporcionado (`scrapad_assigment.db`) a la raíz del proyecto

## Requisitos

- Java 17
- Maven (opcional si usas el wrapper ./mvnw)
- Docker y Docker Compose (opcional)

## Base de Datos
El proyecto utiliza la base de datos SQLite proporcionada (`scrapad_assigment.db`) que debe estar en la raíz del proyecto.

## Ejecución

### Local
```bash
./mvnw spring-boot:run
```

### Docker (Opcional)
```bash
docker-compose up --build
```

## API REST

La documentación de la API está disponible en:
```
http://localhost:8080/swagger-ui.html
```

### Endpoints

#### 1. Crear una oferta
```http
POST /api/v1/offers
Content-Type: application/json

{
  "ad": "uuid",
  "amount": 105,
  "price": 10000,
  "payment_method": "100_in_unload"
}
```

#### 2. Obtener ofertas pendientes
```http
GET /api/v1/offers/organization/{orgId}
```

#### 3. Solicitar financiación
```http
POST /api/v1/offers/{offerId}/financing
Content-Type: application/json

{
  "financingPartner": "financing_by_bank",
  "totalToPerceive": 123123
}
```

#### 4. Aceptar oferta
```http
POST /api/v1/offers/{offerId}/accept
Content-Type: application/json

{
  "financingPartner": "financing_by_bank"  // opcional
}
```

## Reglas de Negocio

### Financiación Bancaria (5% fee)
- Organización de España o Francia
- Más de 10,000 anuncios publicados
- Antigüedad > 1 año

### Financiación Fintech (7% fee)
- Organización de otros países
- Más de 10,000 anuncios publicados
- Antigüedad > 1 año

### Cálculos
- Todos los montos están multiplicados por 100 para evitar decimales
- Ejemplo: 150 representa 1.5 unidades
- Precio total = cantidad * precio
- Monto financiado = precio total - (precio total * porcentaje_financiación / 100)

## Tests End-to-End
Ejecutar:
```bash
./mvnw test
```
Flujo probado: create_offer -> retrieve_pending_offers -> request_financing -> accept_offer_with_financing 