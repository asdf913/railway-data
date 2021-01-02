package org.apache.commons.lang3;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;

public class CrhStation {

	static class Station {

		private String pinyin, telegraphCode, chineseName, englishName, pinyinAbbreviation = null;

	}

	public static List<Station> toStations() throws IOException, ScriptException {
		return toStations(new URL("https://www.12306.cn/index/script/core/common/station_name.js"));
	}

	private static List<Station> toStations(final URL url) throws IOException, ScriptException {
		//
		final URLConnection urlConnection = url != null ? url.openConnection() : null;
		//
		byte[] bs = null;
		//
		try (final InputStream is = urlConnection != null ? urlConnection.getInputStream() : null) {
			bs = IOUtils.toByteArray(is);
		}
		//
		return bs != null ? toStations(new String(bs)) : null;
	}

	private static List<Station> toStations(final String string) throws ScriptException {
		//
		final ScriptEngine se = new ScriptEngineManager().getEngineByExtension("js");
		final ScriptContext sc = getContext(se);
		if (se != null && string != null) {
			se.eval(string);
		}
		//
		return toStations(StringUtils.split(toString(getAttribute(sc, "station_names")), '@'));
		//
	}

	private static ScriptContext getContext(final ScriptEngine instance) {
		return instance != null ? instance.getContext() : null;
	}

	private static Object getAttribute(final ScriptContext instance, final String name) {
		return instance != null ? instance.getAttribute(name) : null;
	}

	private static List<Station> toStations(final String[] ss) throws ScriptException {
		//
		List<Station> stations = null;
		Station station = null;
		//
		for (int i = 0; ss != null && i < ss.length; i++) {
			//
			if ((station = toStation(ss[i])) == null) {
				continue;
			}
			//
			if (stations == null) {
				stations = new ArrayList<>();
			}
			stations.add(station);
			//
		} // for
			//
		return stations;
		//
	}

	private static Station toStation(final String instance) {
		//
		Station station = null;
		//
		final String[] ss = StringUtils.split(instance, '|');
		//
		for (int i = 0; ss != null && i < ss.length; i++) {
			//
			if (station == null) {
				station = new Station();
			}
			//
			switch (i) {
			case 0:
				station.pinyin = ss[0];
				break;
			case 1:
				station.chineseName = ss[1];
				break;
			case 2:
				station.telegraphCode = ss[2];
				break;
			case 3:
				station.englishName = ss[3];
				break;
			case 4:
				station.pinyinAbbreviation = ss[4];
				break;
			}// switch
				//
		} // for
			//
		return station;
		//
	}

	private static String toString(final Object instance) {
		return instance != null ? instance.toString() : null;
	}

}