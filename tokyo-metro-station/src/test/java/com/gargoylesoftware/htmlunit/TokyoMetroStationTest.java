package com.gargoylesoftware.htmlunit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

class TokyoMetroStationTest {

	private static Method METHOD_GET_STATIONS, METHOD_TO_STATION = null;

	@BeforeAll
	private static void beforeAll() throws NoSuchMethodException {
		//
		final Class<?> clz = TokyoMetroStation.class;
		//
		(METHOD_GET_STATIONS = clz.getDeclaredMethod("getStations", HtmlPage.class)).setAccessible(true);
		//
		(METHOD_TO_STATION = clz.getDeclaredMethod("toStation", HtmlPage.class, DomNode.class)).setAccessible(true);
		//
	}

	@Test
	void testGetStations() throws Throwable {
		Assertions.assertNull(getStations(null));
	}

	private static List<?> getStations(final HtmlPage htmlPage) throws Throwable {
		try {
			final Object obj = METHOD_GET_STATIONS.invoke(null, htmlPage);
			if (obj == null) {
				return null;
			} else if (obj instanceof List) {
				return (List<?>) obj;
			}
			throw new Throwable(obj.getClass() != null ? obj.getClass().toString() : null);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	@Test
	void testToStation() throws Throwable {
		//
		Assertions.assertEquals("TokyoMetroStation.Station[codes=<null>,hiragana=<null>,name=<null>,url=<null>]",
				ToStringBuilder.reflectionToString(toStation(null, null), ToStringStyle.SHORT_PREFIX_STYLE));
		//
	}

	private static Object toStation(final HtmlPage htmlPage, final DomNode domNode) throws Throwable {
		try {
			return METHOD_TO_STATION.invoke(null, htmlPage, domNode);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

}