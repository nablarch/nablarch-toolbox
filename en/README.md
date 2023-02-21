# Nablarch-Toolbox

| master | develop |
|:-----------|:------------|
|[![Build Status](https://travis-ci.org/nablarch/nablarch-toolbox.svg?branch=master)](https://travis-ci.org/nablarch/nablarch-toolbox)|[![Build Status](https://travis-ci.org/nablarch/nablarch-toolbox.svg?branch=develop)](https://travis-ci.org/nablarch/nablarch-toolbox)|

## Output Conditions of the Authorized API List Creation Tool
Currently, output to the list file under the following conditions.

*  Package
    * Not output.


* Class (including abstract class, inner class, interface and annotation)
    * It is output only when @Published is attached to the class.


* Field constructor method
    * It is output only when @Published is attached to the element.


* Inheritance
    * The output conditions for both superclass and subclass are the same as above.
    * For field method constructors, both superclass and subclass defined in the class are output under the same conditions as when inheritance is not used.


* In terms of implementation,
    * it is the same as inheritance.


* Internal class
    * It is output under the same conditions as a general class (or field constructor method), regardless of whether an external class is output.


* External class
    * It is output under the same conditions as a general class (or field constructor method), regardless of whether an internal class is output.


## Dependent library

Manually install the following libraries in the local repository before compiling or testing this module.

Library          |File name       |Group ID     |Artifact ID   |Version  |
:-------------------|:----------------|:--------------|:--------------------|:------------|
Oracle JDBC Driver  |ojdbc6.jar       |com.oracle     |ojdbc6               |11.2.0.2.0   |
Oracle UCP for JDBC |ucp.jar          |com.oracle     |ucp                  |11.2.0.3.0   |


Install the above library with the following command.

```
mvn install:install-file -Dfile=<File name> -DgroupId=<Group ID> -DartifactId=<Artifact ID> -Dversion=<Version> -Dpackaging=jar
```
