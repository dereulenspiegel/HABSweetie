package de.akuz.android.openhab.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Widget extends AbstractOpenHABObject {

	protected String widgetId;

	protected String type;

	protected String label;

	protected String icon;

	protected String url;

	protected Item item;

	protected ArrayList<Mapping> mappings;

	protected Page linkedPage;

	protected Boolean switchSupport;

	protected Integer sendFrequency;

	protected Float minValue;

	protected Float maxValue;

	protected Float step;

	protected Integer refresh;

	protected String period;
	/**
	 * Frame childs
	 */
	protected ArrayList<Widget> widget;

	public String getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public List<Mapping> getMappings() {
		return mappings;
	}

	public void setMappings(ArrayList<Mapping> mapping) {
		this.mappings = mapping;
	}

	public Page getLinkedPage() {
		return linkedPage;
	}

	public void setLinkedPage(Page linkedPage) {
		this.linkedPage = linkedPage;
	}

	public String getImageUrl() {
		// FIXME workaround for possible in openHAB. icon should be null if
		// there is no icon defined. Currently icon as the lower case
		// represantation of the widget type if not specified
		if (type.equalsIgnoreCase(icon)) {
			return null;
		}
		if (getIcon() == null) {
			return null;
		}
		return baseUrl + "/images/" + getIcon() + ".png";
	}

	@Override
	public void setBaseUrl(String baseUrl) {
		super.setBaseUrl(baseUrl);
		if (widget != null) {
			for (Widget w : widget) {
				w.setBaseUrl(baseUrl);
			}
		}
		if (item != null) {
			item.setBaseUrl(baseUrl);
		}
		if (linkedPage != null) {
			linkedPage.setBaseUrl(baseUrl);
		}
	}

	public ArrayList<Widget> getWidget() {
		return widget;
	}

	public void setWidget(ArrayList<Widget> widget) {
		this.widget = widget;

	}

	public Boolean isSwitchSupport() {
		return switchSupport;
	}

	public void setSwitchSupport(Boolean switchSupport) {
		this.switchSupport = switchSupport;
	}

	public Integer getSendFrequency() {
		return sendFrequency;
	}

	public void setSendFrequency(Integer sendFrequency) {
		this.sendFrequency = sendFrequency;
	}

	public Float getMinValue() {
		return minValue;
	}

	public void setMinValue(Float minValue) {
		this.minValue = minValue;
	}

	public Float getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Float maxValue) {
		this.maxValue = maxValue;
	}

	public Float getStep() {
		return step;
	}

	public void setStep(Float step) {
		this.step = step;
	}

	public String getUrl() {
		return url;
	}

	public String getFullUrl() {
		if (url != null && !url.startsWith("http")) {
			return baseUrl + "/" + url;
		}
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getRefresh() {
		return refresh;
	}

	public void setRefresh(Integer refresh) {
		this.refresh = refresh;
	}

	public Boolean getSwitchSupport() {
		return switchSupport;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(3, 17).append(widgetId).hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (!(o instanceof Widget)) {
			return false;
		}
		return ((Widget) o).getWidgetId().equals(widgetId);
	}

	public boolean containsWidget(Widget widget) {
		if (this.widget == null || this.widget.size() == 0) {
			return false;
		}
		for (Widget w : this.widget) {
			if (w.getWidgetId().equals(widget.getWidgetId())) {
				return true;
			}
			if (w.containsWidget(widget)) {
				return true;
			}
		}
		return false;
	}

	public boolean updateSubWidget(Widget widget) {
		if (this.widget == null || this.widget.size() == 0) {
			return false;
		}
		for (Widget w : this.widget) {
			if (w.getWidgetId().equals(widget.getWidgetId())) {
				int position = this.widget.indexOf(w);
				this.widget.remove(position);
				this.widget.add(position, widget);
				return true;
			} else {
				if (w.updateSubWidget(widget)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean hasLinkedPage(String pageUrl) {
		if (linkedPage != null && linkedPage.getLink().equals(pageUrl)) {
			return true;
		} else if (widget != null) {
			for (Widget w : widget) {
				if (w.hasLinkedPage(pageUrl)) {
					return true;
				}
			}

		}
		return false;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
}
