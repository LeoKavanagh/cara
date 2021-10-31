enablePlugins(JavaAppPackaging)

libraryDependencies ++= Seq(
    "com.lihaoyi" %% "cask" % "0.7.7",
    "com.lihaoyi" %% "requests" % "0.6.5",
    "com.lihaoyi" %% "ujson" % "1.4.0"
)

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "io.github.leokavanagh",
      scalaVersion := "2.13.3"
    )),
    name := "cara"
  )
