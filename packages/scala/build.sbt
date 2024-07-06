lazy val grokkingFP = (project in file("."))
  .settings(
    name := "grokkingfp",
    scalaVersion := "3.3.3"
  )

libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.4"
libraryDependencies += "co.fs2" %% "fs2-core" % "3.10.2"
