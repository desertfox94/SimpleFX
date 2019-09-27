package de.simplefx

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import java.net.URL

open class SimpleFX : Application() {

	private var customStartup = true;

	companion object {

		private var customStartUp: ((Stage, Pane, Any) -> Unit)? = null

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

		fun launch(location: URL, appClass: Class<out Application> = SimpleFX::class.java) {
			Application.launch(appClass, location.toString())
		}

	}

	override fun start(stage: Stage) {
		var location = getParameters().getRaw().get(0)
		var loader = SimpleFXLoader(URL(location))
		var view = loader.load();
		if (!internalCustomStartup(stage, view, loader.controller)) {
			stage.setScene(Scene(view))
		}
		stage.show()
	}

	private fun internalCustomStartup(stage: Stage, view: Pane, controller: Any): Boolean {
		customStartup(stage, view, controller)
		return customStartup
	}

	protected open fun customStartup(stage: Stage, view: Pane, controller: Any) {
		customStartup = false;
	}

}