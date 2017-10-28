package com.grebesche.cal;

import elemental.client.Browser;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.MouseEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MouseEventCreator {

  private final EventListener mouseMove = this::onMouseMove;
  private final EventListener mouseUp = this::onMouseUp;
  private final List<CalendarDay> days = new ArrayList<>();
  private EventCreated eventCreatedHandler;
  private EventCreating eventCreatingHandler;
  private EventCreatingFinished eventCreatingFinished;

  private Date startDay;
  private Date endDay;

  public void bind(Date startDay) {
    cleanDates();
    this.startDay = startDay;
    Browser.getWindow().addEventListener("mousemove", mouseMove);
    Browser.getWindow().addEventListener("mouseup", mouseUp);
  }

  public void unbind() {
    Browser.getWindow().removeEventListener("mousemove", mouseMove);
    Browser.getWindow().removeEventListener("mouseup", mouseUp);
  }

  public void setEventCreatedHandler(EventCreated eventCreatedHandler) {
    this.eventCreatedHandler = eventCreatedHandler;
  }

  public void setEventCreatingHandler(EventCreating eventCreatingHandler) {
    this.eventCreatingHandler = eventCreatingHandler;
  }

  public void setEventCreatingFinished(EventCreatingFinished eventCreatingFinished) {
    this.eventCreatingFinished = eventCreatingFinished;
  }

  private void onMouseMove(Event event) {
    if (startDay == null) return;
    if (eventCreatingHandler == null) return;
    MouseEvent mouseEvent = (MouseEvent) event;
    for (CalendarDay day : this.days) {
      if (mouseEvent.getTarget() == day.getCell()) {
        endDay = day.getCellDate();
        if (startDay.before(endDay)) {
          eventCreatingHandler.created(startDay, endDay);
        } else {
          eventCreatingHandler.created(endDay, startDay);
        }
        break;
      }
    }
  }

  private void onMouseUp(Event event) {
    if (startDay != null && endDay != null) {
      if (startDay.before(endDay)) {
        eventCreatedHandler.created(startDay, endDay);
      } else {
        eventCreatedHandler.created(endDay, startDay);
      }
    }
    cleanDates();
    unbind();
    if (eventCreatingFinished != null) eventCreatingFinished.finished();
  }

  private void cleanDates() {
    this.startDay = null;
    this.endDay = null;
  }

  public void setCells(List<CalendarDay> days) {
    this.days.clear();
    this.days.addAll(days);
  }

  @FunctionalInterface
  public interface EventCreated {
    void created(Date startDay, Date endDay);
  }

  @FunctionalInterface
  public interface EventCreating {
    void created(Date startDay, Date endDay);
  }

  @FunctionalInterface
  public interface EventCreatingFinished {
    void finished();
  }
}
