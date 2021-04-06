val spinalVersion = "1.4.3"

lazy val root = (project in file("."))
  .settings(
    inThisBuild(List(
      organization := "tuppi-ovh",
      scalaVersion := "2.11.12",
      version      := "0.1"
    )),
    name := "example-sparkfun-alchitry-cu",
    libraryDependencies ++= Seq(
      "com.github.spinalhdl" % "spinalhdl-core_2.11" % spinalVersion,
      "com.github.spinalhdl" % "spinalhdl-lib_2.11" % spinalVersion,
      compilerPlugin("com.github.spinalhdl" % "spinalhdl-idsl-plugin_2.11" % spinalVersion)
    )
  )

fork := true
EclipseKeys.withSource := true
