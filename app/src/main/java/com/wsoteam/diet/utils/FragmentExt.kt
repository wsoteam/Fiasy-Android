package com.wsoteam.diet.utils

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf

fun Fragment.args(): Bundle {
    if (arguments == null) {
        arguments = Bundle()
    }

    return arguments!!
}

@JvmOverloads
fun FragmentActivity.attachOnce(fragment: Fragment,
                                tag: String,
                                container: Int = android.R.id.content,
                                withBackStack: Boolean = false) {

    if (supportFragmentManager.findFragmentByTag(tag) == null) {
        attach(fragment, container, tag, withBackStack)
    }
}

@JvmOverloads
fun FragmentActivity.attach(fragment: Fragment,
                            container: Int = android.R.id.content,
                            tag: String? = null,
                            withBackStack: Boolean = false) {

    val transaction = supportFragmentManager.beginTransaction()
            .add(container, fragment, tag)

    if (withBackStack) {
        transaction.addToBackStack(tag);
    }

    transaction.commit()
}


inline fun <T> Fragment.argument() = FragmentArgument<T>()

class FragmentArgument<T> : ReadWriteProperty<Fragment, T?> {

    private var cached: T? = null

    private fun isParcelable(clazz: KType): Boolean {
        return clazz.isSubtypeOf(Parcelable::class.createType(nullable = true))
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    override fun getValue(target: Fragment, property: KProperty<*>): T? {
        if (cached != null) {
            return cached
        }

        val bundle = target.args()
        val type = property.returnType.classifier

        cached = when {
            isParcelable(property.returnType) -> bundle.getParcelable<Parcelable>(property.name)
            type == Boolean::class -> bundle.getBoolean(property.name)

            else -> throw IllegalStateException("cannot deserialize ${property.returnType}")
        } as T?

        return cached
    }

    override fun setValue(target: Fragment, property: KProperty<*>, value: T?) {
        cached = value

        val bundle = target.args()
        val type = property.returnType.classifier

        when {
            isParcelable(property.returnType) -> bundle.putParcelable(property.name, value as Parcelable?)
            type == Boolean::class -> bundle.putBoolean(property.name, value as Boolean)

            else -> throw IllegalStateException("cannot serialize ${property.returnType}")
        }
    }
}