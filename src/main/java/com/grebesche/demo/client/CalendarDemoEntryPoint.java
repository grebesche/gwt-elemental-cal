package com.grebesche.demo.client;

import com.google.gwt.core.client.EntryPoint;
import com.grebesche.cal.Calendar;
import elemental.client.Browser;
import elemental.dom.Document;
import elemental.html.ButtonElement;
import elemental.html.DivElement;

import java.util.Date;

public class CalendarDemoEntryPoint implements EntryPoint {

  public void onModuleLoad() {

    Date today = new Date();

    Document document = Browser.getDocument();
    DivElement divElement = document.createDivElement();
    divElement.setTextContent(today.toString());
    document.getBody().appendChild(divElement);

    Calendar calendar = new Calendar();
    calendar.setSize(500, 800);
    calendar.navigateToDate(today);

    ButtonElement backward = document.createButtonElement();
    backward.setTextContent("<");
    backward.addEventListener("click", event -> divElement.setTextContent(calendar.moveBackward().toString()));
    document.getBody().appendChild(backward);

    ButtonElement forward = document.createButtonElement();
    forward.setTextContent(">");
    forward.addEventListener("click", event -> divElement.setTextContent(calendar.moveForward().toString()));
    document.getBody().appendChild(forward);

    ButtonElement todayButton = document.createButtonElement();
    todayButton.setTextContent("today");
    todayButton.addEventListener("click", event -> {
      divElement.setTextContent(today.toString());
      calendar.navigateToDate(today);
    });
    document.getBody().appendChild(todayButton);

    document.getBody().appendChild(calendar.getElement());
  }
}
