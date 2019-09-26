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
import de.simplefx.annotation.Model
import java.lang.reflect.Field
import de.simplefx.annotation.Items
import de.simplefx.exceptions.ControllerMemberNotFound

val VIEW = "view"

class SimpleFXLoader<T : Controller<in Pane>>(val controllerClass: Class<T>) {

	val reflector = Reflector()

	lateinit var controller: T

	companion object {
		fun <T : Controller<in Pane>> load(controllerClass: Class<T>): T = SimpleFXLoader(controllerClass).load()
	}

	fun load(): T {
		instantiateViaFXML()
		initializeModels()
		setStyleSheeets()
		return controller
	}

	private fun instantiateViaFXML() {
		var controllerAnnotaion = controllerClass.annotations.first { it is ViewController } as ViewController
		var location = localizeView(controllerAnnotaion.view, controllerClass)
		var loader = FXMLLoader(location)
		var view: Pane = loader.load()
		controller = loader.getController();
		controller.view = view
	}

	private fun setStyleSheeets() {
		var stylesheets = reflector.getAnnotations(controllerClass, StyleSheets::class.java);
		stylesheets.forEach({ controller.styleSheets.addAll(it.path) })
	}

	private fun initializeModels() {
		reflector.fieldsByAnnotation(controller, Model::class.java)
			.forEach({ initializeModel(it) })
	}

	private fun initializeModel(modelField: Field) {
		var model = reflector.initField(controller, modelField)
		bindAll(model)
		fillWithItems(model)
		callPostConstruct(model)
	}

	private fun callPostConstruct(model: Any) {
		reflector.callByAnnotation(model, PostConstruct::class.java)
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

	fun bindAll(model: Any) {
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
		var controllerProperty = Properties.of(controllerField);
		if (biDirectional) {
			BidirectionalBinding.bind(controllerProperty, modelField);
		} else {
			controllerProperty.bind(modelField)
		}
	}

	fun fillWithItems(model: Any) {
		reflector.fieldsByAnnotation(model, Items::class.java).forEach { fillWithItems(model, it) }
	}

	fun fillWithItems(model: Any, itemsField: Field) {
		var itemsAnnotation = itemsField.getAnnotation(Items::class.java)
		var name = if (itemsAnnotation.field.isNotEmpty()) itemsAnnotation.field else itemsField.getName()
		var controlWithItemsField = reflector.fieldByName(controller, name)
		if (controlWithItemsField == null) {
			throw ControllerMemberNotFound(name, itemsField.getName())
		}
		var itemsProperty = Properties.itemsOf(controlWithItemsField.get(controller))
		controlWithItemsField.setAccessible(true)
		itemsField.setAccessible(true)
		itemsProperty.setValue(itemsField.get(model))
	}


	private fun localizeView(view: String, controllerClass: Class<*>): URL {
		return controllerClass.getResource(view)
	}
}

