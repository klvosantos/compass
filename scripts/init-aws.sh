#!/bin/bash

# Name of the LocalStack container
CONTAINER_NAME="localstack"

# Check if the container is already running
if [ $(docker ps -q -f name=${CONTAINER_NAME}) ]; then
    echo "Stopping existing container: ${CONTAINER_NAME}"
    docker stop ${CONTAINER_NAME}

    # Wait until the container is fully stopped
    while [ $(docker ps -q -f name=${CONTAINER_NAME}) ]; do
        echo "Waiting for ${CONTAINER_NAME} to stop..."
        sleep 1
    done

    echo "Removing existing container: ${CONTAINER_NAME}"
    docker rm ${CONTAINER_NAME}
fi

# Start the LocalStack container
echo "Starting new LocalStack container"
docker run --rm -d \
  --name ${CONTAINER_NAME} \
  -p 4566:4566 \
  -e SERVICES=sns,sqs \
  -e DOCKER_HOST=unix:///var/run/docker.sock \
  -v /var/run/docker.sock:/var/run/docker.sock \
  localstack/localstack

# Wait for LocalStack to initialize (adjust the sleep time as needed)
echo "Waiting for LocalStack to initialize..."
sleep 10  # Increase wait time if necessary

# Set the LocalStack endpoint
LOCALSTACK_ENDPOINT="http://localhost:4566"

# Function to check if LocalStack is ready
function check_localstack {
    local retries=10
    while [ $retries -gt 0 ]; do
        if docker exec ${CONTAINER_NAME} awslocal sqs list-queues > /dev/null 2>&1; then
            echo "LocalStack is ready!"
            return
        fi
        echo "Waiting for LocalStack to be ready..."
        sleep 2
        ((retries--))
    done
    echo "LocalStack did not start in time!"
    exit 1
}

check_localstack

# Create SQS queues using the AWS CLI with LocalStack endpoint
echo "Creating SQS queues..."
docker exec ${CONTAINER_NAME} awslocal sqs create-queue --queue-name partialPaymentQueue || { echo "Failed to create partialPaymentQueue"; exit 1; }
docker exec ${CONTAINER_NAME} awslocal sqs create-queue --queue-name fullPaymentQueue || { echo "Failed to create fullPaymentQueue"; exit 1; }
docker exec ${CONTAINER_NAME} awslocal sqs create-queue --queue-name excessPaymentQueue || { echo "Failed to create excessPaymentQueue"; exit 1; }

echo "All queues created."
