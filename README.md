# Build a Native Spring Application

```mvn -Pnative native:compile```

It's a standard native compilation command that would work on any Spring Boot app with GraalVM Native Image support enabled as a dependency.

# Spring Boot AOT Engine and GraalVM

By default, at runtime Spring pulls your app configuration from different sources, and creates an internal representation of your app. What's interesting, GraalVM Native Image does a similar thing – analyzes input and creates an internal representation of your app – but at build time. The Spring AOT engine bridges this gap between two worlds. It does two things: one is transforming your app configuration into native-friendly functional configuration. It also generates three kinds of input for Native Image:

* Java source code (functional configuration)
* Bytecode for things like dynamic proxies
* Runtime hints for dynamic Java features (reflection, resources, etc). 


# Dev Mode

For development purposes, you can speed up native builds by passing the `-Ob` flag: either via the command line, or in the Native Maven plugin:

```xml
<plugin>
  <groupId>org.graalvm.buildtools</groupId>
      <artifactId>native-maven-plugin</artifactId>
          <configuration>
              <buildArgs>
                  <buildArg>-Ob</buildArg>
              </buildArgs>
            </configuration>
</plugin>
```

This will speed up the compilation phase, and therefore the overall build time will be ~15-20% faster.

This is intended as a dev mode, make sure to remove the flag before deploying to production to get the best performance.

# Optimize performance

## PGO 🚀

One of the most powerful performance optimizations in Native Image is profile-guided optimizations (PGO).

1. Build an instrumented image: 

```mvn -Pnative,instrumented native:compile```

2. Run the app and apply relevant workload:

```./target/demo-instrumented```

```hey -n=1000000 http://localhost:8080/hello```

after you shut down the app, you'll see an `iprof` file in your working directory.

3. Build an app with profiles (they are being picked up via `<buildArg>--pgo=${project.basedir}/default.iprof</buildArg>`):

```mvn -Pnative,optimized native:compile```


## ML-enabled PGO 👩‍🔬

The PGO approach described above, where the profiles are customly collected and tailored for your app, is the recommended way to do PGO in Native Image. 

There can be situations though when collecting profiles is not possible – for example, because of your deployment model or other reasons. In that case, it's still possible to get profiling information and optimize the app based on it via ML-enabled PGO. Native Image contains a pre-trained ML model that predicts the probabilities of the control flow graph branches, which lets us additionally optimize the app. This is again available in Oracle GraalVM and you don't need to enable it – it kicks in automatically  in the absence of custom profiles. 

If you are curious about the impact if this optimization, you can disable it with `-H:-MLProfileInference`. In our measurements, this optimization provides ~6% runtime performance improvement, which is pretty cool for an optimization you automatically get out of the box.


## G1 GC 🧹

There could be different GC strategies. The default GC in Native Image, Serial GC, can be beneficial in certain scenarios, for example if you have a short-lived application or want to optimize memory usage. 

If you are aiming for the best peak throughput, our general recommendation is to try the G1 GC (Note that you need Oracle GraalVM for it). 

In our `optimized` profile it's enabled via `<buildArg>--gc=G1</buildArg>`.

## Optimization levels in Native Image

There are several levels of optimizations in Native Image, that can be set at build time:

- `-O0` - No optimizations: Recommended optimization level for debugging native images;

- `-O1` - Basic optimizations: Basic GraalVM compiler optimizations, still works for debugging;
 
- `-O2`  - Advanced optimizations: default optimization level for Native Image;

- `-O3` - All optimizations for best performance;

- `-Ob` - Optimize for fastest build time: use only for dev purposes for faster feedback, remove before compiling for deployment;

- `-pgo`: Using PGO will automatically trigger `-O3` for best performance.


# Testing 🧪

GraalVM's Native Build Tools support testing applications as native images, including JUnit support. The way this works is that your tests are compiled as native executables to verify that things work in the native world as expected. Test our application with the following:

 ```mvn -PnativeTest test```

In our example, `HttpRequestTest` will verify that the application returns the expected message.

Native testing recommendation: you don't need to test in the mode all the time, especially if you are working with frameworks and libraries that support Native Image – usually everything just works. Develop and test your application on the JVM, and test in Native once in a while, as a part of your CI/CD process, or if you are introducing a new dependency, or changing things that are sensitive for Native Image (reflection etc). 

# Using libraries

When using libraries in native mode, some things such as reflection, resources, proxies might have to be made "visible" to Native Image at build time via configuration. Now the word "configuration" doesn't mean that this is something that you need to do manually as a user – let's look at all the many ways how this can just work.

* Ideally, a library would include the necessary config files. Example: [H2](https://github.com/h2database/h2database/blob/master/h2/src/main/META-INF/native-image/reflect-config.json), [OCI Java SDK](https://github.com/oracle/oci-java-sdk/blob/master/bmc-adm/src/main/resources/META-INF/native-image/com.oracle.oci.sdk/oci-java-sdk-adm/reflect-config.json). In this case no further action needed from a user – things just work.
* In cases when a library doesn't (yet) support GraalVM, the next best option is having configuration for it in the [GraalVM Reachability Metadata Repository](https://github.com/oracle/graalvm-reachability-metadata). It's a centralized repository where both maintainers and users can contribute and then reuse configuration for Native Image. It's integrated into [Native Build Tools](https://github.com/graalvm/native-build-tools) and now enabled by default, so as a user, again things just work.<br>
For both of those options, a quick way to asses whether your dependencies work with Native Image is the ["Ready for Native Image"](https://www.graalvm.org/native-image/libraries-and-frameworks/) page. Note that this is a list of libraries that are *known* to be continuously testing with Native Image, and there are more compatible libraries out there; but this is a good first step for assessment. 
* You can use framework support to produce custom “hints” for Native Image:
```java
runtimeHints.resources().registerPattern(“config/app.properties”); //register a resource
```
```java
@Reflective //flag elements that require reflection
```
* You can use the Tracing Agent to produce the necessary config [automatically](https://www.graalvm.org/latest/reference-manual/native-image/metadata/AutomaticMetadataCollection/).
* You can provide/extend config for reflection, JNI, resources, serialization, and predefined classes [manually in JSON](graalvm.org/latest/reference-manual/native-image/metadata/#specifying-metadata-with-json).


# Configuring reflection, resources, proxies

There is a way to automatically generate configuration files for Native Image. In our example, we have `ReflectionController`, which accesses a field in a different class at runtime, and `ResourceController`, which is reading `message.xml` at runtime. To make those calls visible and automatically resolved by Native Image, run the tracing agent:

```shell
java -agentlib:native-image-agent=config-output-dir=./resources/META-INF/native-image  -jar ./target/demo-0.0.1-SNAPSHOT.jar
```

As the app is running, access the corresponding endpoints (`http://localhost:8080/reflection`, `http://localhost:8080/resource`) to emulate relevant workload. The agent will observe those call, produce configuration files in `resources/META-INF/native-image`. As this is a known location, Native Image will pick up the config files automatically. Rebuild the app and access the endpoints to verify:

```shell
mvn -Pnative native:compile
./target/demo
http://localhost:8080/reflection
http://localhost:8080/resource
```

# Monitoring 📈

Build an application with monitoring features enabled:

```shell
mvn -Pmonitored native:compile
```
This will trigger a profile with the following `buildArgs`: `--enable-monitoring=heapdump,jfr,jvmstat`. You can also opt for using just one of those monitoring features. 

<!-- add Micrometer -->