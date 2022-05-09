# Candidate Resource
## Solution Back-End Engineer Assignment

## Installation

This service is very easy to install and deploy in a Docker container.

Building image of service.

```sh
docker build -t candidate-resource:1.0.0 .
```

Once done, run the docker-compose command and map the port to whatever you wish on
your host. By default, the Docker will expose port 20001, so change this within the
docker-compose.yml file if necessary.
```sh
docker-compose -f docker-compose.yml up -d
```
This will create the candidate-resource service and postgres database.

Verify the deployment by curl to helathcheck endpoint.

```sh
curl --location --request GET 'http://localhost:20001/api/healthcheck'
```

After service and database have started, you can use any tools to connect to database and create tables in our database container.
Use this SQL script to create your tables.

```sh
CREATE TABLE candidates (
	id serial PRIMARY KEY,
	name VARCHAR ( 50 ) NOT NULL,
	dob VARCHAR ( 50 ) NOT NULL,
	bio_link VARCHAR ( 255 ) NOT NULL,
	image_link VARCHAR ( 255 ) NOT NULL,
	policy VARCHAR ( 255 ) NOT NULL,
    voted_count INT NOT NULL DEFAULT 0 
);
CREATE TABLE voters (
	id VARCHAR ( 50 ) PRIMARY KEY,
	is_voted BOOLEAN NOT NULL DEFAULT false
);
```

Then you're good to go with candidate-resource service!

## ::FYI::
## Export csv endpoint
CSV file willl be located at `/src/candidate-resource/stage` in container of candidate resource service.

# Apart from the main requrirements
## POST Create Voter
API to create voter who is able vote the candidate
Example curl

```sh
curl -X POST {endpoint}/api/people \
--header 'Authorization: Bearer xxxx' \
-d '{
    "nationalId": "1111111111114‬"
}'
```

Expected Response:
```sh
# Success
{
    "nationalId": "1111111111114‬",
    "isVoted": false
}
```
## Response Detail
| Parameter | Description |
| ------ | ------ |
| nationalId | national id of voter |
| isVoted | status of voter (already voted or not as true/false) |