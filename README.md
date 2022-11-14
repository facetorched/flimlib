# FLIMLib
[![](https://github.com/flimlib/flimlib/actions/workflows/build-main.yml/badge.svg)](https://github.com/flimlib/flimlib/actions/workflows/build-main.yml)

FLIMLib is a curve fitting library used for Fluorescent Lifetime Imaging or
FLIM. It is developed by Paul Barber (UCL and KCL, London) and the Advanced Technology Group at the
[Oxford Institute for Radiation Oncology](https://www.oncology.ox.ac.uk/),
University of Oxford, as well as the [Laboratory for Optical and Computational
Instrumentation](https://loci.wisc.edu/) at the University of
Wisconsin-Madison. FLIMLib is used for FLIM functionality in the [Time Resolved
Imaging](https://www.assembla.com/spaces/ATD_TRI/wiki) (TRI2) software, as well
as in the [FLIMJ plugin for ImageJ](https://imagej.net/FLIMJ).

For exponential lifetime fitting there are three core algorithms within FLIMLib:

1. A triple integral method that does a very fast estimate of a single
   exponential lifetime component.
2. A Levenberg-Marquardt algorithm or LMA that uses an iterative,
   least-squares-minimization approach to generate a fit. This works with
   single, double and triple exponential models, as well as stretched
   exponential.
3. A Bayesian algorithm that combines evidence from each single photon to 
   estimate lifetimes etc. It offers better performance with low photon counts.

There is also code to perform 'global' analysis over a number of signals
simultaneously (e.g. over an image), where the lifetimes can be considered
constant across the data set, but the amplitudes are allowed to vary for each
signal. There is also a completely generic global analysis function. A third
algorithm is available to perform phasor analysis.

In addition there is a non-negative linear least squares algorithm that is
useful for spectral unmixing in combined spectral-lifetime imaging (SLIM).

The FLIMLib library code is written in C89 compatible C and is thread-safe for
fitting multiple pixels concurrently. A Java interface (generated by
[SWIG](http://www.swig.org) is privided to call the library from Java
code: `FLIMLib.java` provide a subset of function calls used by the FLIMJ
plugin for ImageJ.

Additionally, there is wrapper code in `FLIMLib.i` to wrap the external
functions in `flimlib.def`. This code generates swig wrapper files which enable
you to call these functions from Java.

## See also

* [FLIMLib wiki](https://github.com/flimlib/flimlib/wiki)
* [FLIMLib web site](https://flimlib.github.io/)
* [FLIMLib Doxygen docs](http://code.imagej.net/flimlib/html/)

## Directory contents

| Directory                       | Contents                                                               |
| ------------------------------- | ---------------------------------------------------------------------- |
| `src/main/c`                    | The source files for the FLIMLib library                               |
| `src/main/cpp`                  | The C++ include file for a FLIMLib class for use in C++ projects       |
| `target/generated-sources/main` | The Java API and C++ wrapper generated by SWIG                         |
| `src/main/java`                 | The rest of the Java API source files                                  |
| `src/main/python`               | The Python API source files (ctypes-based)                             |
| `src/main/swig`                 | The SWIG sources that directs Java API generation                      |
| `src/flimlib-cmd/c`             | The source files for the standalone executable wrapper for the library |
| `src/flimlib-cmd/cpp`           | The source files for the standalone executable written in C++          |
| `src/matlab`                    | Wrapper and example code for use of the library with Matlab            |
| `test_files`                    | `.dat` and `.ini` settings file for testing                            |
| `target/natives`                | Compiled library binary                                                |

## Building the source (C++/Java)

You need JDK, Maven, CMake, SWIG, and C and C++ toolchains (GCC on Linux,
Command Line Tools or Xcode on macOS, Visual Studio (with C++ Desktop
Development) on Windows) to be installed.

To build the library and standalone program using maven:

  ```
  mvn clean install
  ```

## Running the standalone executable

1.  Copy the executable to the `test_files` folder for convenience

    ```
    cp target/build/bin/flimlib-cmd ./test_files
    ```

2.  Run the program with the test files

    ```
    cd ./test_files
    ./flimlib-cmd test.ini transient.dat
    ```

## Using from a Java project

To depend on FLIMLib from Maven, simply copy the following to appropriate places in your `pom.xml`:

```xml
<properties>
  <flimlib.version>2.1.0</flimlib.version>
</properties>

<!-- FLIMLib Java interface -->
<dependency>
  <groupId>flimlib</groupId>
  <artifactId>flimlib</artifactId>
  <version>${flimlib.version}</version>
</dependency>
<!-- FLIMLib native binary -->
<dependency>
  <groupId>flimlib</groupId>
  <artifactId>flimlib</artifactId>
  <version>${flimlib.version}</version>
  <classifier>${scijava.natives.classifier}</classifier>
  <!-- Or one of the following if you would like to manually specify the binary platform -->
  <!-- <classifier>native-linux_64</classifier> -->
  <!-- <classifier>native-windows_64</classifier> -->
  <!-- <classifier>native-osx_64</classifier> -->
</dependency>
```

*Note that the native binary is platform-dependent. So you may want to make sure that the `<classifier>` attribute is either automatically detected by the parent `scijava` pom (`${scijava.natives.classifier}`) or manually filled in to match your platform.*

## Using from Python

The Python API is a ctypes-based wrapper around a few of the library functions.

```sh
pip install flimlib
```

```py
import flimlib
```

To get started, see the help (docstrings) for these functions:
- `flimlib.GCI_marquardt_fitting_engine()` (Levenberg-Marquardt)
- `flimlib.GCI_triple_integral_fitting_engine()` (RLD: rapid lifetime
  determination)
- `flimlib.GCI_Phasor()` (phasor analysis)
