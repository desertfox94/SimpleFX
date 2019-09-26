package de.simplefx

import java.lang.reflect.Field

class Reflector {

	fun initField(obj: Any, field : Field) : Any {
		field.setAccessible(true)
		var instance = field.getType().newInstance()
		field.set(obj, instance)
		return instance
	}
	
	fun fieldsByAnnotation(obj: Any, annotation: Class<out Annotation>) : List<Field> {
		return declaredFields(obj).filter { it.isAnnotationPresent(annotation) }
	}

	fun <T : Annotation> firstAnnotation(clazz: Class<*>, type: Class<T>): T {
		return getAnnotations(clazz, type).first() as T
	}

	fun <T : Annotation> getAnnotations(clazz: Class<*>, type: Class<T>): List<T> {
		return clazz.annotations.filter { it.equals(type) } as List<T>
	}
	
	fun callByAnnotation(model: Any, annotation: Class<out Annotation>) {
		var method = model.javaClass.getMethods().first { method -> method.isAnnotationPresent(annotation) }
		if (method != null) {
			method.invoke(model)
		}
	}
	
	fun declaredFields(obj : Any): Array<out Field> {
		return obj.javaClass.getDeclaredFields() ?: emptyArray()
	}
	
}