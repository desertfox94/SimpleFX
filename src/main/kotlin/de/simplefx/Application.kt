package de.simplefx

import javafx.scene.Scene
import javafx.scene.layout.Pane
import javafx.stage.Stage

private lateinit var appController : Class<Controller<in Pane, Any>>

private var initialized = false;

open class SimpleApplication() : javafx.application.Application() {
	
	override fun start(primaryStage: Stage) {
		var controller = load(appController)
		primaryStage.setScene(Scene(controller.view as Pane))
		primaryStage.show()
	}
	
}

fun launch(controller : Class<*>) {
	if (initialized) {
		throw RuntimeException("SimpleFX Apploication already running!")
	}
	initialized = true;
	appController = controller as Class<Controller<in Pane, Any>>;
	javafx.application.Application.launch(SimpleApplication::class.java)
}