import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info)
{
    // Managed dependencies that are used by the project file itself.
    // Putting them here allows them to be imported in the project class.

    val t_repo = "t_repo" at "http://tristanhunt.com:8081/content/groups/public"
    val posterous = "net.databinder" % "posterous-sbt" % "0.1.3"
}
