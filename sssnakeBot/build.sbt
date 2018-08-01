name := "sssnakeBot"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.6"

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % "10.1.3",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.1.3" % Test
)

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.5.14",
    "com.typesafe.akka" %% "akka-testkit" % "2.5.14" % Test
)

libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-stream" % "2.5.14",
    "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.14" % Test
)

libraryDependencies ++= Seq("org.yaml" % "snakeyaml" % "1.19")