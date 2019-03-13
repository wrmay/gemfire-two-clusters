## Overview

There is a perceived limitation with GemFire that a client cannot connect to
two clusters.  Technically, the limitation is that a client cannot declare two
regions with the same name.  There is also a problem with using PDX serialization when connected to two cluster.

This project demonstrates a few ways to work around the problems with connecting to multiple clusters  without using
undocumented APIs.

## Option 1: Use Two Pools

This option applies when

- you do not need to access regions with the same name in different clusters
- and you are not using PDX serialization

This is the easiest case.  Simply use a client configuration like the one shown below.  This example uses Spring Data GemFire but the same thing can be acheived with native GemFire/Geode configuration.



```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:util="http://www.springframework.org/schema/util"
	xmlns:gfe="http://www.springframework.org/schema/gemfire"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-4.3.xsd
		http://www.springframework.org/schema/gemfire http://www.springframework.org/schema/gemfire/spring-gemfire-2.0.xsd">

	
	<gfe:client-cache id="cache" pdx-read-serialized="false"
		pool-name="pool1" />

	<gfe:pool id="pool1" read-timeout="10000">
		<gfe:locator host="${GEMFIRE_LOCATOR_1_HOST}" port="${GEMFIRE_LOCATOR_1_PORT}" />
	</gfe:pool>
	
	<gfe:pool id="pool2" read-timeout="10000">
		<gfe:locator host="${GEMFIRE_LOCATOR_2_HOST}" port="${GEMFIRE_LOCATOR_2_PORT}" />
	</gfe:pool>


	<gfe:client-region id="personRegion" name="person" 
		cache-ref="cache" pool-name="pool1" shortcut="PROXY" />

	<gfe:client-region id="customerRegion" name="customer" 
		cache-ref="cache" pool-name="pool2" shortcut="PROXY" />

	
</beans>

```



## Option 2: Use onServer Functions to Access One Cluster

This approach can be used whenever you are not using PDX serialization.  It can be used to copy data from one cluster to another even if the regions involved have the same name.  

The technique in this case is to use a Function to perform all access to one of the clusters. The `two-cluster-client-2b` project shows how to use this technique to copy objects from one cluster to another.

The key that makes this work is the "PutFunction" which is installed on cluster b.  The execute method is shown below.

```java
	public void execute(FunctionContext context) {		
		Object []args=(Object [])context.getArguments();
        // arg[0] = region name
        // arg[1] = key
        // arg[2] = val
        
        // validation and error handling ommitted for clarity
		
		Region region = CacheFactory.getAnyInstance().getRegion((String) args[0]);
		
		Object result = region.put(args[1], args[2]);
		
		context.getResultSender().lastResult(result);
	}

```



*Note that this Function does not use RegionFunctionContext and is not a data aware Function.  This is an important aspect of the technique.*

The client should be configured as follows:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:util="http://www.springframework.org/schema/util"
	xmlns:gfe="http://www.springframework.org/schema/gemfire"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-4.3.xsd
		http://www.springframework.org/schema/gemfire http://www.springframework.org/schema/gemfire/spring-gemfire-2.0.xsd">

	
	<gfe:client-cache id="cache" pool-name="pool1" />

	<gfe:pool id="pool1" read-timeout="10000">
		<gfe:locator host="${GEMFIRE_LOCATOR_1_HOST}" port="${GEMFIRE_LOCATOR_1_PORT}" />
	</gfe:pool>
	
	 <gfe:pool id="pool2" read-timeout="10000">
		<gfe:locator host="${GEMFIRE_LOCATOR_2_HOST}" port="${GEMFIRE_LOCATOR_2_PORT}" />
	</gfe:pool>
	

	<gfe:client-region id="personRegion" name="person" 
		cache-ref="cache" pool-name="pool1" shortcut="PROXY" />

