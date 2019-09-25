package de.simplefx

import de.simplefx.annotation.Bind
import de.simplefx.annotation.StyleSheets
import de.simplefx.annotation.ViewController
import javafx.beans.property.Property
import javafx.fxml.FXMLLoader
import javafx.scene.control.TextField
import javafx.scene.layout.Pane
import java.net.URL
import java.util.function.Predicate
import javafx.scene.control.CheckBox
import de.simplefx.exceptions.ControlBindingNotSupported
import javafx.scene.control.Label
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ColorPicker
import javafx.scene.control.DatePicker
import javafx.scene.control.ComboBoxBase
import javafx.scene.web.HTMLEditor
import javafx.scene.image.ImageView
import javafx.scene.control.ListView
import javafx.scene.control.PasswordField
import javafx.scene.control.TextInputControl
import javafx.scene.control.ToggleButton
import javafx.beans.binding.Bindings
import javafx.beans.binding.Binding
import com.sun.javafx.binding.BidirectionalBinding
import javax.annotation.PostConstruct
import java.lang.reflect.Method

val VIEW = "view"

fun <T : Controller<in Pane, Any>> load(controllerClass: Class<T>): T {
	var viewController = controllerClass.annotations.first { it is ViewController } as ViewController
	var location = localizeView(viewController.view, controllerClass)
	var loader = FXMLLoader(location)
	var view: Pane = loader.load()
	var controller: T = loader.getController();
	controller.view = view

	if (viewController.model.isNotEmpty()) {
		var model = loadModel(viewController.model);
		controller.model = model
		bindAll(model, controller)
//		callPostConstruct(model)
	}

	controllerClass.annotations.filter { it is StyleSheets }
		.forEach({ controller.styleSheets.addAll((it as StyleSheets).path) })
	return controller
}

private fun callPostConstruct(model: Any) {
	callByAnnotation(model, PostConstruct::class.java)
}

private fun callByAnnotation(model: Any, annotation: Class<out Annotation>) {
	var method = model.javaClass.getMethods().first { method -> method.isAnnotationPresent(annotation) }
	if (method != null) {
		method.invoke(model)
	}
}


private fun <T : Annotation> firstAnnotation(clazz: Class<*>, type: Class<T>): T {
	return getAnnotations(clazz, type).first() as T
}

private fun <T : Annotation> getAnnotations(clazz: Class<*>, type: Class<T>): List<T> {
	return clazz.annotations.filter { it.equals(type) } as List<T>
}

fun loadModel(model: String): Any {
	return Class.forName(model).newInstance();
}

fun addValidation(modelField: Property<String>, p: Predicate<String>) {
	modelField.addListener({ prop, o, n ->
		if (!n.equals(o) && !p.test(n)) {

			modelField.setValue(o)
		}
	})
}

fun bindAll(model: Any, controller: Controller<Pane, Any>) {
	var fields = model.javaClass.getDeclaredFields()
	fields.forEach { field ->
		field.setAccessible(true)
		if (field.isAnnotationPresent(Bind::class.java)) {
			var binding = field.getAnnotation(Bind::class.java)
			var controllerFieldName: String;
			if (binding.field.isNotEmpty()) {
				controllerFieldName = binding.field
			} else {
				controllerFieldName = field.getName()
			}
			bind(
				field.get(model) as Property<Any>,
				controller::class.java.getField(controllerFieldName).get(controller),
				binding.biDirectional
			)
		}
	}

}

fun bind(modelField: Property<Any>, controllerField: Any, biDirectional: Boolean) {
	var controllerProperty = propertyOf(controllerField);
	if (biDirectional) {
		BidirectionalBinding.bind(controllerProperty, modelField);
	} else {
		controllerProperty.bind(modelField)
	}
}

fun propertyOf(control: Any): Property<Any> {
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

private fun localizeView(view: String, controllerClass: Class<*>): URL {
	return controllerClass.getResource(view)
}