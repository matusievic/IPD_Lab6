package by.andrej.wifi.service.updater

import by.andrej.wifi.app.Application

class Updater extends Thread {
  var done = false

  override def run(): Unit = {
    while (!done) {
      Thread.sleep(3000)
      if (!done) Application.update()
    }
  }
}