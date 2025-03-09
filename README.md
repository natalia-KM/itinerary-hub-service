[![codecov](https://codecov.io/gh/natalia-KM/itinerary-hub-service/branch/master/graph/badge.svg?token=OI0QDW1TXV)](https://codecov.io/gh/natalia-KM/itinerary-hub-service)

# Itinerary Hub Service

Service for the ItineraryHub

## Setup

1. Add `.env` file
2. Set profile to `local`
3. Run:

```bash
./gradlew bootJar
./gradlew bootRun
```

## Docs
If you're updating any of the enpoints, run `./gradlew generateOpenApiDocs` (service must be running)

The docs will be checked in CI, but if you wish to check them locally simply run `./gradlew checkDocs` (the generated docs must be committed for this to work)