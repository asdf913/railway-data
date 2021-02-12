package com.gargoylesoftware.htmlunit;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HankyuStation {

	private static final Logger LOG = LoggerFactory.getLogger(HankyuStation.class);

	public static class Station implements Serializable {

		private static final long serialVersionUID = -3084774415491829146L;

		private String code, name, hiragana = null;

		private URL url = null;

		private List<String> equipments, serviceFacilities = null;

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

	private static <N extends DomNode> N querySelector(final DomNode instance, final String selectors) {
		return instance != null ? instance.querySelector(selectors) : null;
	}

	private static Station toStation(final WebClient webClient, final HtmlPage htmlPage, final Node node)
			throws IOException {
		//
		Station station = new Station();
		//
		final String textCotent = getTextContent(node);
		station.name = StringUtils.substringBefore(textCotent, "（");
		station.hiragana = StringUtils.substringBetween(textCotent, "（", "）");
		//
		final HtmlAnchor htmlAnchor = cast(HtmlAnchor.class, node);
		if (htmlAnchor != null
				&& (station.url = HtmlAnchor.getTargetUrl(htmlAnchor.getHrefAttribute(), htmlPage)) != null) {
			//
			station = ObjectUtils.defaultIfNull(merge(station, toStation(webClient, station.url)), station);
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
		Page page = null;
		//
		try {
			return toStation(cast(HtmlPage.class, page = webClient != null ? webClient.getPage(url) : null));
		} catch (final FailingHttpStatusCodeException e) {
			return null;
		} finally {
			if (page != null) {
				page.cleanUp();
			}
		}
		//
	}

	private static Station toStation(final HtmlPage htmlPage) {
		//
		Station station = null;
		//
		HtmlImage htmlImage = cast(HtmlImage.class, querySelector(htmlPage, "#section_h1 img"));
		if (htmlImage != null) {
			(station = new Station()).code = StringUtils.substringAfter(htmlImage.getAltAttribute(), ' ');
		}
		//
		if (station == null) {
			station = new Station();
		}
		station.equipments = getEquipments(htmlPage);
		station.serviceFacilities = getServiceFacilities(htmlPage);
		//
		return station;
		//
	}

	private static List<String> getEquipments(final DomNode input) {
		//
		List<String> equipments = null;
		//
		final Iterable<DomElement> childElements = getChildElements(
				getNextElementSibling(querySelector(input, ".section_h3:nth-child(1)")));
		//
		if (childElements != null) {
			//
			List<DomNode> childNodes = null;
			String altAttribute = null;
			HtmlImage htmlImage = null;
			//
			for (final DomElement childElement : childElements) {
				//
				if (childElement == null) {
					continue;
				}
				//
				for (int i = 0; (childNodes = childElement.getChildNodes()) != null && i < childNodes.size(); i++) {
					//
					if ((htmlImage = cast(HtmlImage.class, childNodes.get(0))) == null) {
						continue;
					}
					//
					if (equipments == null) {
						equipments = new ArrayList<>();
					}
					//
					if (!equipments.contains(altAttribute = htmlImage.getAltAttribute())) {
						equipments.add(altAttribute);
					}
					//
				}
				//
			} // for
				//
		} // if
			//
		return equipments;
		//
	}

	private static DomElement getNextElementSibling(final DomNode instance) {
		return instance != null ? instance.getNextElementSibling() : null;
	}

	private static Iterable<DomElement> getChildElements(final DomElement instance) {
		return instance != null ? instance.getChildElements() : null;
	}

	private static List<String> getServiceFacilities(final DomNode input) {
		//
		List<String> serviceFacilities = null;
		//
		final Iterable<DomElement> childElements = getChildElements(
				getNextElementSibling(querySelector(input, ".section_h3:nth-child(3)")));
		//
		if (childElements != null) {
			//
			List<DomNode> childNodes = null;
			String altAttribute = null;
			HtmlImage htmlImage = null;
			//
			Iterable<DomNode> children = null;
			//
			for (final DomElement childElement : childElements) {
				//
				if (childElement == null) {
					continue;
				}
				//
				for (int i = 0; (childNodes = childElement.getChildNodes()) != null && i < childNodes.size(); i++) {
					//
					if ((children = getChildren(cast(HtmlAnchor.class, childNodes.get(i)))) == null) {
						continue;
					} // skip null
						//
					for (final DomNode child : children) {
						//
						if ((htmlImage = cast(HtmlImage.class, child)) == null) {
							continue;
						}
						//
						if (serviceFacilities == null) {
							serviceFacilities = new ArrayList<>();
						}
						//
						if (!serviceFacilities.contains(altAttribute = htmlImage.getAltAttribute())) {
							serviceFacilities.add(altAttribute);
						}
						//
					} // for
						//
				} // for
					//
			} // for
				//
		} // if
			//
		return serviceFacilities;
		//
	}

	private static final Iterable<DomNode> getChildren(final DomNode instance) {
		return instance != null ? instance.getChildren() : null;
	}

	private static String getTextContent(final Node instance) {
		return instance != null ? instance.getTextContent() : null;
	}

	private static <T> T cast(final Class<T> clz, final Object instance) {
		return clz != null && clz.isInstance(instance) ? clz.cast(instance) : null;
	}

}