</beans>
```



Note that the declared region as well as the cache element point to "pool1". "pool2"  is declared but not referenced by anything. 

The copy functionality can then be implemented using code similar to that shown below.

```java
void copyRegion(){
	Set<Integer> keys = personRegion.keySetOnServer(); 
	for(Integer k: keys){
		DataSerializablePerson p = personRegion.get(k);
			
		Object []args = new Object []{"customer",k, p};
    Execution<Object[],DataSerializablePerson,DataSerializablePerson> exec = 
					FunctionService.onServer(pool).setArguments(args).withCollector(new AnyResultCollector<DataSerializablePerson>());
			ResultCollector<DataSerializablePerson,DataSerializablePerson> rc = exec.execute("io.pivotal.pde.sample.PutFunction");
			Person person = rc.getResult();
		}
	}
}
```



## Option 3: Use the REST API with One or Both Clusters

This option has the advantage of working even when you are using PDX serialization.  There is no problem with accessing regions with the same name in two clusters. `two-cluster-client-3a` illustrates this approach.  The GemFire context looks like this.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:util="http://www.springframework.org/schema/util"
	xmlns:gfe="http://www.springframework.org/schema/gemfire"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util-4.3.xsd
		http://www.springframework.org/schema/gemfire http://www.springframework.org/schema/gemfire/spring-gemfire-2.0.xsd">

	
	<gfe:client-cache id="cache" pdx-read-serialized="true"
		pool-name="pool1" pdx-serializer-ref="pdxSerializer" />

	<gfe:pool id="pool1" read-timeout="10000">
		<gfe:locator host="${GEMFIRE_LOCATOR_2_HOST}" port="${GEMFIRE_LOCATOR_2_PORT}" />
	</gfe:pool>
	

	<gfe:client-region id="customerRegion" name="customer" 
		cache-ref="cache" pool-name="pool1" shortcut="PROXY" />

	<bean id="pdxSerializer" class="org.apache.geode.pdx.ReflectionBasedAutoSerializer">
		<constructor-arg>
			<util:list value-type="java.lang.String">
				<value>io.pivotal.pde.sample.*</value>
			</util:list>
		</constructor-arg>
	</bean>
	
	<bean id="restClient" class="io.pivotal.datatx.twoclusterclient.RESTClient" >
		<property name="baseURL" value="${GEMFIRE_CLUSTER_2_REST_URL}"/>
	</bean>
	
</beans>
```

Only one of the two clusters is declared.  The second cluster is accessed via the REST API.  *Note that read-serialized is set to true* so that a get on the first cluster will return a PDXInstance.   The java snippet below illustrates calling the REST API on the second cluster to put a "Person" object that has been retrieved as a PDX instance.

```java
public void put(String region, Object key, PdxInstance value){
	String postURL = String.format("%s/v1/%s", baseURL,region);
	log.info("POST TO " + postURL);
		
	TypePreservingPdxToJSON converter = new TypePreservingPdxToJSON(value);
    String body = JSONFormatter.toJSON(converter.getJSON()); 
	log.info("BODY: " + body);
		
	try {
		HttpResponse<JsonNode> jsonResponse = 
            Unirest.post(postURL)
			.header("accept", "application/json")
			.header("Content-Type", "application/json")
			.queryString("key", key.toString())
			.body(body)
			.asJson();

			int rc = jsonResponse.getStatus();
			if (rc != 201)
				throw new RuntimeException("That didn't work.  rc was " + rc);
				
		} catch (UnirestException e) {
			log.error("An error occurred while accessing the REST API", e);
			throw new RuntimeException("That didn't work");
		}
	}
```

The key part is using the TypePreservingPdxtoJSON class ( availabe in `two-cluster-client-3a`) to create the body for the REST call.  This ensures that other GemFire clients, including Java clients using the Person POJO,  will be able to access data in the second cluster.

#### Caveats

There are some caveats that apply to interoperation between REST data and java POJOS.  For details, see http://gemfire.docs.pivotal.io/97/geode/rest_apps/rest_prereqs.html.  The most significant points are:

- keys that are PUT via the REST API will be turned into Strings regardless of the original type.
- When retrieved into a Java client, POJOs written via REST will be of type PdxInstance _even if the client has set read-serialized=false_.  The original object can be retrieved with `pdxInst.getObject()`.  This is illustrated in the `dump` project.

## Summary

This document has described 3 techniques for accessing multiple clusters from a single client.  

If you are not using PDX serialization and the regions you need to interact with have different names, you can just use 2 pools as shown in Option 1.

Option 2 shows how to access Functions on one cluster while using normal region access for the second cluster.

If you are using PDX, you can still access 2 clusters by using the REST API for one of the clusters. 