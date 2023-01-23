#!/usr/bin/env bash

./mvnw clean package -Dmaven.test.skip=true

echo 'Copy files...'

scp -i path_to_ssh_key \
    target/ff-tm-back-0.0.1-SNAPSHOT.jar \
    root@ip:/home/febfes/

echo 'Restart server...'

ssh -i path_to_ssh_key root@ip << EOF

pgrep java | xargs kill -9
nohup java -jar ../home/febfes/ff-tm-back-0.0.1-SNAPSHOT.jar > log.txt &

EOF

echo 'Bye'
