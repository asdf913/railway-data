package org.apache.commons.lang3;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.script.ScriptException;

import org.apache.commons.lang3.CrhStation.Station;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CrhStationExporter {

	public static void main(final String[] args)
			throws IOException, ScriptException, IllegalAccessException, InvocationTargetException {
		//
		List<Station> stations = null;
		//
		final List<Method> methods = toMethods(args, CrhStationExporter.class.getDeclaredMethods());
		//
		Method m = null;
		//
		for (int i = 0; methods != null && i < methods.size(); i++) {
			//
			if ((m = methods.get(i)) == null) {
				continue;
			}
			//
			if (Arrays.equals(m.getParameterTypes(), new Class[] { File.class, List.class })) {
				//
				if (stations == null) {
					stations = CrhStation.toStations();
				}
				//
				m.invoke(null,
						new File(String.format("%1$tY-%1$tm-%1$td_%1$tH%1$tM%1$ts.%2$s", new Date(), getName(m))),
						stations);
				//
			} // if
				//
		} // for
			//
	}

	private static String getName(final Member instance) {
		return instance != null ? instance.getName() : null;
	}

	private static List<Method> toMethods(final String[] args, final Method[] ms) {
		//
		List<Method> methods = null;
		List<Method> temp = null;
		//
		for (int i = 0; args != null && i < args.length; i++) {
			//
			final String arg = args[i];
			//
			if ((temp = collect(filter(stream(ms), m -> Objects.equals(getName(m), arg)),
					Collectors.toList())) == null) {
				continue;
			}
			//
			for (final Method m : temp) {
				//
				if (m == null) {
					continue;
				} // skip null
					//
				if (methods == null) {
					methods = new ArrayList<>();
				}
				//
				if (!methods.contains(m)) {
					methods.add(m);
				}
				//
			} // for
				//
		} // for
			//
		if (methods != null && !methods.isEmpty()) {
			return methods;
		}
		//
		return collect(filter(stream(ms), m -> m != null), Collectors.toList());
		//
	}

	private static <T> Stream<T> stream(T[] array) {
		return array != null ? Arrays.stream(array) : null;
	}

	private static <T> Stream<T> filter(final Stream<T> instance, final Predicate<? super T> predicate) {
		return instance != null ? instance.filter(predicate) : instance;
	}

	private static <T, R, A> R collect(final Stream<T> instance, final Collector<? super T, A, R> collector) {
		return instance != null ? instance.collect(collector) : null;
	}

	private static void json(final File file, final List<Station> stations) throws IOException {
		//
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		objectMapper.writeValue(file, stations);
		//
	}

}