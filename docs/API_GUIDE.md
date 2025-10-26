# Tennis Scoring System API Guide

This guide provides comprehensive examples for using the Tennis Scoring System REST API.

## Base URL

```
http://localhost:8080/api
```

## Authentication

Currently, the API does not require authentication. All endpoints are publicly accessible.

## Content Type

All requests should use `Content-Type: application/json` for POST and PUT requests.

## Error Handling

The API returns consistent error responses:

```json
{
  "error": "Error Type",
  "message": "Detailed error message",
  "status": 400,
  "path": "/api/matches",
  "timestamp": "2025-10-26T08:44:42.123456"
}
```

## Endpoints

### 1. Create Match

Creates a new tennis match between two players.

**Endpoint:** `POST /matches`

**Request Body:**
```json
{
  "player1Name": "Rafael Nadal",
  "player2Name": "Roger Federer"
}
```

**Success Response (201 Created):**
```json
{
  "matchId": "bca822dc-da60-44d3-93fc-decdd4f39da9",
  "player1": {
    "playerId": "dd02d24b-b0c3-4255-a87c-db38efd72c17",
    "name": "Rafael Nadal"
  },
  "player2": {
    "playerId": "7beaf12e-19c0-4a02-af34-9d0bf7544baf",
    "name": "Roger Federer"
  },
  "status": "IN_PROGRESS",
  "currentScore": "0-0 (0-0)",
  "sets": [],
  "createdAt": "2025-10-26T08:44:54.112544"
}
```

**Error Responses:**

*Duplicate Player Names (400 Bad Request):*
```json
{
  "error": "Duplicate Player",
  "message": "Validation failed for field 'playerName' with value 'Same Player': Player names must be different",
  "status": 400,
  "path": "/api/matches",
  "timestamp": "2025-10-26T08:44:54.388"
}
```

*Validation Error (400 Bad Request):*
```json
{
  "error": "Validation Failed",
  "message": "請求參數驗證失敗",
  "status": 400,
  "path": "/api/matches",
  "timestamp": "2025-10-26T08:44:54.394",
  "details": [
    {
      "field": "player1Name",
      "message": "第一位球員名稱不能為空"
    }
  ]
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/matches \
  -H "Content-Type: application/json" \
  -d '{
    "player1Name": "Rafael Nadal",
    "player2Name": "Roger Federer"
  }'
```

### 2. Score Point

Records a point for a specific player in a match.

**Endpoint:** `POST /matches/{matchId}/score`

**Path Parameters:**
- `matchId` (string): The unique identifier of the match

**Request Body:**
```json
{
  "playerId": "dd02d24b-b0c3-4255-a87c-db38efd72c17"
}
```

**Success Response (200 OK):**
```json
{
  "matchId": "bca822dc-da60-44d3-93fc-decdd4f39da9",
  "player1": {
    "playerId": "dd02d24b-b0c3-4255-a87c-db38efd72c17",
    "name": "Rafael Nadal"
  },
  "player2": {
    "playerId": "7beaf12e-19c0-4a02-af34-9d0bf7544baf",
    "name": "Roger Federer"
  },
  "status": "IN_PROGRESS",
  "currentScore": "0-0 (15-0)",
  "sets": [
    {
      "setNumber": 1,
      "player1Games": 0,
      "player2Games": 0,
      "status": "IN_PROGRESS",
      "currentGame": {
        "gameNumber": 1,
        "player1Score": "15",
        "player2Score": "0",
        "status": "IN_PROGRESS"
      }
    }
  ]
}
```

**Error Responses:**

*Match Not Found (404 Not Found):*
```json
{
  "error": "Match Not Found",
  "message": "Match not found with ID: invalid-match-id",
  "status": 404,
  "path": "/api/matches/invalid-match-id/score",
  "timestamp": "2025-10-26T08:44:54.287"
}
```

*Player Not Found (404 Not Found):*
```json
{
  "error": "Player Not Found",
  "message": "Player not found with ID: invalid-player-id in match: bca822dc-da60-44d3-93fc-decdd4f39da9",
  "status": 404,
  "path": "/api/matches/bca822dc-da60-44d3-93fc-decdd4f39da9/score",
  "timestamp": "2025-10-26T08:44:54.403"
}
```

*Invalid Match State (409 Conflict):*
```json
{
  "error": "Invalid Match State",
  "message": "Cannot score on completed match: bca822dc-da60-44d3-93fc-decdd4f39da9",
  "status": 409,
  "path": "/api/matches/bca822dc-da60-44d3-93fc-decdd4f39da9/score",
  "timestamp": "2025-10-26T08:44:54.494"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/matches/bca822dc-da60-44d3-93fc-decdd4f39da9/score \
  -H "Content-Type: application/json" \
  -d '{
    "playerId": "dd02d24b-b0c3-4255-a87c-db38efd72c17"
  }'
```

### 3. Get Match Details

Retrieves detailed information about a specific match.

**Endpoint:** `GET /matches/{matchId}`

**Path Parameters:**
- `matchId` (string): The unique identifier of the match

