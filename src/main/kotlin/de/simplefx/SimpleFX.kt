package de.simplefx

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.net.URL

final class SimpleFX : Application() {

	companion object {
		
		fun load(url: URL): Pane = SimpleFXLoader(url).load()

		fun loadAndPresent(url: URL, parent: Pane) {
			parent.getChildren().add(load(url))
		}

		fun loadAndPresent(url: URL, scene: Scene) {
			scene.setRoot(load(url))
		}
		
		fun loadAndPresent(url: URL, stage: Stage) {
			stage.setScene(Scene(load(url)))
		}

		fun launch(location: URL) {
			Application.launch(SimpleFX::class.java, location.toString())
		}

	}
	
	override fun start(primaryStage: Stage) {
		var location = getParameters().getRaw().get(0)
		SimpleFX.loadAndPresent(URL(location), primaryStage)
		primaryStage.show()
	}

}