enablePlugins(JavaServerAppPackaging)

name := "rest-api-scala-akka-poc"

//version := "0.1"

organization := "com.bbva.mike"

scalaVersion := "2.11.7"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Spray Repository"    at "http://repo.spray.io",
  "Maven Central" at "http://repo1.maven.org/maven2",
  "Nexus Repository Manager" at "http://ec2-52-48-62-134.eu-west-1.compute.amazonaws.com:8081/nexus/")

libraryDependencies ++= {
  val akkaVersion       = "2.3.9"
  val sprayVersion      = "1.3.2"
  Seq(
    "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
    "io.spray"          %% "spray-can"       % sprayVersion,
    "io.spray"          %% "spray-routing"   % sprayVersion,
    "io.spray"          %% "spray-json"      % "1.3.1",
    "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
    "ch.qos.logback"    %  "logback-classic" % "1.1.2",
    "com.typesafe.akka" %% "akka-testkit"    % akkaVersion  % "test",
    "io.spray"          %% "spray-testkit"   % sprayVersion % "test",
    "org.specs2"        %% "specs2"          % "2.3.13"     % "test"
  )
}

assemblyMergeStrategy in assembly := {
  case m if m.toLowerCase.endsWith(".idea")          => MergeStrategy.discard
  case m if m.toLowerCase.endsWith("manifest.mf")          => MergeStrategy.discard
  case m if m.toLowerCase.matches("meta-inf.*")      => MergeStrategy.discard
  case m if m.toLowerCase.startsWith("meta-inf/services/") => MergeStrategy.filterDistinctLines
  case "reference.conf"                                    => MergeStrategy.concat
  case _                                                   => MergeStrategy.first
}

artifact in (Compile, assembly) := {
  val art = (artifact in (Compile, assembly)).value
  art.copy(`classifier` = Some("assembly"))
}

addArtifact(artifact in (Compile, assembly), assembly)

publishMavenStyle := true

publishTo := {
  val nexus = "http://ec2-52-48-62-134.eu-west-1.compute.amazonaws.com:8081/nexus/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "content/repositories/releases")
}

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

// Assembly settings
//mainClass in Global := Some("com.bbva.mike.mikeapi.Main")

jarName in assembly := "mike-ingestion-api.jar"