name := "FollowersMazeCodingChallenge"

version := "0.1"

scalaVersion := "2.13.8"
libraryDependencies ++= {

  val scalaTestVersion = "3.0.8"
  val mockitoVersion = "3.0.0"

  Seq(

    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "org.mockito" % "mockito-core" % mockitoVersion % "test"
  )
}