package quuux.zibble

import java.io.File
import scala.io.Source
import scala.collection.JavaConversions.{mapAsScalaMap}
import javax.swing.filechooser.FileSystemView
import scala.util.parsing.combinator._
import ZParser._

/**
 * Parses a project file.
 * see ./doc/index.html for details and ./projects for examples.
 * @author Stefan Maetschke
 */
class ZParser extends JavaTokenParsers {
  /** variable definitions */
  var varMap = Map[String,String]()++driveVariables++envVariables
  /** regex for line comments */
  val comment = """#.*$""".r

  /** returns list of logical drive names and the drive letter as tuples ({name},letter) */
  def driveVariables = {
    val drive = """(.+) \((\w:)\)$""".r
    val display = FileSystemView.getFileSystemView.getSystemDisplayName _
    File.listRoots.map(display).collect{case drive(name,letters) => ("{"+name+"}",letters)}
  }

  /** returns list of environment variables as tuples ({name},value) */
  def envVariables = mapAsScalaMap(System.getenv).map{ case (name,value) => ("{"+name+"}",value)}

  /** replaces variable names in value by variable values */
  def instantiate(value:String) = varMap.foldLeft(value){case (v,(n,l)) => v.replace(n,l)}.trim

  /** Reads file content but removes comment sections before parsing */
  def read(path:String) =
    parseAll(projects, Source.fromFile(path).getLines().map(comment.replaceFirstIn(_,"\n")).mkString("\n"))

  // grammar of a projects file
  def projects = rep(variable)~>rep(project) ^^ (_.map(p => ZProject(p)))
  def project = "PROJECT"~>rep(property)
  def property  = (NAME~"\\w+".r |
                   RUN~"(now)|(never)".r |
                   SIMULATE~"(off)|(on)".r |
                   FROM~value |
                   TO~value |
                   INCLUDE~value |
                   EXCLUDE~value |
                   COMPRESS~"[0-9]".r |
                   VERBOSE~"[01]".r) ^^ {case n~v => (n,v)}
  def varname  = """\{[^\}]+\}""".r
  def variable = "VAR"~>varname~value ^^ {case n~v => varMap += n->v}
  def value = """[^\n]+""".r  ^^ instantiate
}

/**
 * Defines constants for parser.
 */
object ZParser {
  // property names
  val NAME      = "NAME"
  val RUN       = "RUN"
  val FROM      = "FROM"
  val TO        = "TO"
  val INCLUDE   = "INCLUDE"
  val EXCLUDE   = "EXCLUDE"
  val SIMULATE  = "SIMULATE"
  val COMPRESS  = "COMPRESS"
  val VERBOSE   = "VERBOSE"

  // separator for values of a property
  val SEP = ','
}