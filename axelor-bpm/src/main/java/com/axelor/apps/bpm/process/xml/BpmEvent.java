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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class BpmEvent {

  @XmlAttribute private String name;

  @XmlAttribute private String title;

  @XmlAttribute private String type;

  @XmlAttribute private String target;

  @XmlAttribute private String interruptible;

  @XmlAttribute private String trigger;

  @XmlAttribute private String attachedTo;

  @XmlAttribute private String onEnable;

  @XmlAttribute private BPM_EVENT_FAMILY family;

  @XmlAttribute private Boolean interruptive;

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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public String getInterruptible() {
    return interruptible;
  }

  public void setInterruptible(String interruptible) {
    this.interruptible = interruptible;
  }

  public String getTrigger() {
    return trigger;
  }

  public void setTrigger(String trigger) {
    this.trigger = trigger;
  }

  public String getAttachedTo() {
    return attachedTo;
  }

  public void setAttachedTo(String attachedTo) {
    this.attachedTo = attachedTo;
  }

  public String getOnEnable() {
    return onEnable;
  }

  public void setOnEnable(String onEnable) {
    this.onEnable = onEnable;
  }

  public BPM_EVENT_FAMILY getFamily() {
    return family;
  }

  public void setFamily(BPM_EVENT_FAMILY family) {
    this.family = family;
  }

  public Boolean getInterruptive() {
    return interruptive;
  }

  public void setInterruptive(Boolean interruptive) {
    this.interruptive = interruptive;
  }

  public enum BPM_EVENT_FAMILY {
    start,
    end,
    intermediate;
  }
}
