package de.akuz.android.openhab.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonRootName;

import com.google.api.client.util.Key;

@JsonRootName("page")
public class Page extends AbstractOpenHABObject {

	@Key
	private String id;
	@Key
	private String title;
	@Key
	private String icon;
	@Key
	private String link;
	@Key
	private Page parent;
	@Key
	private ArrayList<Widget> widget;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public Page getParent() {
		return parent;
	}

	public void setParent(Page parent) {
		this.parent = parent;
	}

	public ArrayList<Widget> getWidget() {
		return widget;
	}

	public void setWidget(ArrayList<Widget> widget) {
		this.widget = widget;
	}

	public String getIconUrl() {
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

		if (parent != null) {
			parent.setBaseUrl(baseUrl);
		}
	}

	@Override
	public void setReceivedAt(long receivedAt) {
		super.setReceivedAt(receivedAt);
		if (widget != null) {
			for (Widget w : widget) {
				w.setReceivedAt(receivedAt);
			}
		}
		if (parent != null) {
			parent.setReceivedAt(receivedAt);
		}
	}

	public boolean hasSubPage(String pageUrl) {
		if (widget != null) {
			for (Widget w : widget) {
				if (w.hasLinkedPage(pageUrl)) {
					return true;
				}
			}
		}
		return false;
	}

	public void updateWidget(Widget widget) {
		for (int i = 0; i < this.widget.size(); i++) {
			Widget w = this.widget.get(i);
			if (w.getWidgetId().equals(widget.getWidgetId())) {
				this.widget.remove(i);
				this.widget.add(i, widget);
				return;
			} else if (w.updateSubWidget(widget)) {
				return;
			}
		}
	}

	public void batchUpdateWidgets(List<Widget> widgets) {
		for (Widget w : widgets) {
			updateWidget(w);
		}
	}

}