**Success Response (200 OK):**
```json
{
  "matchId": "bca822dc-da60-44d3-93fc-decdd4f39da9",
  "player1": {
    "playerId": "dd02d24b-b0c3-4255-a87c-db38efd72c17",
    "name": "Rafael Nadal"
  },
  "player2": {
    "playerId": "7beaf12e-19c0-4a02-af34-9d0bf7544baf",
    "name": "Roger Federer"
  },
  "status": "IN_PROGRESS",
  "currentScore": "1-0 (0-0)",
  "sets": [
    {
      "setNumber": 1,
      "player1Games": 1,
      "player2Games": 0,
      "status": "IN_PROGRESS",
      "currentGame": {
        "gameNumber": 2,
        "player1Score": "0",
        "player2Score": "0",
        "status": "IN_PROGRESS"
      }
    }
  ],
  "createdAt": "2025-10-26T08:44:54.112544"
}
```

**Error Response:**

*Match Not Found (404 Not Found):*
```json
{
  "error": "Match Not Found",
  "message": "Match not found with ID: invalid-match-id",
  "status": 404,
  "path": "/api/matches/invalid-match-id",
  "timestamp": "2025-10-26T08:44:54.287"
}
```

**cURL Example:**
```bash
curl http://localhost:8080/api/matches/bca822dc-da60-44d3-93fc-decdd4f39da9
```

### 4. Get All Matches

Retrieves a list of all matches in the system.

**Endpoint:** `GET /matches`

**Success Response (200 OK):**
```json
[
  {
    "matchId": "bca822dc-da60-44d3-93fc-decdd4f39da9",
    "player1": {
      "playerId": "dd02d24b-b0c3-4255-a87c-db38efd72c17",
      "name": "Rafael Nadal"
    },
    "player2": {
      "playerId": "7beaf12e-19c0-4a02-af34-9d0bf7544baf",
      "name": "Roger Federer"
    },
    "status": "IN_PROGRESS",
    "currentScore": "1-0 (0-0)",
    "createdAt": "2025-10-26T08:44:54.112544"
  },
  {
    "matchId": "another-match-id",
    "player1": {
      "playerId": "player3-id",
      "name": "Novak Djokovic"
    },
    "player2": {
      "playerId": "player4-id",
      "name": "Andy Murray"
    },
    "status": "COMPLETED",
    "currentScore": "6-4, 6-3",
    "createdAt": "2025-10-26T07:30:00.000000"
  }
]
```

**cURL Example:**
```bash
curl http://localhost:8080/api/matches
```

### 5. Cancel Match

Cancels an ongoing match, setting its status to CANCELLED.

**Endpoint:** `PUT /matches/{matchId}/cancel`

**Path Parameters:**
- `matchId` (string): The unique identifier of the match

**Success Response (200 OK):**
```json
{
  "matchId": "bca822dc-da60-44d3-93fc-decdd4f39da9",
  "player1": {
    "playerId": "dd02d24b-b0c3-4255-a87c-db38efd72c17",
    "name": "Rafael Nadal"
  },
  "player2": {
    "playerId": "7beaf12e-19c0-4a02-af34-9d0bf7544baf",
    "name": "Roger Federer"
  },
  "status": "CANCELLED",
  "currentScore": "1-0 (15-0)",
  "sets": [
    {
      "setNumber": 1,
      "player1Games": 1,
      "player2Games": 0,
      "status": "CANCELLED"
    }
  ]
}
```

**Error Response:**

*Match Not Found (404 Not Found):*
```json
{
  "error": "Match Not Found",
  "message": "Match not found with ID: invalid-match-id",
  "status": 404,
  "path": "/api/matches/invalid-match-id/cancel",
  "timestamp": "2025-10-26T08:44:54.287"
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:8080/api/matches/bca822dc-da60-44d3-93fc-decdd4f39da9/cancel
```

### 6. Delete Match

Permanently deletes a match from the system.

**Endpoint:** `DELETE /matches/{matchId}`

**Path Parameters:**
- `matchId` (string): The unique identifier of the match

**Success Response (204 No Content):**
No response body is returned for successful deletion.

**Error Response:**

*Match Not Found (404 Not Found):*
```json
{
  "error": "Match Not Found",
  "message": "Match not found with ID: invalid-match-id",
  "status": 404,
  "path": "/api/matches/invalid-match-id",
  "timestamp": "2025-10-26T08:44:54.287"
}
```

**cURL Example:**
```bash
curl -X DELETE http://localhost:8080/api/matches/bca822dc-da60-44d3-93fc-decdd4f39da9
```

### 7. Get Match Statistics

Retrieves system-wide match statistics.

**Endpoint:** `GET /matches/statistics`

**Success Response (200 OK):**
```json
{
  "totalMatches": 15,
  "activeMatches": 3,
  "completedMatches": 10,
  "cancelledMatches": 2,
  "averageMatchDuration": "45 minutes",
  "mostActivePlayer": "Rafael Nadal"
}
```

**cURL Example:**
```bash
curl http://localhost:8080/api/matches/statistics
```

## Complete Match Flow Example

