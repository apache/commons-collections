Jakarta Commons Collections
===========================

Welcome to the Collections component of the Jakarta Commons project.
This component contains many new collections and collection utilities.

Two jar files are produced by this component.
The first, commons-collections.jar is the main jar used by applications.
The second, commons-collections-testframework.jar is an extension to junit
for testing new collection implementations and is not normally used by applications.


Building from source
--------------------
This component requires the excellent Ant utility.
It can be found here :

  http://ant.apache.org/

For testing the project, you will also need JUnit :

  http://www.junit.org/

To let the test process find JUnit, you may make a 
copy of the build.properties.sample file, rename to
build.properties,  and modify to reflect
the location of the junit.jar on your computer.


Once you have Ant propertly installed, and the
build.properties file correctly reflects the location
of your junit.jar, you are ready to build and test.

To compile and test the component :

$ ant test

To build a jar :

$ ant dist-jar

To build the API documentation :

$ ant doc

To build the jar and API doc at once :

$ ant dist


Maven
-----
The component can also be built using Maven.
http://maven.apache.org
