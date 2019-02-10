# Used to set up the server on the VM
# Code based on Dockerfile provided by team 700 for seng302 2018, University of Canterbury. Used with permission.

# FROM: Initializes a new build stage and sets the Base Image for subsequent instructions. A Dockerfile must start with a FROM instruction.
# Defining the JDK we will use to run the .jar
FROM openjdk:8-jre


# ADD: Copies new files, directories or remote URLS from the source and adds them to the filesystem of the image at the path.
# We put the resources we need in the top level target directory, AND in the server target directory
CMD ["ls", "-al"]
ADD ./server/target /target

# ENV: Sets the environment variable to to the given value. This value will be in the environment for all subsequent instructions in the build stage.
# We want to sync the server time to the same time that is in our SQL database on csse.
ENV TZ Pacific/Auckland


# RUN: Executes any commands in a new layer on top of the current image and commit the results. the resulting committed image will be used for the next step in the Dockerfile.
# no need for this command yet

# WORKDIR sets the working directory for any RUN, CMD, and ADD instructions that follow it in the Dockerfile.
# We move into the top level target directory to run the .jar
WORKDIR /target
CMD ["ls", "-al"]
EXPOSE 80


# CMD: There can only be one CMD instruction. Its main purpose is to provide defaults for an executing container.
# We run the command to start the jar in the target directory using the JDK we defined.
CMD ["java", "-jar", "server-0.0.jar"]