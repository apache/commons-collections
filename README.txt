Jakarta Commons Collections
===========================

Welcome to the Collections component of the Jakarta Commons
project.


***********************************************************

   This is a patch release of Commons Collections 2.1

 This release has been created due to the discovery of
 binary incompatibility between 2.1 and 3.0  in the
 IteratorUtils class (sorry!).
 
         See the release notes for more detail.

***********************************************************

This component requires the excellent Ant utility.  It can 
be found here :

  http://jakarta.apache.org/ant/

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


