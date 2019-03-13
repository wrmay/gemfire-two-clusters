# Overview

This spring boot application uses spring data gemfire to read entries from one
cluster and write them to another.  _Note that this approach appears to work but it does
not because the data inserted into the second cluster is not readable by other
clients_ . This happens because the data is serialized to the second cluster using a PDX
type id from the first cluster.

# Walk Through

1. Make sure both clusters are running.

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

3. Run the spring boot app to copy the data into the second cluster. You will need to set some environment variables to point to the two clusters.

   ```bash
   cd two-cluster-client-1a
   export GEMFIRE_LOCATOR_1_HOST=localhost
   export GEMFIRE_LOCATOR_1_PORT=10000
   export GEMFIRE_LOCATOR_2_HOST=localhost
   export GEMFIRE_LOCATOR_2_PORT=20000
   mvn spring-boot:run
   ```

4. __This approach does not work.__Now connect to the second cluster and attempt to retrieve an entry. You should see an error similar to the one below.  

   ```bash
   gibson:two-cluster-client-1a rmay$ $GEMFIRE/bin/gfsh -e "connect --locator=localhost[20000]" -e "get --region=customer --key=1 --key-class=java.lang.Integer"

   (1) Executing - connect --locator=localhost[20000]

   Connecting to Locator at [host=localhost, port=20000] ..
   Connecting to Manager at [host=192.168.1.128, port=20099] ..
   Successfully connected to: [host=192.168.1.128, port=20099]


   (2) Executing - get --region=customer --key=1 --key-class=java.lang.Integer

   Message : Unknown pdx type=30168529
   Result  : false
   ```
