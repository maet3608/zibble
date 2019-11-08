package quuux.zibble.gui

//import au.com.quuux.utils.JErrorHandler

import javax.swing.JOptionPane.{showMessageDialog, ERROR_MESSAGE}
import javax.swing.UIManager.{setLookAndFeel, getSystemLookAndFeelClassName}
import javax.swing.JFrame.{EXIT_ON_CLOSE}
import java.awt.Toolkit.getDefaultToolkit
import javax.swing.{JOptionPane, JFrame}
import java.awt.event.{WindowAdapter, WindowEvent}
import java.awt.{EventQueue, Dimension}
import java.io.{FileWriter, File}


/**
 * Graphical user interface for quuux.zibble.
 * See ./doc/index.html for usage.
 * @author Stefan Maetschke
 */

class Zibble extends JFrame {
//  Thread.setDefaultUncaughtExceptionHandler(new JErrorHandler())
  val VERSION = "0.1"
	  
}


object Zibble {
  def main(args:Array[String]) {
    setLookAndFeel(getSystemLookAndFeelClassName)
    new Zibble
  }

}