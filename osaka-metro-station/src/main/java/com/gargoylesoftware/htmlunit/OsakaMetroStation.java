package com.gargoylesoftware.htmlunit;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
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

	public static class Station implements Serializable {

		private static final long serialVersionUID = 1119439420330737324L;

		private String code, name, line = null;

		private URL url = null;

		private List<TransferRoute> transferRoutes = null;

	}

	public static class TransferRoute {

		private String line = null;

		private Boolean thirdParty, brt = null;

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
					cast(HtmlPage.class, webClient.getPage("https://subway.osakametro.co.jp/guide/routemap.php")));
			//
		} // try
			//
		return stations;
		//
	}

	private static List<Station> getStations(final WebClient webClient, final HtmlPage htmlPage) throws IOException {
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

	private static <N extends DomNode> N querySelector(final DomNode instance, final String selectors) {
		return instance != null ? instance.querySelector(selectors) : null;
	}

	private static Station toStation(final WebClient webClient, final HtmlPage htmlPage, final Node node)
			throws IOException {
		//
		Station station = new Station();
		//
		final NamedNodeMap attributes = node != null ? node.getAttributes() : null;
		//
		try {
			//
			final URL url = htmlPage != null
					? HtmlAnchor.getTargetUrl(getTextContent(getNamedItem(attributes, "href")), htmlPage)
					: null;
			//
			if ((station.url = url) != null) {
				station = ObjectUtils.defaultIfNull(merge(station, toStation(webClient, url)), station);
			}
			//
		} catch (final MalformedURLException e) {
			LOG.error(e.getMessage(), e);
		}
		//
		final String[] ss = StringUtils.split(getTextContent(getNamedItem(attributes, "alt")), ' ');
		final String[] fieldNames = new String[] { "code", "name" };
		//
		for (int i = 0; ss != null && i < ss.length && station != null; i++) {
			//
			try {
				if (i < fieldNames.length) {
					FieldUtils.writeField(station, fieldNames[i], ss[i], true);
				}
			} catch (final IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			}
			//
		}
		//
		return station;
		//
	}

	private static <T extends Serializable> T merge(final T a, final T b) {
		//
		if (a == null && b == null) {
			return null;
		} else if (a == null || b == null) {
			return SerializationUtils.clone(ObjectUtils.defaultIfNull(a, b));
		}
		//
		final T result = SerializationUtils.clone(a);
		//
		final Field[] fs = FieldUtils.getAllFields(a.getClass());
		Field f = null;
		//
		for (int i = 0; fs != null && i < fs.length; i++) {
			//
			if ((f = fs[i]) == null || Modifier.isFinal(f.getModifiers())) {
				continue;
			}
			//
			if (!f.isAccessible()) {
				f.setAccessible(true);
			}
			//
			try {
				f.set(result, ObjectUtils.defaultIfNull(f.get(a), f.get(b)));
			} catch (final IllegalAccessException e) {
				LOG.error(e.getMessage(), e);
			}
			//
		} // for
			//
		return result;
		//
	}

	private static Station toStation(final WebClient webClient, final URL url) throws IOException {
		//
		final Page page = webClient != null ? webClient.getPage(url) : null;
		//
		try {
			return toStation(cast(HtmlPage.class, page));
		} finally {
			if (page != null) {
				page.cleanUp();
			}
		}
		//
	}

	private static Station toStation(final HtmlPage htmlPage) {
		//
		final Station station = new Station();
		//
		station.line = getTextContent(querySelector(htmlPage, ".cs-lineName"));
		station.transferRoutes = toTransferRoutes(
				querySelectorAll(cast(DomNode.class, querySelector(htmlPage, ".transferList.clfix")), "li"));
		//
		return station;
		//
	}

	private static List<TransferRoute> toTransferRoutes(final DomNodeList<DomNode> domNodeList) {
		//
		List<TransferRoute> transferRoutes = null;
		TransferRoute transferRoute = null;
		//
		DomNode domNode = null;
		NamedNodeMap attributes = null;
		//
		String[] classes = null;
		//
		for (int i = 0; domNodeList != null && i < domNodeList.getLength(); i++) {
			//
			if ((domNode = domNodeList.get(i)) == null || (attributes = domNode.getAttributes()) == null) {
				continue;
			}
			//
			if (Objects.equals(Boolean.TRUE,
					(transferRoute = new TransferRoute()).thirdParty = Boolean.valueOf(ArrayUtils.contains(
							classes = StringUtils.split(getTextContent(attributes.getNamedItem("class")), ' '),
							"thirdParty")))) {
				transferRoute.line = getTextContent(domNode);
			} else {
				transferRoute.line = getTextContent(querySelector(domNode, ".cs-transferListLine"));
			}
			//
			transferRoute.brt = ArrayUtils.contains(classes, "thirdParty-brt");
			//
			if ((transferRoutes = ObjectUtils.getIfNull(transferRoutes, ArrayList::new)) != null) {
				transferRoutes.add(transferRoute);
			}
			//
		} // for
			//
		return transferRoutes;
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