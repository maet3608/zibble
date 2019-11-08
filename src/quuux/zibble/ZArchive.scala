package quuux.zibble

import java.util.zip.{ZipEntry, ZipOutputStream}
import java.io._

/**
 * A simple ZIP archive. Only zip but no unzip supported.
 * Infos: http://blogs.sun.com/CoreJavaTechTips/entry/creating_zip_and_jar_files
 * @param filename filepath of the zip file to be generated.
 * @param level compression level [0-9].
 * @param simulate true: nothing happens, false: archiving is active
 * @author Stefan Maetschke
 */
class ZArchive(filename:String, level:Int=9, simulate:Boolean=false) {
  val buf = new Array[Byte](4068)
  val drive = """^([A-Za-z]:)?[/\\]""".r
  val zos = if(!simulate) new ZipOutputStream(new FileOutputStream(filename)) else null

  if(!simulate) {
    zos.setLevel(level)                         // compression level
    zos.setMethod(ZipOutputStream.DEFLATED)     // this is default but just to be sure
  }

  /** adds a file to the archive (overwrites existing files). Stores file path if withPath==true */
  def addFile(file:File, withPath:Boolean=true) {
    if(simulate) return
    val filepath = if(withPath) file.getCanonicalPath else file.getName
    val fis = new FileInputStream(file)
    zos.putNextEntry(new ZipEntry(drive.replaceFirstIn(filepath,"")))
    var n = 0
    while(n != -1) {
      n = fis.read(buf)
      if(n > 0) zos.write(buf,0,n)
    }
    fis.close()
    zos.closeEntry()
    zos.flush()
  }

  /** closes the archive */
  def close() { if(!simulate) zos.close() }
}

