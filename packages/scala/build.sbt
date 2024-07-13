lazy val grokkingFP = (project in file("."))
  .settings(
    name := "grokkingfp",
    scalaVersion := "3.3.3"
  )

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.5.4",
  "co.fs2" %% "fs2-core" % "3.10.2",
  "org.apache.jena" % "apache-jena-libs" % "5.0.0",
  "org.apache.jena" % "jena-fuseki-main" % "5.0.0"
)
