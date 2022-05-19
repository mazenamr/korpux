# korpux
a java server used to index and rank html pages for use in a search engine 

## Usage

to run the indexer using maven:

```
mvn clean compile exec:java -Dexec.mainClass="com.mazxn.korpux.indexer.Indexer"
```

to run the query engine using maven:

```
mvn clean compile exec:java -Dexec.mainClass="com.mazxn.korpux.queryengine.QueryEngine"
```