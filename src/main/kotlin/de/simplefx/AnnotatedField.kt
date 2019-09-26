package de.simplefx

import java.lang.reflect.Field

data class  AnnotatedField<T : Annotation>(val field : Field, val annotation : T)