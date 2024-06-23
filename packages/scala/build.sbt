lazy val grokkingFP = (project in file("."))
  .settings(
    name := "grokkingfp",
    scalaVersion := "3.3.3"
  )

libraryDependencies += "org.typelevel" %% "cats-effect" % "2.5.3"
