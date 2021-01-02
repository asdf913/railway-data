package org.apache.commons.lang3;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CrhStationExporterTest {

	private static Method METHOD_GET_NAME, METHOD_TO_METHODS = null;

	@BeforeAll
	private static void beforeAll() throws NoSuchMethodException {
		//
		final Class<?> clz = CrhStationExporter.class;
		//
		(METHOD_GET_NAME = clz.getDeclaredMethod("getName", Member.class)).setAccessible(true);
		//
		(METHOD_TO_METHODS = clz.getDeclaredMethod("toMethods", String[].class, Method[].class)).setAccessible(true);
		//
	}

	@Test
	void testGetName() throws Throwable {
		//
		final String methodName = "toString";
		Assertions.assertSame(methodName, getName(Object.class.getDeclaredMethod(methodName)));
		//
	}

	private static String getName(final Member instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_NAME.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(obj.getClass() != null ? obj.getClass().toString() : null);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testToMethods() throws Throwable {
		//
		Assertions.assertNull(toMethods(null, null));
		Assertions.assertNull(toMethods(new String[] {}, null));
		Assertions.assertNull(toMethods(new String[] { null }, null));
		//
		Assertions.assertEquals(Collections.emptyList(), toMethods(new String[] { null }, new Method[] {}));
		Assertions.assertEquals(Collections.emptyList(), toMethods(new String[] { null }, new Method[] { null }));
		//
	}

	private static List<Method> toMethods(final String[] args, final Method[] ms) throws Throwable {
		try {
			final Object obj = METHOD_TO_METHODS.invoke(null, args, ms);
			if (obj == null) {
				return null;
			} else if (obj instanceof List) {
				return (List) obj;
			}
			throw new Throwable(obj.getClass() != null ? obj.getClass().toString() : null);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

}