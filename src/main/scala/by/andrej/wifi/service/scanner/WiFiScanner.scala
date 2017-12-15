package by.andrej.wifi.service.scanner

import java.io.File

import by.andrej.wifi.entity.WiFi

import scala.collection.mutable._
import scala.io.Source
import scala.sys.process._

object WiFiScanner {
  var wifi = MutableList[WiFi]()

  def scan(): Seq[WiFi] = {
    "nmcli device wifi rescan" !
    val pb: ProcessBuilder = Process("nmcli -f ssid,bssid,signal,in-use,security dev wifi")
    val logFile = new File("log.txt")
    logFile.delete
    val logger = ProcessLogger(logFile)
    val res = pb.run(logger).exitValue
    logger.close
    if (res != 0) null
    else {
      wifi.clear
      val strings = Source.fromFile("log.txt").getLines
      val firstLine = strings.next
      val bssid = firstLine.indexOf("BSSID")
      val signal = firstLine.indexOf("SIGNAL")
      val active = firstLine.indexOf('*')
      val security = firstLine.indexOf("SECURITY")
      for (s <- strings) {
        val cur = new WiFi(s.substring(0, bssid).trim, s.substring(bssid, signal).trim, s.substring(security).trim)
        cur.quality = s.substring(signal, active).trim.toInt
        cur.active = if (s(active) == '*') true else false
        wifi += cur
      }
      wifi
    }
  }
}