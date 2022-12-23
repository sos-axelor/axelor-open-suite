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
import javax.xml.bind.annotation.XmlType;

@XmlType
public class BpmActivity {

  @XmlAttribute private String name;

  @XmlAttribute private String help;

  @XmlAttribute(name = "if")
  private String ifCondition;

  @XmlAttribute private String onEnable;

  @XmlAttribute private String target;

  @XmlAttribute private String type;

  @XmlAttribute private String title;

  @XmlAttribute private String model;

  @XmlAttribute private String form;

  @XmlElement(name = "button")
  private List<BpmButton> buttons;

  @XmlElement(name = "dmn")
  private List<BpmDmn> dmns;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHelp() {
    return help;
  }

  public void setHelp(String help) {
    this.help = help;
  }

  public String getIfCondition() {
    return ifCondition;
  }

  public void setIfCondition(String ifCondition) {
    this.ifCondition = ifCondition;
  }

  public String getOnEnable() {
    return onEnable;
  }

  public void setOnEnable(String onEnable) {
    this.onEnable = onEnable;
  }

  public String getTarget() {
    return target;
  }

  public void setTarget(String target) {
    this.target = target;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getModel() {
    return model;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getForm() {
    return form;
  }

  public void setForm(String form) {
    this.form = form;
  }

  public List<BpmButton> getButtons() {
    return buttons;
  }

  public void setButtons(List<BpmButton> buttons) {
    this.buttons = buttons;
  }

  public List<BpmDmn> getDmns() {
    return dmns;
  }

  public void setDmns(List<BpmDmn> dmns) {
    this.dmns = dmns;
  }
}
