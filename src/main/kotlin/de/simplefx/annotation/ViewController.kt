package de.simplefx.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ViewController(val view : String) {
}