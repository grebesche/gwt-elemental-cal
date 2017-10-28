package com.grebesche.cal;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import elemental.client.Browser;
import elemental.dom.Document;
import elemental.dom.Element;
import elemental.events.Event;
import elemental.events.EventListener;

import java.util.Date;

public class CalendarDay {

  private static Style gss = GWT.<Resources>create(Resources.class).css();

  static {
    gss.ensureInjected();
  }

  private static Document document = Browser.getDocument();

  private final Element cell;
  private Date cellDate;
  private boolean handleClick = true;

  public CalendarDay(Date cellDate, boolean enabled) {
    this.cellDate = cellDate;

    this.cell = document.createElement("td");
    if (isToday(this.cellDate)) {
      this.cell.getClassList().add(gss.today());
    }
    if (isWE(this.cellDate)) {
      this.cell.getClassList().add(gss.weekend());
      this.cell.getClassList().add(gss.disabled());
      handleClick = false;
    } else if (enabled) {
      this.cell.getClassList().add(gss.enabled());
    } else {
      this.cell.getClassList().add(gss.disabled());
      handleClick = false;
    }
    this.cell.setTextContent("" + this.cellDate.getDate());
  }

  public void setEventCreationStart(EventCreationStart eventCreationStart) {
    if(handleClick) {
      this.cell.addEventListener("mousedown", event -> {
        if (eventCreationStart != null) eventCreationStart.start(this.cellDate);
      });
    }
  }

  public void setClickHandler(EventListener eventListener) {
    if(handleClick) {
      this.cell.addEventListener("click", eventListener);
    }
  }

  public Date getCellDate() {
    return cellDate;
  }

  private boolean isToday(Date date) {
    Date today = new Date();
    return date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate();
  }

  private boolean isWE(Date date) {
    return date.getDay() == 0 || date.getDay() == 6;
  }

  public Element getCell() {
    return cell;
  }

  public void setSelected(boolean selected) {
    if(selected) {
      this.cell.getClassList().add(gss.selected());
    } else {
      this.cell.getClassList().remove(gss.selected());
    }
  }

  interface Style extends CssResource {
    String enabled();

    String disabled();

    String weekend();

    String today();

    String selected();
  }

  interface Resources extends ClientBundle {
    @Source("CalendarDay.gss")
    Style css();
  }

  @FunctionalInterface
  public interface EventCreationStart {
    void start(Date startDate);
  }
}
