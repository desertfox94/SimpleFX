package de.simplefx

import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

class Reflector {

	fun initField(obj: Any, field: Field): Any {
		field.setAccessible(true)
		var instance = field.getType().newInstance()
		field.set(obj, instance)
		return instance
	}

	fun fieldsByAnnotation(obj: Any, annotation: Class<out Annotation>): List<Field> {
		return fieldsByAnnotation(obj.javaClass, annotation)
	}
	
	fun fieldsByAnnotation(clazz: Class<Any>, annotation: Class<out Annotation>): List<Field> {
		return declaredFields(clazz).filter { it.isAnnotationPresent(annotation) }
	}

	fun <T : Annotation> getAnnotatedFields(clazz: Class<Any>, annotation: Class<T>): List<AnnotatedField<T>> {
		return declaredFields(clazz).filter { it.isAnnotationPresent(annotation) }.map { AnnotatedField(it, it.getAnnotation(annotation)) }
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

	fun declaredFields(obj: Any): Array<out Field> {
		return declaredFields(obj.javaClass)
	}

	fun declaredFields(clazz: Class<Any>): Array<out Field> {
		return clazz.getDeclaredFields() ?: emptyArray()
	}
	
	fun genericTypeOf(field: Field): Class<Any> {
		return (field.getGenericType() as ParameterizedType).getActualTypeArguments()[0] as Class<Any>
	}
	
	fun fieldByName(obj: Any, name: String): Field? {
		return declaredFields(obj).first({ it.getName().equals(name)})
	}

}