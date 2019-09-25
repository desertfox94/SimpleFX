package de.simplefx

import javafx.fxml.FXML
import javafx.scene.layout.Pane

open class Controller<V : Pane, M : Any> {

	lateinit var view: V

	lateinit var model: M

	val styleSheets: MutableList<String> = ArrayList()

	@FXML
	protected open fun initialize() {
	}

	protected fun onShow() {}

}