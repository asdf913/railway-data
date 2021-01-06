package com.gargoylesoftware.htmlunit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class OsakaMetroStation {

	private static final Logger LOG = LoggerFactory.getLogger(OsakaMetroStation.class);

	public static class Station {

		private String code, name = null;

		private URL url = null;

	}

	public static List<Station> getStations() throws IOException {
		//
		Page page = null;
		//
		try (final WebClient webClient = new WebClient()) {
			//
			final WebClientOptions webClientOptions = webClient.getOptions();
			if (webClientOptions != null) {
				webClientOptions.setJavaScriptEnabled(false);
			}
			//
			page = webClient.getPage("https://subway.osakametro.co.jp/guide/routemap.php");
			//
		} // try
			//
		return getStations(cast(HtmlPage.class, page));
		//
	}

	private static List<Station> getStations(final HtmlPage htmlPage) throws IOException {
		//
		final DomNodeList<DomNode> domNodeList = querySelectorAll(htmlPage, "area");
		//
		List<Station> stations = null;
		//
		for (int i = 0; domNodeList != null && i < domNodeList.size(); i++) {
			//
			if (stations == null) {
				stations = new ArrayList<>();
			}
			//
			stations.add(toStation(htmlPage, domNodeList.get(i)));
			//
		} // for
			//
		return stations;
		//
	}

	private static DomNodeList<DomNode> querySelectorAll(final DomNode instance, final String selectors) {
		return instance != null ? instance.querySelectorAll(selectors) : null;
	}

	private static Station toStation(final HtmlPage htmlPage, final Node node) {
		//
		Station station = new Station();
		//
		final NamedNodeMap attributes = node != null ? node.getAttributes() : null;
		//
		try {
			//
			station.url = htmlPage != null
					? HtmlAnchor.getTargetUrl(getTextContent(getNamedItem(attributes, "href")), htmlPage)
					: null;
			//
		} catch (final MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}
		//
		final String[] ss = StringUtils.split(getTextContent(getNamedItem(attributes, "alt")), ' ');
		//
		for (int i = 0; ss != null && i < ss.length; i++) {
			if (i == 0) {
				station.code = ss[i];
			} else if (i == 1) {
				station.name = ss[i];
			}
		}
		//
		return station;
		//
	}

	private static Node getNamedItem(final NamedNodeMap instance, final String name) {
		return instance != null ? instance.getNamedItem(name) : null;
	}

	private static String getTextContent(final Node instance) {
		return instance != null ? instance.getTextContent() : null;
	}

	private static <T> T cast(final Class<T> clz, final Object instance) {
		return clz != null && clz.isInstance(instance) ? clz.cast(instance) : null;
	}

}