package org.apache.commons.lang3;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CrhStationTest {

	private static Method METHOD_TO_STATIONS_URL, METHOD_TO_STATIONS_STRING, METHOD_TO_STATIONS_STRING_ARRAY,
			METHOD_GET_CONTEXT, METHOD_GET_ATTRIBUTE = null;

	@BeforeAll
	private static void beforeAll() throws NoSuchMethodException {
		//
		final Class<?> clz = CrhStation.class;
		//
		(METHOD_TO_STATIONS_URL = clz.getDeclaredMethod("toStations", URL.class)).setAccessible(true);
		//
		(METHOD_TO_STATIONS_STRING = clz.getDeclaredMethod("toStations", String.class)).setAccessible(true);
		//
		(METHOD_TO_STATIONS_STRING_ARRAY = clz.getDeclaredMethod("toStations", String[].class)).setAccessible(true);
		//
		(METHOD_GET_CONTEXT = clz.getDeclaredMethod("getContext", ScriptEngine.class)).setAccessible(true);
		//
		(METHOD_GET_ATTRIBUTE = clz.getDeclaredMethod("getAttribute", ScriptContext.class, String.class))
				.setAccessible(true);
		//
	}

	@Test
	void testToStations() throws Throwable {
		//
		Assertions.assertNull(toStations((URL) null));
		Assertions.assertNull(toStations((String) null));
		Assertions.assertNull(toStations((String[]) null));
		Assertions.assertNull(toStations(new String[] { null }));
		Assertions.assertNull(toStations(""));
		//
		final List<?> list = toStations("var station_names=\"@bjb|北京北|VAP|beijingbei|bjb\";");
		Assertions.assertNotNull(list);
		Assertions.assertEquals(1, list.size());
		Assertions.assertEquals(
				"CrhStation.Station[chineseName=北京北,englishName=beijingbei,pinyin=bjb,pinyinAbbreviation=bjb,telegraphCode=VAP]",
				ToStringBuilder.reflectionToString(list.get(0), ToStringStyle.SHORT_PREFIX_STYLE));
		//
	}

	private static List<?> toStations(final URL url) throws Throwable {
		try {
			final Object obj = METHOD_TO_STATIONS_URL.invoke(null, url);
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

	private static List<?> toStations(final String string) throws Throwable {
		try {
			final Object obj = METHOD_TO_STATIONS_STRING.invoke(null, string);
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

	private static List<?> toStations(final String[] ss) throws Throwable {
		try {
			final Object obj = METHOD_TO_STATIONS_STRING_ARRAY.invoke(null, (Object) ss);
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
	void testGetContext() throws Throwable {
		Assertions.assertNull(getContext(null));
	}

	private static ScriptContext getContext(final ScriptEngine instance) throws Throwable {
		try {
			final Object obj = METHOD_GET_CONTEXT.invoke(null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof ScriptContext) {
				return (ScriptContext) obj;
			}
			throw new Throwable(obj.getClass() != null ? obj.getClass().toString() : null);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testGetAttribute() throws Throwable {
		Assertions.assertNull(getAttribute(null, null));
	}

	private static Object getAttribute(final ScriptContext instance, final String name) throws Throwable {
		try {
			return METHOD_GET_ATTRIBUTE.invoke(null, instance, name);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

}