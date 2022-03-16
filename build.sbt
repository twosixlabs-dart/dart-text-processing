import Dependencies._
import sbt._

organization in ThisBuild := "com.twosixlabs.dart"
name := "dart-text-processing"

resolvers in ThisBuild ++= Seq( "Maven Central" at "https://repo1.maven.org/maven2/",
                                "JCenter" at "https://jcenter.bintray.com",
                                "Local Ivy Repository" at s"file://${System.getProperty( "user.home" )}/.ivy2/local/default" )

crossScalaVersions in ThisBuild := Seq( "2.11.12", "2.12.7" )

publishMavenStyle := true

lazy val root = ( project in file( "." ) ).aggregate( embeddedTextUtils ) // spark utils isn't really used anymore, saving the code but don't build the project

lazy val embeddedTextUtils = ( project in file( "embedded-text-utils" ) ).settings( libraryDependencies ++= dartCommons
                                                                                                            ++ betterFiles
                                                                                                            ++ logging
                                                                                                            ++ scalaTest,
                                                                                    excludeDependencies ++= Seq( ExclusionRule( "org.slf4j", "slf4j-log4j12" ),
                                                                                                                 ExclusionRule( "org.slf4j", "log4j-over-slf4j" ),
                                                                                                                 ExclusionRule( "log4j", "log4j" ),
                                                                                                                 ExclusionRule( "org.apache.logging.log4j", "log4j-core" ) ) )

// TODO @michael - need to upgrade spark to 3.x to support java 11, this stuff is currently not used so removing it from the build until i have time to fix it
lazy val sparkTextUtils = ( project in file( "spark-text-utils" ) ).settings( libraryDependencies ++= sparkNlp
                                                                                                      ++ spark
                                                                                                      ++ dartCommons
                                                                                                      ++ sparkFastTests
                                                                                                      ++ scalaTest
                                                                                                      ++ betterFiles
                                                                                                      ++ logging,
                                                                              dependencyOverrides ++= Seq( "com.google.guava" % "guava" % "15.0" ) )

test in publish := {}

javacOptions in ThisBuild ++= Seq( "-source", "8", "-target", "8" )
scalacOptions in ThisBuild += "-target:jvm-1.8"

sonatypeProfileName := "com.twosixlabs"
inThisBuild(List(
    organization := organization.value,
    homepage := Some(url("https://github.com/twosixlabs-dart/dart-text-processing")),
    licenses := List("GNU-Affero-3.0" -> url("https://www.gnu.org/licenses/agpl-3.0.en.html")),
    developers := List(
        Developer(
            "twosixlabs-dart",
            "Two Six Technologies",
            "",
            url("https://github.com/twosixlabs-dart")
            )
        )
    ))

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / sonatypeRepository := "https://s01.oss.sonatype.org/service/local"