## Usage

To just run the microbenchmarks, you can skip unit tests, and just run the produced `benchmarks.jar`

```shell
mvn clean package \
    -DskipTests -am -pl benchmark \
    && java -jar benchmark/target/original-matt-benchmark-1.0-SNAPSHOT-shaded.jar
```
