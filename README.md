# storm-docker
storm docker image for production deploy on kubernetes
# Apache Storm images for Docker and kubernetes

# Build images

* ```docker build -t <name>/storm-base base```
* ```docker build -t <name>/storm-nimbus nimbus```
* ``` docker build -t <name>/storm-worker worker```

Auto built at hub.docker.io/typhoon1986

# Run on kubernetes
* NOTE: make sure to configure xxxx files to use your own zookeeper address and zookeeper root.
We use an explicit zookeeper cluster on production to ensure write performance.
```
kubectl create -f kubernetes/*.json
```

# Submit a topology to the storm cluster
to build the sample topology:
```
cd storm_logcount_demo/
mvn package
```
This will build and pack the topology and put the jar file under 'target' directory. 
Copy the jar file to the storm client machine(you can use typhoon1986/storm-docker to create a pod, and copy the jar into the container)
and run:
```
/opt/apache-storm/bin/storm jar /path/to/stormdemo-1.0-jar-with-dependencies.jar storm.storm_logcount_demo.StormLogCountDemo
```

# View storm ui
we use NodePort to publish storm ui, findout the storm-ui pod ip, and open in web browser:
http://<storm-ui pod ip>:40003
