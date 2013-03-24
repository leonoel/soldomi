import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "soldomi"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    javaCore,
    javaJdbc,
    javaEbean,
    "org.apache.commons" % "commons-math3" % "3.1.1"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
  )

}
