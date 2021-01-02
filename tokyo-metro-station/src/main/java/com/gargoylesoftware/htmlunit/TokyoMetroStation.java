package com.gargoylesoftware.htmlunit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TokyoMetroStation {

	private static final Logger LOG = LoggerFactory.getLogger(TokyoMetroStation.class);

	public static class Station {

		private String name, hiragana = null;

		private URL url = null;

		private List<String> codes = null;

	}

	public static List<Station> getStations() throws IOException {
		//
		Page page = null;
		//
		try (final WebClient webClient = new WebClient()) {
			page = webClient.getPage("https://www.tokyometro.jp/station/index03.html");
		}
		//
		return getStations(cast(HtmlPage.class, page));
		//
	}

	private static List<Station> getStations(final HtmlPage htmlPage) throws IOException {
		//
		final DomNodeList<DomNode> domNodeList = querySelectorAll(htmlPage, ".v2_gridSCol3");
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

	private static Station toStation(final HtmlPage htmlPage, final DomNode domNode) {
		//
		Station station = new Station();
		//
		try {
			//
			station.url = htmlPage != null
					? HtmlAnchor.getTargetUrl(getAttribute(querySelector(domNode, "a"), "href"), htmlPage)
					: null;
			//
		} catch (final MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}
		//
		final DomNode p = querySelector(domNode, "p");
		//
		if (p != null) {
			//
			station.name = getTextContent(item(getChildNodes(p), 0));
			station.hiragana = getTextContent(item(getChildNodes(p), 1));
			//
		}
		//
		final DomNodeList<DomNode> imgs = querySelectorAll(domNode, "img");
		//
		if (imgs != null) {
			//
			NamedNodeMap attributes = null;
			String code = null;
			//
			for (final Node img : imgs) {
				//
				if (img == null || (attributes = img.getAttributes()) == null) {
					continue;
				}
				//
				if (station == null) {
					station = new Station();
				}
				//
				if (station.codes == null) {
					station.codes = new ArrayList<>();
				}
				//
				if (!station.codes.contains(code = getTextContent(getNamedItem(attributes, "alt")))) {
					station.codes.add(code);
				}
				//
			} // for
				//
		} // if
			//
		return station;
		//
	}

	private static String getAttribute(final Element instance, final String name) {
		return instance != null ? instance.getAttribute(name) : null;
	}

	private static Node getNamedItem(final NamedNodeMap instance, final String name) {
		return instance != null ? instance.getNamedItem(name) : null;
	}

	private static <N extends DomNode> N querySelector(final DomNode instance, final String selectors) {
		return instance != null ? instance.querySelector(selectors) : null;
	}

	private static NodeList getChildNodes(final Node instance) {
		return instance != null ? instance.getChildNodes() : null;
	}

	private static String getTextContent(final Node instance) {
		return instance != null ? instance.getTextContent() : null;
	}

	private static Node item(final NodeList instance, final int index) {
		return instance != null ? instance.item(index) : null;
	}

	private static <T> T cast(final Class<T> clz, final Object instance) {
		return clz != null && clz.isInstance(instance) ? clz.cast(instance) : null;
	}

}