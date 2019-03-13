# Overview

This spring boot application demonstrates using a Function to enable a client to talk to two clusters.  This application reads entries from cluster_a using the normal region apis and writes those entries to cluster_b using a Function.

# Walk Through

1. Edit `cluster_b/cluster.json` to add the necessary types to the class path. See the "classpath" line in the snippet below.

   ```json
   ...   
   "datanode-properties" : {
           "conserve-sockets" : false,
           "log-level" : "config",
           "membership-port-range" : "10901-10999",
           "statistic-sampling-enabled" : "true",
           "log-file-size-limit" : "10",
           "log-disk-space-limit" : "100",
           "archive-file-size-limit" : "10",
           "archive-disk-space-limit" : "100",
           "statistic-archive-file" : "server.gfs",
           "log-file" : "server.log",
           "enable-network-partition-detection" : "true",
           "gemfire.ALLOW_PERSISTENT_TRANSACTIONS" : "true",
           "classpath" : "../../people-loader/target/people-loader-1.0-SNAPSHOT.jar",
           "jvm-options" : ["-Xmx128m","-Xms128m","-Xmn68m", "-XX:+UseConcMarkSweepGC", "-XX:+UseParNewGC", "-XX:CMSInitiatingOccupancyFraction=85"]
       },
   ...
   
   ```

2. Make sure both clusters are running, cleaning up from previous runs if needed.

   ```bash
   export GEMFIRE=/Users/rmay/Downloads/pivotal-gemfire-9.5.2
   export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_191.jdk/Contents/Home
   python init_gemfire_clusters.py
   ```

3. Load some DataSerializable people into the first cluster.

   ```
   cd peopleloader
   python peopleloader.py --locator=localhost[10000] --region=person --count=113 --serialization=dataserializable
   ```

4. Run the spring boot app to copy people from cluster_a to cluster_b using a Function

   ```bash
   cd two-cluster-client-2b
   export GEMFIRE_LOCATOR_1_HOST=localhost
   export GEMFIRE_LOCATOR_1_PORT=10000
   export GEMFIRE_LOCATOR_2_HOST=localhost
   export GEMFIRE_LOCATOR_2_PORT=20000
   mvn spring-boot:run
   ```

5. Now connect to the second cluster and verify that you can retrieve an entry with no problem.

   ```bash
   gibson:two-cluster-client-2b rmay$ $GEMFIRE/bin/gfsh -e "connect --locator=localhost[20000]" -e "get --region=customer --key=1 --key-class=java.lang.Integer"
   
   (1) Executing - connect --locator=localhost[20000]
   
   Connecting to Locator at [host=localhost, port=20000] ..
   Connecting to Manager at [host=192.168.1.128, port=20099] ..
   Successfully connected to: [host=192.168.1.128, port=20099]
   
   
   (2) Executing - get --region=customer --key=1 --key-class=java.lang.Integer
   
   Result      : true
   Key Class   : java.lang.Integer
   Key         : 1
   Value Class : io.pivotal.pde.dataserializable.sample.DataSerializablePerson
   
                              address                             | age | firstName | gender | id | lastName  | partitionByZipKey | pastAddresses | phone
   -------------------------------------------------------------- | --- | --------- | ------ | -- | --------- | ----------------- | ------------- | --------------------
   io.pivotal.pde.dataserializable.sample.DataSerializableAddress | 56  | Thea      | F      | 1  | Heathcote | 70365|00000001    | a Collection  | (980) 546-4419 x2777
   
   ```

