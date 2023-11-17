# Build a plain Native Image app

```mvn -Pnative native:compile```

It's a standard native compilation command that would work on any Spring Boot app with GraalVM Native Image support enabled as a dependency.


# Optimize performance

## PGO

One of the most powerful performance optimizations in Native Image is profile-guided optimizations (PGO).

Build an instrumented image: 

```mvn -Pinstrumented native:compile```

Run the app and apply relevant workload:

```./target/demo-instrumented```

```hey -n=1000000 http://localhost:8080/hello```

after you shut down the app, you'll see an `iprof` file in your working directory.

Build an app with profiles (they are being picked up via `<buildArg>--pgo=${project.basedir}/default.iprof</buildArg>`):

```mvn -Poptimized native:compile```


## ML-enabled PGO

The PGO approach described above, where the profiles are customly collected and tailored for your app, is the recommended way to do PGO in Native Image. There can be situations though when collecting profiles is not possible – for example, because of your deployment model or other reasons. In that case, it's still possible to get profiling information and optimize the app based on it via ML-enabled PGO. Native Image contains a pre-trained ML model that predicts the probabilities of the control flow graph branches, which lets us additionally optimize the app. This is again available in Oracle GraalVM and you don't need to enable it – it kicks in automatically  in the absence of custom profiles. If you are curios about the impact if this optimization, you can disable it with `-H:-MLProfileInference`. In our measurements, this optimization provides ~6% runtime performance improvement, which is pretty cool for an optimization you automatically get out of the box.



## G1 GC

There could be different GC strategies.  The default GC in Native Image, Serial GC, can be beneficial in certain scenarios, for example if you have a short-lived application or want to optimize memory usage. But if you are aiming for the best peak throughput, our general recommendation is to try the G1 GC (Note that you need Oracle GraalVM for it). In our `optimized` profile it's enabled via `<buildArg>--gc=G1</buildArg>`.

## Optimization levels in Native Image

# Dev Mode

`-Ob`