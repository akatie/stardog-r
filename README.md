Stardog/R
=========

Stardog extension wrapping R function calls as SPARQL custom functions.

## About

This extension expands the standard functionality of SPARQL in [Stardog](http://www.stardog.com/)'s query engine, by allowing R functions (such as test statistics) to be called directly from SPARQL queries.

## Build and Setup

You'll need a Stardog installation with a valid license. Stardog distributions and community and developer licenses can be obtained for free at http://www.stardog.com

You'll need to install the Java/R Interface (JRI). Please follow the instructions to install JRI at http://rforge.net/JRI/ (quick howto: execute `install.packages("rJava")` in your R environment).

1. Set your local Stardog install and home paths in `project.properties`
2. `cd function; ant dist`
3. A file `stardog-function-examples-1.0.jar` will be generated in `function/dist`. Copy that jar into `$STARDOG_HOME/server/pack/`
4. Add the following lines to the script `$STARDOG_HOME/bin/stardog-admin`:
   1. Make Stardog aware of where your R install lives: `export R_HOME="/usr/lib/R"` (Linux), or `export R_HOME="/Library/Frameworks/R.framework/Resources"` (MacOS X)
   2. Make Stardog aware of where your JRI shared libraries live. This can be achieved in [many ways](http://www.chilkatsoft.com/java-loadLibrary-MacOSX.asp). Recommended: in Linux, modify the LD_LIBRARY_PATH environment variable (add `export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/usr/local/lib/R/site-library/rJava/jri` using your valid JRI path to `$STARDOG_HOME/bin/stardog-admin`; in MacOS X, it's easier to symlink the library `libjri.so` in your Java library path (you can get your Java library path by `System.out.println(System.getProperty("java.library.path"));`).
5. Run the Stardog server
6. R calls are available under the `stardog: <tag:stardog:api:>` namespace. For instance, this SPARQL query executes the Wilcoxon signed-rank test over two given RDF Data Cube slices:

   `./stardog query mydb "prefix stardog: <tag:stardog:api:> prefix eg: <http://example.org/ns#> select ?foo where { bind(stardog:R(\"wilcox.test\", eg:slice1, eg:slice2, eg:height) as ?foo)}"`

## Requirements

- JDK/JRE 1.7
- Ant 1.9.4
- R >= 3.0.2
- JRI >= 0.5-5

