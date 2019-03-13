# Overview

This spring boot application uses spring data gemfire to read entries from one
cluster and writes them to a second cluster using the REST api.  Unlike the other examples, this one does use PDX serialization.

# Walk Through

1. Clean up from previous runs and make sure both clusters are running.

   ```bash
   export GEMFIRE=/Users/rmay/Downloads/pivotal-gemfire-9.5.2
   export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home
   python init_gemfire_clusters.py
   ```

2. Load some PDX serialized data into the first cluster.

   ```
   cd peopleloader
   python peopleloader.py --locator=localhost[10000] --region=person --count=113
   ```

3. Run the spring boot app to copy the data into the second cluster. You will need to set some environment variables to point to the two clusters.  Note that the REST API may not bind to localhost so you may need to figure how what IP address it *is* listening on.

   ```bash
   cd two-cluster-client-3a
   export GEMFIRE_LOCATOR_1_HOST=localhost
   export GEMFIRE_LOCATOR_1_PORT=10000
   export GEMFIRE_CLUSTER_2_REST_URL=http://192.168.1.128:20080/geode
   mvn spring-boot:run
   ```

   Note that this program will fail if the items already exist in the target cluster because it uses the POST method of the REST API.  

4. Run the dump program to verify that the objects can be read as POJOs.

   ```bash
   cd ../dump
   mvn spring-boot:run
   ```

   
