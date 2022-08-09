import java.lang.reflect.Field
import java.lang.reflect.Method


fun <O, V> getFieldValue(fieldName: String, o: O, oClass: Class<O>): V {
    val field: Field = oClass.getDeclaredField(fieldName)
    field.isAccessible = true
    return field.get(o) as V
}

fun <O, V> setFieldValue(fieldName: String?, o: O, oClass: Class<O>, value: V) {
    val field: Field = oClass.getDeclaredField(fieldName)
    field.isAccessible = true
    field.set(o, value)
}

fun <O, V> invokeMethod(
    methodName: String,
    o: O,
    oClass: Class<O>,
    args: Array<Any?>?,
    parameterTypes: Array<Class<*>?>
): V {
    val method: Method = oClass.getMethod(methodName, *parameterTypes)
    method.isAccessible = true
    return method.invoke(o, args) as V
}