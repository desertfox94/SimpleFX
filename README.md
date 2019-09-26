# SimpleFX
A basic framework to simplify the use of JavaFX and prevents boilerplate code for common usecases.

## Some examples
### Binding a model property to a JavaFX control

```kotlin
class AppController : Controller<Pane>() {
	
	@Model
	lateinit var model : AppModel

	@FXML
	lateinit var description: TextField
	
	@FXML
	lateinit var datePicker: DatePicker
	
}

class AppModel {

	@Bind
	val description: StringProperty = SimpleStringProperty()
	
	@Bind("datePicker")
	val date: ObjectProperty<LocalDate> = SimpleObjectProperty<LocalDate>()
	
}


```
Introduce the model with the @Model annotation to the Controller.
With the @Bind annotation binds the Property of the model to a field of the controller. By default the property ist bound to a field with the same name (e.g. 'description').
Its also possible to reference the control with a paramater in the @Bind Annotation (see date/datePicker)



### Fill a table view

The FXML
```XML
<TableView fx:id="tableView">
	<columns>
		<TableColumn fx:id="text"/>
		<TableColumn fx:id="random"/>
	</columns>
</TableView>
```

Controller, Model and TableObject
```kotlin
class AppController : Controller<Pane>() {
	
	@FXML
	lateinit var tableView: TableView<TableObject>
	
	@FXML
	lateinit var text: TableColumn<TableObject, String>
	
	@Model
	lateinit var model : AppModel
	
}
```
```kotlin
class AppModel {

	@Items("tableView")
	val tableContent: ObservableList<TableObject> = FXCollections.observableArrayList(...)
	
}
```
```kotlin
class TableObject {
	
	@CellValue("text") val value = SimpleStringProperty()
	
	@CellValue val random = SimpleIntegerProperty()
	
}
```

As you can see the table has two coulmns: 'text' and 'random'.
The AppController holds the references to the the tableView and model. Note the column 'random' is not referenced.
The AppModel uses the @Items Annotation to put items of 'tableContent' into the tableView.
In the TableObject you can see the @ColumnValue Annotation which is used to bind the Property to the TableCell.
As mentioned the 'random' column is not referenced, but the binding will work anyway.