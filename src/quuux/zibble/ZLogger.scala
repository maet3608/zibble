package quuux.zibble

import java.io.{BufferedWriter, FileWriter}

/**
 * A simple trait for logging that writes log messages to the console and a
 * logging file.
 * @author Stefan Maetschke
 */

trait ZLogger {
  def output(msg:String, toConsole:Boolean=true, toFile:Boolean=true) {
    ZLogger.output(msg, toConsole, toFile)
  }
  def error(msg:String) { output("ERROR: "+msg) }
  def warning(msg:String) { output("WARNING: "+msg) }
}


object ZLogger {
  private var logFile:BufferedWriter = null

  def open() {
    logFile = new BufferedWriter(new FileWriter("quuux.zibble.log"))
  }

  def output(msg:String, toConsole:Boolean=true, toFile:Boolean=true) {
    if(toFile) { logFile.write(msg+"\n"); logFile.flush() }
    if(toConsole) println(msg)
  }

  def close() {
    if(logFile!=null) logFile.close()
  }
}