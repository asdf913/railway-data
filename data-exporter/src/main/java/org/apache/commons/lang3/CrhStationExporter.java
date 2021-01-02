package org.apache.commons.lang3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

	private static void xlsx(final File file, final List<Station> stations) throws IllegalAccessException, IOException {
		//
		Workbook wb = null;
		//
		final Field[] fs = Station.class.getDeclaredFields();
		//
		Station station = null;
		Sheet sheet = null;
		Row row = null;
		//
		Field f = null;
		//
		for (int i = 0; stations != null && i < stations.size(); i++) {
			//
			if ((station = stations.get(i)) == null) {
				continue;
			}
			//
			if (wb == null && (row = createRow(sheet = (wb = new XSSFWorkbook()).createSheet(),
					sheet.getPhysicalNumberOfRows())) != null) {
				for (int j = 0; fs != null && j < fs.length; j++) {
					setCellValue(row.createCell(j), getName(fs[j]));
				} // for
			}
			//
			row = createRow(sheet, sheet.getPhysicalNumberOfRows());
			//
			for (int j = 0; fs != null && j < fs.length; j++) {
				//
				if ((f = fs[j]) == null) {
					continue;
				}
				//
				if (!f.isAccessible()) {
					f.setAccessible(true);
				}
				//
				setCellValue(row.createCell(j), toString(f.get(station)));
				//
			} // for
				//
		} // for
			//
		if (wb != null) {
			//
			try (final OutputStream os = file != null ? new FileOutputStream(file) : null) {
				if (os != null) {
					wb.write(os);
				}
			} // try
				//
		} // if
			//
	}

	private static String toString(final Object instance) {
		return instance != null ? instance.toString() : null;
	}

	private static Row createRow(final Sheet instance, final int rownum) {
		return instance != null ? instance.createRow(rownum) : null;
	}

	private static void setCellValue(final Cell instance, final String value) {
		if (instance != null) {
			instance.setCellValue(value);
		}
	}

}