# to build the jar file
mvn clean install -DskipTests  

# use VSCode to build the image

# connect to Github
echo YOUR_TOKEN | docker login ghcr.io -u USERNAME --password-stdin

# push image
docker push ghcr.io/oleblond-lb/springboot:latest 