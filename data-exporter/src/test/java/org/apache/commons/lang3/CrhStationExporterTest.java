package org.apache.commons.lang3;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.CrhStation.Station;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CrhStationExporterTest {

	private static Method METHOD_GET_NAME, METHOD_TO_METHODS, METHOD_XLSX, METHOD_CREATE_ROW,
			METHOD_SET_CELL_VALUE = null;

	@BeforeAll
	private static void beforeAll() throws NoSuchMethodException {
		//
		final Class<?> clz = CrhStationExporter.class;
		//
		(METHOD_GET_NAME = clz.getDeclaredMethod("getName", Member.class)).setAccessible(true);
		//
		(METHOD_TO_METHODS = clz.getDeclaredMethod("toMethods", String[].class, Method[].class)).setAccessible(true);
		//
		(METHOD_XLSX = clz.getDeclaredMethod("xlsx", File.class, List.class)).setAccessible(true);
		//
		(METHOD_CREATE_ROW = clz.getDeclaredMethod("createRow", Sheet.class, Integer.TYPE)).setAccessible(true);
		//
		(METHOD_SET_CELL_VALUE = clz.getDeclaredMethod("setCellValue", Cell.class, String.class)).setAccessible(true);
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

	@Test
	void testXlsx() throws NoSuchMethodException {
		//
		Assertions.assertDoesNotThrow(() -> xlsx(null, null));
		Assertions.assertDoesNotThrow(() -> xlsx(null, Collections.singletonList(null)));
		//
		final Constructor<Station> constructor = Station.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		Assertions.assertDoesNotThrow(() -> xlsx(null, Collections.singletonList(constructor.newInstance())));
		//
	}

	private static void xlsx(final File file, final List<Station> stations) throws Throwable {
		try {
			METHOD_XLSX.invoke(null, file, stations);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testCreateRow() throws Throwable {
		Assertions.assertNull(createRow(null, 0));
	}

	private static Row createRow(final Sheet instance, final int rownum) throws Throwable {
		try {
			final Object obj = METHOD_CREATE_ROW.invoke(null, instance, rownum);
			if (obj == null) {
				return null;
			} else if (obj instanceof Row) {
				return (Row) obj;
			}
			throw new Throwable(obj.getClass() != null ? obj.getClass().toString() : null);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testSetCellValue() {
		Assertions.assertDoesNotThrow(() -> setCellValue(null, null));
	}

	private static void setCellValue(final Cell instance, final String value) throws Throwable {
		try {
			METHOD_SET_CELL_VALUE.invoke(null, instance, value);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

}