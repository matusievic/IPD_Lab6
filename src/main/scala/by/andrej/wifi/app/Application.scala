package by.andrej.wifi.app

import by.andrej.wifi.entity.WiFi
import by.andrej.wifi.service.scanner.WiFiScanner
import by.andrej.wifi.service.updater.Updater

import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, GridPane}

object Application extends JFXApp {
  var wifiList = new ListView[WiFi] {
    onMouseClicked = { _ => {
      val item = this.selectionModel().getSelectedItem
      if (item != null) {
        nameLabel.text = item.name
        macLabel.text = item.mac
        authLabel.text = item.auth
        qualityLabel.text = item.quality + " %"
        connectionLabel.text = item.active.toString
      }
    }
    }
  }
  val nameLabel = new Label
  val macLabel = new Label
  val authLabel = new Label
  val qualityLabel = new Label
  val connectionLabel = new Label
  val updater = new Updater

  stage = new PrimaryStage {
    title = "Wi-Fi Manager"
    width = 700
    height = 450
    scene = new Scene {
      val rootPane = new BorderPane

      //for connecting
      val passwordField = new TextField
      passwordField.text = "password"
      val connectButton: Button = new Button("Connect") {
        onMouseClicked = _ => wifiList.selectionModel.value.getSelectedItems.get(0).connect(passwordField.text.value)
      }

      //set up data pane
      val dataPane = new GridPane {
        addRow(0, new Label("Name:"), nameLabel)
        addRow(1, new Label("Mac:"), macLabel)
        addRow(2, new Label("Authentication type:"), authLabel)
        addRow(3, new Label("Quality:"), qualityLabel)
        addRow(4, new Label("Connected:"), connectionLabel)
        addRow(5, passwordField, connectButton)
        hgap = 50
        vgap = 20
        margin = Insets(20, 20, 20, 20)
      }
      rootPane.left = wifiList
      rootPane.center = dataPane
      root = rootPane

      onCloseRequest = _ => updater.done = true
    }
  }

  def update() = {
    Platform.runLater {
      new Runnable {
        override def run(): Unit = {
          val cur = wifiList.selectionModel.value.getSelectedIndices.get(0)
          wifiList.items = ObservableBuffer(WiFiScanner.scan())
          wifiList.selectionModel.value.selectIndices(cur)
        }
      }
    }
  }

  updater.start()
}