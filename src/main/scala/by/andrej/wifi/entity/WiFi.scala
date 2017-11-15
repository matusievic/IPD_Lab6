package by.andrej.wifi.entity

import java.io.File

import scala.sys.process.{Process, ProcessBuilder, ProcessLogger}

class WiFi(val name: String, val mac: String, val auth: String) {
  var quality = 0
  var active = false

  def connect(password: String): Boolean = {
    val pb: ProcessBuilder = Process("nmcli dev wifi connect " + name + " password " + password)
    val logFile = new File("log.txt")
    logFile.delete
    val logger = ProcessLogger(logFile)
    val res = pb.run(logger).exitValue
    logger.close
    res == 0
  }
  override def toString: String = name
}