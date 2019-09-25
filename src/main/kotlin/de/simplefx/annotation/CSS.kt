package de.simplefx.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class StyleSheets(vararg val path : String) {
}