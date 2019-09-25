package de.simplefx.exceptions

class ControlBindingNotSupported(val control : String) : RuntimeException("Control of type $control is not supported. Please check the @Bind-Annotation and the control type") {
}