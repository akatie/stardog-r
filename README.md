Stardog/R
=========

Stardog extension wrapping R function calls as SPARQL custom functions.

## About

This extension expands the standard functionality of SPARQL in [Stardog](http://www.stardog.com/)'s query engine. 

## Build and Setup

You'll need a Stardog installation with a valid license. Stardog distributions and community and developer licenses can be obtained for free at http://www.stardog.com

You'll need to install the Java/R Interface (JRI). Please follow the instructions to install JRI at http://rforge.net/JRI/ (quick howto: execute `install.packages("rJava")` in your R environment).

1. Set your local Stardog install and home paths in `project.properties`
2. `cd function; ant dist`
3. A file `stardog-function-examples-1.0.jar` will be generated in `function/dist`. Copy that jar into `$STARDOG_HOME/server/pack/`
4. Run the Stardog server
5. R calls are available under the `stardog: <tag:stardog:api:>` namespace. For instance, this SPARQL query executes the Wilcoxon signed-rank test over two given RDF Data Cube slices:

   `./stardog query mydb "prefix stardog: <tag:stardog:api:> prefix eg: <http://example.org/ns#> select ?foo where { bind(stardog:R(\"wilcox.test\", eg:slice1, eg:slice2, eg:height) as ?foo)}"`

## Requirements

- JDK/JRE 1.7
- Ant 1.9.4
- R >= 3.0.2
- JRI >= 0.5-5

