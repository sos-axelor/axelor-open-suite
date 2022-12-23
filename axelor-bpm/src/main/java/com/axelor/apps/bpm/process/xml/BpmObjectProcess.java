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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlRootElement(name = "object-processes")
public class BpmObjectProcess {

  @XmlElement(name = "process")
  private List<BpmProcess> processList;

  @XmlElement(name = "subprocess")
  private List<BpmSubProcess> subProcessList;

  public List<BpmProcess> getProcessList() {
    return processList;
  }

  public void setProcessList(List<BpmProcess> processList) {
    this.processList = processList;
  }

  public List<BpmSubProcess> getSubProcessList() {
    return subProcessList;
  }

  public void setSubProcessList(List<BpmSubProcess> subProcessList) {
    this.subProcessList = subProcessList;
  }
}
