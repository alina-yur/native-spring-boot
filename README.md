# Build a Native Spring Application

```mvn -Pnative native:compile```

It's a standard native compilation command that would work on any Spring Boot app with GraalVM Native Image support enabled as a dependency.

# Spring Boot AOT engine and GraalVM

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

```mvn -Pinstrumented native:compile```

2. Run the app and apply relevant workload:

```./target/demo-instrumented```

```hey -n=1000000 http://localhost:8080/hello```

after you shut down the app, you'll see an `iprof` file in your working directory.

3. Build an app with profiles (they are being picked up via `<buildArg>--pgo=${project.basedir}/default.iprof</buildArg>`):

```mvn -Poptimized native:compile```


## ML-enabled PGO 👩‍🔬

The PGO approach described above, where the profiles are customly collected and tailored for your app, is the recommended way to do PGO in Native Image. 

There can be situations though when collecting profiles is not possible – for example, because of your deployment model or other reasons. In that case, it's still possible to get profiling information and optimize the app based on it via ML-enabled PGO. Native Image contains a pre-trained ML model that predicts the probabilities of the control flow graph branches, which lets us additionally optimize the app. This is again available in Oracle GraalVM and you don't need to enable it – it kicks in automatically  in the absence of custom profiles. 

If you are curious about the impact if this optimization, you can disable it with `-H:-MLProfileInference`. In our measurements, this optimization provides ~6% runtime performance improvement, which is pretty cool for an optimization you automatically get out of the box.


### G1 GC 🧹

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

`HttpRequestTest` will verify that our application returns the expected message.

Native testing recommendation: you don't need to test in the mode all the time, especially if you are working with frameworks and libraries that support Native Image – usually everything just works. Develop and test your application on the JVM, and test in Native once in a while, as a part of your CI/CD process, or if you are introducing a new dependency, or changing things that are sensitive for Native Image (reflection etc). 

# Using libraries

# Monitoring 📈
