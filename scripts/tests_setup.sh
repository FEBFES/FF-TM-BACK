#!/usr/bin/env bash
# Prepares FF‑TM‑BACK for offline test execution.
# Execute this script once with network access.

set -euo pipefail

echo "Downloading Maven dependencies..."

# Build shared modules first
for shared in febfes-commons fftm-grpc-api
do
  (
    cd "$shared"
    mvn -B install -DskipTests
  )
done

# Fetch dependencies for each service
for module in authentication config-server gateway notification ff-tm-back
do
  echo "Processing $module ..."
  (
    cd "$module"
    ./mvnw -B dependency:go-offline -DskipTests
  )
done

echo "Setup complete. Maven dependencies cached in ~/.m2"
