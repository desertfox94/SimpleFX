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
		bindAll(model, controller)
	}

	controllerClass.annotations.filter { it is StyleSheets }
		.forEach({ controller.styleSheets.addAll((it as StyleSheets).path) })
	return controller
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

	var controllerProperty: Property<Any>;

	if (controllerField is TextField) {
		controllerProperty = controllerField.textProperty() as Property<Any>
	} else {
		throw RuntimeException("Field type not supported")
	}

	if (biDirectional) {
		controllerProperty.bindBidirectional(modelField)
	} else {
		controllerProperty.bind(modelField)
	}
}

private fun localizeView(view: String, controllerClass: Class<*>): URL {
	return controllerClass.getResource(view)
}