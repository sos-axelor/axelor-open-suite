/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2022 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or  modify
 * it under the terms of the GNU Affero General Public License, version 3,
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.axelor.apps.bpm.process.xml;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class BpmBaseProcess {

  @XmlAttribute private String name;

  @XmlAttribute private String title;

  @XmlElementWrapper(name = "events")
  @XmlElement(name = "event")
  private List<BpmEvent> events;

  @XmlElementWrapper(name = "gateways")
  @XmlElement(name = "gateway")
  private List<BpmGateway> gateways;
  
  @XmlElementWrapper(name = "activities")
  @XmlElement(name = "activity")
  private List<BpmActivity> activities;
  
  @XmlElementWrapper(name = "comments")
  @XmlElement(name = "comment")
  private List<BpmComment> comments;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<BpmEvent> getEvents() {
    return events;
  }

  public void setEvents(List<BpmEvent> events) {
    this.events = events;
  }

  public List<BpmGateway> getGateways() {
    return gateways;
  }

  public void setGateways(List<BpmGateway> gateways) {
    this.gateways = gateways;
  }

  public List<BpmActivity> getActivities() {
    return activities;
  }

  public void setActivities(List<BpmActivity> activities) {
    this.activities = activities;
  }

  public List<BpmComment> getComments() {
    return comments;
  }

  public void setComments(List<BpmComment> comments) {
    this.comments = comments;
  }
}
