# FL3XX Webhook Client

A Spring Boot webhook client designed for FL3XX partners to receive and process webhook events with HMAC-SHA256 signature verification and asynchronous processing.

## Features

### 🔐 Hash Verification
- **HMAC-SHA256 Signature Verification**: Validates webhook signatures using HMAC-SHA256
- **Default Secret**: Uses a configurable default secret for hash computation
- **Security**: Rejects webhooks with invalid or missing signatures

### ⚡ Async Processing
- **Immediate Response**: Returns success immediately upon webhook receipt
- **Background Processing**: Processes webhook events asynchronously
- **Thread Pool**: Configurable thread pool for concurrent processing

### 📝 Event Logging
- **File Storage**: Stores webhook events to a configurable log file
- **Timestamped**: Each event includes ISO timestamp
- **Append Mode**: Events are appended to the log file

## Configuration

### Application Properties

```properties
# Webhook configuration
webhook.events.file=events.log
webhook.secret=your-webhook-secret-key

# Server configuration
server.port=8082
spring.application.name=fl3xx-webhook-client
```

### Environment Variables

You can override the default secret using environment variables:
```bash
export WEBHOOK_SECRET=your-custom-secret-key
```

## API Endpoints

### POST /events
Receives webhook events with signature verification.

**Headers:**
- `X-Webhook-Signature`: HMAC-SHA256 signature of the request body

**Request Body:** JSON payload

**Response:**
- `200 OK`: Event received and queued for processing
- `401 Unauthorized`: Missing or invalid signature
- `500 Internal Server Error`: Processing error

## Usage

### Prerequisites
- Java 21 or higher
- Maven 3.6 or higher

### Starting the Application
```bash
# Clone the repository
git clone <repository-url>
cd fl3xx-webhook-client

# Build and run
mvn clean install
mvn spring-boot:run
```

The application will start on port 8082 by default.

### Testing Webhook Reception

To test the webhook endpoint, you'll need to generate a valid HMAC-SHA256 signature:

```bash
# Example payload
PAYLOAD='{"eventId":"12345","resourceId":123,"event":"FLIGHT_CREATE","resourceType":"FLIGHT","timestamp":1642244400000}'

# Generate signature (replace with your actual secret)
SIGNATURE=$(echo -n "$PAYLOAD" | openssl dgst -sha256 -hmac "your-webhook-secret-key" -hex | sed 's/^.* //')

# Send webhook
curl -X POST http://localhost:8082/events \
  -H "Content-Type: application/json" \
  -H "X-Webhook-Signature: $SIGNATURE" \
  -d "$PAYLOAD"
```

## Architecture

### Components

1. **WebhookController**: Handles HTTP requests and signature verification
2. **HashVerificationService**: Verifies HMAC-SHA256 signatures
3. **WebhookService**: Processes events asynchronously
4. **WebhookConfig**: Provides async executor and ObjectMapper configuration

### Async Processing Flow

1. Webhook received with signature
2. Signature verified against payload
3. Success response returned immediately
4. Event processed asynchronously in background
5. Event logged to file

## Security

- **Signature Verification**: All webhooks must include valid HMAC-SHA256 signature
- **Secret Management**: Webhook secret configurable via properties or environment
- **Error Handling**: Invalid signatures return 401 Unauthorized
- **Logging**: Processing errors logged without exposing sensitive data

## Event Types

The FL3XX webhook system sends events for various flight operations:

- `FLIGHT_CREATE` - New flight created
- `FLIGHT_UPDATE` - Flight details updated
- `FLIGHT_CANCEL` - Flight cancelled
- `FLIGHT_PAX_COUNT_UPDATE` - Passenger count changed
- `FLIGHT_PAX_LIST_UPDATE` - Passenger list modified
- `FLIGHT_MOVEMENT_UPDATE` - Flight movement details changed

### Event Payload Structure

```json
{
  "eventId": "unique-event-identifier",
  "resourceId": 12345,
  "operatorUuid": "operator-uuid",
  "event": "FLIGHT_CREATE",
  "timestamp": 1642244400000,
  "resourceType": "FLIGHT"
}
```

## Deployment

### Building for Production

```bash
# Build JAR file
mvn clean package

# Run the JAR
java -jar target/fl3xx-webhook-client-0.0.1-SNAPSHOT.jar
```

### Docker Deployment

Create a `Dockerfile`:

```dockerfile
FROM openjdk:21-jre-slim
COPY target/fl3xx-webhook-client-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Build and run with Docker
docker build -t fl3xx-webhook-client .
docker run -p 8082:8082 -e WEBHOOK_SECRET=your-secret fl3xx-webhook-client
```

## Dependencies

- Spring Boot 3.5.3
- Jackson for JSON processing
- Apache Commons Codec for hash verification
- Spring Web for HTTP handling
- Spring Async for background processing

## Support

For integration support or questions about FL3XX webhooks, please contact your FL3XX integration team.