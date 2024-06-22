# to build the jar file
mvn clean install -DskipTests  

# use VSCode to build the image

# connect to Github
echo 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 | docker login ghcr.io -u USERNAME --password-stdin

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

# push image
docker push ghcr.io/lb-advisors/springboot:latest 


Environment variables:
DOCKER_USERNAME
DOCKER_GH_TOKEN
RENDER_TRIGGER_URL