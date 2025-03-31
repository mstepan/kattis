# Kattis and Java

* Use `Java 21` according to [Kattis Java documentation](https://open.kattis.com/languages/java)
* Use maven `v3.9.9` with the [wrapper](https://maven.apache.org/wrapper/)
* Uses [Error Prone](https://errorprone.info/) as an additional compiler to `javac`.
* Uses [Spotless](https://github.com/diffplug/spotless/) for automatic code formatting
  in [Android Open Source Project](https://source.android.com/docs/setup/contribute/code-style) style.


## Build & run

### Standard maven

* Build self-executable jar file

```bash
./mvnw clean package
```

* Run application

```bash
DEBUG=true java -jar target/kattis-0.0.1-SNAPSHOT.jar 
```
