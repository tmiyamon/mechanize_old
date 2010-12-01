import sbt._

class conf(info: ProjectInfo) extends DefaultProject(info)
{
      val httpunit = "httpunit" % "httpunit" % "1.7"
      val rhino = "rhino" % "js" % "1.7R2"
      val jericho = "net.htmlparser.jericho" % "jericho-html" % "3.1"
}
