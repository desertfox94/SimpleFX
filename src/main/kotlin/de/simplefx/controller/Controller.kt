package de.simplefx

import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.layout.Pane

open class Controller<V : Pane> {

	lateinit var view: V 

	val styleSheets: MutableList<String> = ArrayList()

	@FXML
	protected open fun initialize() {
	}

	protected fun onShow() {}

	protected fun getScene() : Scene = view.getScene()
	
}