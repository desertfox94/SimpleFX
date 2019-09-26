# SimpleFX
A basic framework to simplify the use of JavaFX and prevents boilerplate code for common usecases.

### Some examples
Binding a model property to a JavaFX control

```kotlin
class AppController : Controller<GridPane>() {

        @Model
	lateinit var model : AppModel

	@FXML
	lateinit var description: TextField
}

class AppModel {

	@Bind
	val description: StringProperty = SimpleStringProperty("Test")
}


```