#!/bin/bash
cd ~/../..  #Just to make sure we're at the root of the container. Depends on who the user is which is why theres a double
cd home/gitlab-runner/builds/9a494248/0/seng302-2018/team-600/server/ # Navigate to the server directory for the build
# echo 'FriskedStray8847' | sudo -S mvn clean package -Dmaven.test.skip=true > output.txt # Recreate the jar
cd .. # Go back to the main directory
# docker-compose build # Build the docker image
docker stop team600_web_1 # Stop the current server
docker container prune -f # Remove the current server completely
docker-compose up -d # Create the new server from the docker image we just built