Here's a complete example of creating and playing a tennis match:

### 1. Create Match
```bash
MATCH_RESPONSE=$(curl -s -X POST http://localhost:8080/api/matches \
  -H "Content-Type: application/json" \
  -d '{
    "player1Name": "Rafael Nadal",
    "player2Name": "Roger Federer"
  }')

MATCH_ID=$(echo $MATCH_RESPONSE | jq -r '.matchId')
PLAYER1_ID=$(echo $MATCH_RESPONSE | jq -r '.player1.playerId')
PLAYER2_ID=$(echo $MATCH_RESPONSE | jq -r '.player2.playerId')

echo "Match created: $MATCH_ID"
echo "Player 1 (Nadal): $PLAYER1_ID"
echo "Player 2 (Federer): $PLAYER2_ID"
```

### 2. Play First Game (Nadal wins 4-1)
```bash
# Nadal scores 4 points
for i in {1..4}; do
  curl -s -X POST http://localhost:8080/api/matches/$MATCH_ID/score \
    -H "Content-Type: application/json" \
    -d "{\"playerId\": \"$PLAYER1_ID\"}" > /dev/null
  echo "Nadal scores point $i"
done

# Federer scores 1 point
curl -s -X POST http://localhost:8080/api/matches/$MATCH_ID/score \
  -H "Content-Type: application/json" \
  -d "{\"playerId\": \"$PLAYER2_ID\"}" > /dev/null
echo "Federer scores 1 point"

# Check score
curl -s http://localhost:8080/api/matches/$MATCH_ID | jq '.currentScore'
```

### 3. Play Deuce Game
```bash
# Both players reach 40 (3 points each)
for i in {1..3}; do
  curl -s -X POST http://localhost:8080/api/matches/$MATCH_ID/score \
    -H "Content-Type: application/json" \
    -d "{\"playerId\": \"$PLAYER1_ID\"}" > /dev/null
  
  curl -s -X POST http://localhost:8080/api/matches/$MATCH_ID/score \
    -H "Content-Type: application/json" \
    -d "{\"playerId\": \"$PLAYER2_ID\"}" > /dev/null
done

echo "Deuce situation reached"

# Nadal gets advantage
curl -s -X POST http://localhost:8080/api/matches/$MATCH_ID/score \
  -H "Content-Type: application/json" \
  -d "{\"playerId\": \"$PLAYER1_ID\"}" > /dev/null
echo "Nadal has advantage"

# Nadal wins the game
curl -s -X POST http://localhost:8080/api/matches/$MATCH_ID/score \
  -H "Content-Type: application/json" \
  -d "{\"playerId\": \"$PLAYER1_ID\"}" > /dev/null
echo "Nadal wins the game"
```

### 4. Check Final Score
```bash
curl -s http://localhost:8080/api/matches/$MATCH_ID | jq '{
  currentScore: .currentScore,
  status: .status,
  sets: .sets
}'
```

## Rate Limiting

Currently, there are no rate limits implemented. In production, consider implementing:
- Request rate limiting per IP
- Authentication-based rate limiting
- Circuit breaker patterns for resilience

## Monitoring and Health Checks

The application provides health check endpoints:

```bash
# Application health
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info
```

## WebSocket Support (Future)

Future versions may include WebSocket support for real-time match updates:

```javascript
// Future WebSocket implementation
const socket = new WebSocket('ws://localhost:8080/ws/matches/{matchId}');
socket.onmessage = function(event) {
  const update = JSON.parse(event.data);
  console.log('Match update:', update);
};
```

## SDK Examples

### JavaScript/Node.js
```javascript
class TennisAPI {
  constructor(baseUrl = 'http://localhost:8080/api') {
    this.baseUrl = baseUrl;
  }

  async createMatch(player1Name, player2Name) {
    const response = await fetch(`${this.baseUrl}/matches`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ player1Name, player2Name })
    });
    return response.json();
  }

  async scorePoint(matchId, playerId) {
    const response = await fetch(`${this.baseUrl}/matches/${matchId}/score`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ playerId })
    });
    return response.json();
  }
}

// Usage
const api = new TennisAPI();
const match = await api.createMatch('Nadal', 'Federer');
await api.scorePoint(match.matchId, match.player1.playerId);
```

### Python
```python
import requests

class TennisAPI:
    def __init__(self, base_url='http://localhost:8080/api'):
        self.base_url = base_url

    def create_match(self, player1_name, player2_name):
        response = requests.post(
            f'{self.base_url}/matches',
            json={'player1Name': player1_name, 'player2Name': player2_name}
        )
        return response.json()

    def score_point(self, match_id, player_id):
        response = requests.post(
            f'{self.base_url}/matches/{match_id}/score',
            json={'playerId': player_id}
        )
        return response.json()

# Usage
api = TennisAPI()
match = api.create_match('Nadal', 'Federer')
api.score_point(match['matchId'], match['player1']['playerId'])
```

This API guide provides comprehensive examples for integrating with the Tennis Scoring System. For more details, refer to the interactive Swagger documentation at http://localhost:8080/swagger-ui.html when the application is running.