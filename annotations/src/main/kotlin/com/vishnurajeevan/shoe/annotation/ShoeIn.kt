package com.vishnurajeevan.shoe.annotation

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ShoeIn(val eventClass: KClass<*>)
