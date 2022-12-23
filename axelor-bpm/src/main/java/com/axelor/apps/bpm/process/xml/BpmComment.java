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
public class BpmComment {

  @XmlAttribute private String name;

  @XmlElement(name = "to")
  private List<BpmToTarget> toTargets;

  @XmlElement(name = "help")
  private String help;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<BpmToTarget> getToTargets() {
    return toTargets;
  }

  public void setToTargets(List<BpmToTarget> toTargets) {
    this.toTargets = toTargets;
  }

  public String getHelp() {
    return help;
  }

  public void setHelp(String help) {
    this.help = help;
  }
}
