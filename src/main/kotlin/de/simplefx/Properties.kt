package de.simplefx

import de.simplefx.exceptions.ControlBindingNotSupported
import javafx.beans.property.Property
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBoxBase
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TextInputControl
import javafx.scene.control.ToggleButton
import javafx.scene.image.ImageView


class Properties{
	
	companion object {
		fun of(control: Any): Property<Any> {
		if (control is CheckBox) {
			return control.selectedProperty() as Property<Any>
		} else if (control is Label) {
			return control.textProperty() as Property<Any>
		} else if (control is ComboBoxBase<*>) {
			return control.valueProperty() as Property<Any>
		} else if (control is ImageView) {
			return control.imageProperty() as Property<Any>
		} else if (control is ListView<*>) {
			return control.itemsProperty() as Property<Any>
		} else if (control is TextInputControl) {
			return control.textProperty() as Property<Any>
		} else if (control is ToggleButton) {
			return control.selectedProperty() as Property<Any>
		} else {
			throw ControlBindingNotSupported(control::class.java.getSimpleName())
		}
	}
	}
	
}