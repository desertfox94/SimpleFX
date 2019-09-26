package de.simplefx.exceptions

class ControllerMemberNotFound(controllerMember : String, modelMember : String) : RuntimeException("The controller member '$controllerMember' specified by the model member '$modelMember' was not found") {
}