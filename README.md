[![codecov](https://codecov.io/gh/natalia-KM/itinerary-hub-service/branch/master/graph/badge.svg?token=OI0QDW1TXV)](https://codecov.io/gh/natalia-KM/itinerary-hub-service)

# Itinerary Hub Service

---
Service for the ItineraryHub


## Setup

---
1. Add `.env` file
2. Set profile to `local`
3. Run:

```bash
./gradlew bootJar
./gradlew bootRun
```

## Database

---
You must have a configured db before running the service locally.
To configure a db you must have Postgres17 and pgAdmin4 installed.


<details>
<summary>
Getting the schema
</summary>


#### Option 1.

Run in your terminal 
```bash
pg_dump --host=your-neon-host --port=5432 --username=your-username --password --dbname=your-database -s --file=schema_backup.sql
```


#### Option 2.

If you're going to update schema regularly then you can add the credentials to your system.

1. Add <code>pgpass.conf</code> file to

> C:\Users\{your_user}\AppData\Ro aming\postgresql

with the following:

>hostname:5432:database:username:password



2. Add environment variables to the Windows System Variables:
   - PGUSER=your-username
   - PGDATABASE=your-database
   - PGHOST=your-neon-host
   - PGPORT=5432

3. Run the following command to get the schema

```bash
pg_dump -s --file=schema_backup.sql
```

</details>

<details><summary>pgAdmin setup</summary>

1. Create a new database
2. Open PSQL (you might have to connect to the server first)
3. Run 

```bash
\i 'C:/{path}/schema_backup.sql'
```

</details>

> **IMPORTANT**:  
> As there is a time constraint on this project, schema versioning will not be implemented.  
> If you make any schema changes, you can either delete your local database and follow the two steps 
> above again, or copy the SQL query from Neon and apply it locally.

## Docs

---
If you're updating any of the endpoints, run `./gradlew generateOpenApiDocs` (service must be running)

The docs will be checked in CI, but if you wish to check them locally simply run `./gradlew checkDocs` (the generated docs must be committed for this to work)

## Google Auth

--- 
To test locally make sure to set secrets in properties

## Deployment

--- 
Make sure any new env vars are set in Fly.io instance

docs: https://fly.io/docs/flyctl/