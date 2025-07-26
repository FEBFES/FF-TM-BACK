#!/usr/bin/env bash
# Prepares FF‑TM‑BACK for offline test execution.
# Execute this script once with network access.

set -euo pipefail

echo "Downloading Maven dependencies..."

# Build the shared commons module first
(
  cd febfes-commons
  ./mvnw -B install -DskipTests
)

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
