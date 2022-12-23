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
package com.axelor.apps.bpm.process.service;

import com.axelor.apps.bpm.process.service.parser.BpmProcessParser;
import com.axelor.apps.bpm.process.xml.BpmObjectProcess;
import com.axelor.apps.bpm.process.xml.BpmProcess;
import com.google.common.io.Resources;
import java.io.Reader;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

public class BpmProcessTransformer {

  private static final String LOCAL_SCHEMA = "xsd/bpm.xsd";

  public String transform(Reader inReader) {

    try {
      JAXBContext context = JAXBContext.newInstance(BpmObjectProcess.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = schemaFactory.newSchema(Resources.getResource(LOCAL_SCHEMA));
      unmarshaller.setSchema(schema);
      BpmObjectProcess bpmObjectProcess = (BpmObjectProcess) unmarshaller.unmarshal(inReader);
      return transform(bpmObjectProcess);
    } catch (Exception e) {
    	e.printStackTrace();
//      TraceBackService.trace(e);
    }

    return null;
  }

  public String transform(BpmObjectProcess bpmObjectProcess) throws ReflectiveOperationException {
    if (bpmObjectProcess == null) {
      return null;
    }


    if (bpmObjectProcess.getProcessList() == null) {
      return null;
    }

    BpmProcess bpmProcess = bpmObjectProcess.getProcessList().get(0);

    BpmnModelInstance bpmnModelInstance = new BpmProcessParser().parse(bpmProcess);
    
    return Bpmn.convertToString(bpmnModelInstance);
  }

}
