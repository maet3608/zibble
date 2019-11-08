package quuux.zibble

import java.io.File
import util.matching.Regex
import ZParser._


/**
 * A backup project.
 * @param properties Map of properties (= name,value pairs) that describe project.
 * see .doc/index.html for details
 * @author Stefan Maetschke
 */
class ZProject(properties:Map[String,String]) extends ZLogger {
  val (inPatterns,exPatterns) = (values(INCLUDE).map(toRegex), values(EXCLUDE).map(toRegex))
  /** tests if the project is scheduled to be active */
  val isActive = properties(RUN) == "now"

  /** returns value of the property with the given name */
  def value(name:String) = properties(name).trim
  /** splits value of property with given name at separator and returns array of values */
  def values(name:String) = value(name).split(SEP).map(_.trim)
  
  /** tests if string is matched by at least on of the file patterns */
  def test(string:String, patterns:Seq[Regex]) = patterns.exists(_.findFirstIn(string) != None)
  /** tests if file name or dir is matched by includes or exclude patterns */
  def testInEx(nameOrDir:String) = test(nameOrDir,inPatterns) && !test(nameOrDir,exPatterns)
  /** tests if file name is matched by includes or exclude patterns */
  def testName(file:File) = testInEx(file.getName)
  /** tests if file dir is matched by includes or exclude patterns */
  def testDir(file:File) = testInEx(file.getParent.replace('\\','/'))

  /** replace wildcard symbols * and ? by their regex equivalents and disable '.' */
  def convert(pattern:String) =
    pattern.replace(".","\\.").replace('?','.').replace('@','?').replace("*",".*")
  /** convert pattern to regular expression depending on name or dir matching */
  def toRegex(pattern:String) =
    if (pattern.startsWith("/")) convert(pattern).r else ("^"+convert(pattern)+"$").r

  /** archives the file if it is matched by includes or exclude patterns */
  def process(file:File, archives:Seq[ZArchive]) =
    if (testName(file) && testDir(file))
      try { output(file.getCanonicalPath, toConsole=value(VERBOSE)=="1");
            archives.foreach(_.addFile(file)) }
      catch { case e:Exception => error(e.getMessage) }

  /** lists files in the given directory */
  def dir(path:File) = path.listFiles match {
    case null => Array[File]()
    case list => list
  }

  /** recursively walks file tree */
  def walker(path:File, archives:Seq[ZArchive]) {
    dir(path).foreach(f => if (f.isDirectory) walker(f,archives) else process(f,archives))
  }
  
  /** run the project */
  def run {
    val mode = if (properties(SIMULATE) == "on") "SIMULATING" else "ARCHIVING"
    output(mode+"  "+value(NAME)+"  ->  "+value(TO))
    val level = value(COMPRESS).toInt
    val simulate =  properties(SIMULATE) == "on"
    val archives = values(TO).map(path => new ZArchive(path, level, simulate))
    values(FROM).map(new File(_)).foreach(walker(_,archives))
    archives.foreach(_.close())
  }

  override def toString =
    properties.map{case (n,v) => "  "+n+" "+v}.mkString("PROJECT\n","\n","\n")
}

/**
 * Defines default values for properties.
 */
object ZProject {
  /** default values */
  val default =
    Map(NAME     -> "project",
        RUN      -> "now",
        SIMULATE -> "off",
        FROM     -> ".",
        TO       -> "backup.zip",
        INCLUDE  -> "*",
        EXCLUDE  -> "",
        COMPRESS -> "9",
        VERBOSE  -> "0"
    )

  /** Creates project from sequence of properties (= name,value pairs) */ 
  def apply(properties:Seq[(String,String)]) = new ZProject(default ++ properties)
}