# Start from a base image with Java
FROM openjdk:11

# Set the working directory in the container
WORKDIR /app

# Copy CPILint from your project directory to the container
COPY . /app/cpilint

# Set the CPILint directory in the PATH
ENV PATH="/app/cpilint/bin:${PATH}"

# set expose port
EXPOSE  8081:8081
CMD tail -f /dev/null