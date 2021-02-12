package com.gargoylesoftware.htmlunit;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HankyuStation {

	public static class Station implements Serializable {

		private static final long serialVersionUID = -3084774415491829146L;

		private String name, hiragana = null;

		private URL url = null;

	}

	public static List<Station> getStations() throws IOException {
		//
		List<Station> stations = null;
		//
		try (final WebClient webClient = new WebClient()) {
			//
			final WebClientOptions webClientOptions = webClient.getOptions();
			if (webClientOptions != null) {
				webClientOptions.setJavaScriptEnabled(false);
			}
			//
			stations = getStations(webClient,
					cast(HtmlPage.class, webClient.getPage("https://www.hankyu.co.jp/station/info.html")));
			//
		} // try
			//
		return stations;
		//
	}

	private static List<Station> getStations(final WebClient webClient, final HtmlPage htmlPage) throws IOException {
		//
		final DomNodeList<DomNode> domNodeList = querySelectorAll(htmlPage, "dl dt a");
		//
		List<Station> stations = null;
		//
		for (int i = 0; domNodeList != null && i < domNodeList.size(); i++) {
			//
			if (stations == null) {
				stations = new ArrayList<>();
			}
			//
			stations.add(toStation(webClient, htmlPage, domNodeList.get(i)));
			//
		} // for
			//
		return stations;
		//
	}

	private static DomNodeList<DomNode> querySelectorAll(final DomNode instance, final String selectors) {
		return instance != null ? instance.querySelectorAll(selectors) : null;
	}

	private static Station toStation(final WebClient webClient, final HtmlPage htmlPage, final Node node)
			throws IOException {
		//
		final Station station = new Station();
		//
		final HtmlAnchor htmlAnchor = cast(HtmlAnchor.class, node);
		if (htmlAnchor != null) {
			station.url = HtmlAnchor.getTargetUrl(htmlAnchor.getHrefAttribute(), htmlPage);
		}
		//
		final String textCotent = getTextContent(node);
		station.name = StringUtils.substringBefore(textCotent, "（");
		station.hiragana = StringUtils.substringBetween(textCotent, "（", "）");
		//
		return station;
		//
	}

	private static String getTextContent(final Node instance) {
		return instance != null ? instance.getTextContent() : null;
	}

	private static <T> T cast(final Class<T> clz, final Object instance) {
		return clz != null && clz.isInstance(instance) ? clz.cast(instance) : null;
	}

}