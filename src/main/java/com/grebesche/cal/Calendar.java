package com.grebesche.cal;

import elemental.client.Browser;
import elemental.dom.Document;
import elemental.dom.Element;
import elemental.html.TableElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Calendar {

  private static Document document = Browser.getDocument();

  private Element container;
  private TableElement tableElement;
  private List<CalendarDay> days = new ArrayList<>();
  private List<Element> cols = new ArrayList<>();
  private List<Element> tableBodyRows = new ArrayList<>();
  private MouseEventCreator mouseEventCreator;

  private int tableWidth = 700; // default
  private int tableHeight = 500; // default
  private int headerHeight = 30; // default

  private Date currentDate;

  public Calendar() {

    this.container = document.createDivElement();


    this.tableElement = document.createTableElement();
    this.tableElement.setCellPadding("0px");
    this.tableElement.setCellSpacing("0px");
    this.container.appendChild(this.tableElement);

    mouseEventCreator = new MouseEventCreator();
    mouseEventCreator.setEventCreatedHandler(this::onCreatedEvent);
    mouseEventCreator.setEventCreatingHandler(this::onCreatingEvent);
    mouseEventCreator.setEventCreatingFinished(() -> {
      for (CalendarDay day : days) {
        day.setSelected(false);
      }
    });

    applySize();
    navigateToDate(new Date());
  }

  public void setSize(int height, int width) {
    this.tableHeight = height;
    this.tableWidth = width;
    applySize();
  }

  private void applySize() {
    this.tableElement.getStyle().setHeight(tableHeight + "px");
    this.tableElement.getStyle().setWidth(tableWidth + "px");
    for (Element col : cols) {
      col.setAttribute("width", (tableWidth / cols.size()) + "px");
    }
    int rowHeight = 0;
    if (tableBodyRows.size() != 0) {
      rowHeight = (tableHeight - headerHeight) / tableBodyRows.size();
    }
    for (Element row : tableBodyRows) {
      row.getStyle().setHeight(rowHeight + "px");
    }
  }

  public void navigateToDate(Date date) {

    days.clear();
    while (this.tableElement.hasChildNodes()) {
      this.tableElement.removeChild(this.tableElement.getLastChild());
    }

    int year = date.getYear();
    int month = date.getMonth();
    currentDate = new Date(year, month, 1);
    Date tmpCurrentDate = new Date(year, month, 1);

    while (tmpCurrentDate.getDay() != 0) {
      tmpCurrentDate.setDate(tmpCurrentDate.getDate() - 1);
    }

    // COL
    cols.clear();
    cols.add(document.createElement("col"));
    cols.add(document.createElement("col"));
    cols.add(document.createElement("col"));
    cols.add(document.createElement("col"));
    cols.add(document.createElement("col"));
    cols.add(document.createElement("col"));
    cols.add(document.createElement("col"));
    for (Element col : cols) {
      tableElement.appendChild(col);
    }

    // HEADER
    Element headerRow = document.createElement("tr");
    headerRow.getStyle().setHeight(headerHeight + "px");
    addHeader(headerRow, "Sunday");
    addHeader(headerRow, "Monday");
    addHeader(headerRow, "Tuesday");
    addHeader(headerRow, "Wednesday");
    addHeader(headerRow, "Thursday");
    addHeader(headerRow, "Friday");
    addHeader(headerRow, "Saturday");
    tableElement.appendChild(headerRow);

    // TABLE BODY
    tableBodyRows.clear();
    Element currentRow = null;

    while (!isNextMonth(date, tmpCurrentDate)) {

      if (currentRow == null || tmpCurrentDate.getDay() == 0) {
        currentRow = document.createElement("tr");
        tableBodyRows.add(currentRow);
        tableElement.appendChild(currentRow);
      }

      currentRow.appendChild(createCalendarDay(new Date(tmpCurrentDate.getTime()), tmpCurrentDate.getMonth() == month).getCell());
      tmpCurrentDate.setDate(tmpCurrentDate.getDate() + 1);
    }

    while (tmpCurrentDate.getDay() != 0) {
      if (currentRow == null) return; // should never append
      currentRow.appendChild(createCalendarDay(new Date(tmpCurrentDate.getTime()), false).getCell());
      tmpCurrentDate.setDate(tmpCurrentDate.getDate() + 1);
    }

    mouseEventCreator.setCells(days);

    applySize();
  }

  private boolean isNextMonth(Date date, Date currentDate) {
    if (currentDate.getYear() > date.getYear()) {
      return true;
    } else if (currentDate.getYear() >= date.getYear() && currentDate.getMonth() > date.getMonth()) {
      return true;
    }
    return false;
  }

  private void addHeader(Element headerRow, String label) {
    Element header = document.createElement("th");
    header.setTextContent(label);
    headerRow.appendChild(header);
  }

  private CalendarDay createCalendarDay(Date date, boolean enabled) {
    CalendarDay dayCell = new CalendarDay(date, enabled);
    dayCell.setEventCreationStart(mouseEventCreator::bind);
    dayCell.setClickHandler(event -> onCreatedEvent(date, date));
    days.add(dayCell);
    return dayCell;
  }

  public Date moveForward() {
    return move(true);
  }

  public Date moveBackward() {
    return move(false);
  }

  public Date move(boolean forward) {
    int fullYear = currentDate.getYear();
    int month = currentDate.getMonth();
    if (forward && month == 11) {
      fullYear = fullYear + 1;
      month = 0;
    } else if (!forward && month == 0) {
      fullYear = fullYear - 1;
      month = 11;
    } else {
      month = month + (forward ? 1 : -1);
    }
    Date newDate = new Date(fullYear, month, 1);
    navigateToDate(newDate);
    return newDate;
  }

  private void onCreatedEvent(Date startDay, Date endDay) {
    Browser.getWindow().alert("Create Event from " + startDay + " to " + endDay);
  }

  private void onCreatingEvent(Date startDay, Date endDay) {
    for (CalendarDay day : days) {
      day.setSelected((day.getCellDate() == startDay || day.getCellDate().after(startDay) &&
          (day.getCellDate() == endDay || day.getCellDate().before(endDay))));
    }
  }

  public Element getElement() {
    return container;
  }
}
