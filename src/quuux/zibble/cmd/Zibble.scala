package quuux.zibble.cmd

import quuux.zibble._
import java.util.Date

/**
 * Command line interface for quuux.zibble.
 * See ./doc/index.html for usage.
 * @author Stefan Maetschke
 */
object Zibble extends ZParser with ZLogger {
	
  /** prints parsing errors */
  def parseError(path:String, msg:String,next:Input) {
     error("%s\n %s:\n%s at line %d column %d ".format(path,msg,next.pos.longString,next.pos.line,next.pos.column))
  }

  /** args is empty or path of a project file */
  def main(args: Array[String]) {
    ZLogger.open()
    try {
      val path = if(args.length>0) args(0) else "default.zpj"
      output("ZIBBLE "+path)
      output("DATE "+(new Date))
      read(path) match {
        case Success(result, next) => result.filter(_.isActive).foreach(_.run)
        case Failure(msg, next) => parseError(path,msg,next)
        case Error(msg, next) =>  parseError(path,msg,next)
      }
      output("FINISHED.")
    }
    catch { case e:Exception => error(e.getMessage) }
    ZLogger.close()
  }
}

