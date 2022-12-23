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
package com.axelor.apps.bpm.process.service.parser;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Map;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axelor.apps.bpm.process.service.BpmElementMapGenerator;
import com.axelor.apps.bpm.process.xml.BpmActivity;
import com.axelor.apps.bpm.process.xml.BpmDmn;
import com.axelor.apps.bpm.process.xml.BpmEvent;
import com.axelor.apps.bpm.process.xml.BpmGateway;
import com.axelor.apps.bpm.process.xml.BpmEvent.BPM_EVENT_FAMILY;
import com.axelor.apps.bpm.process.xml.BpmProcess;
import com.axelor.apps.bpm.process.xml.BpmSubProcess;

public class BpmProcessParser {
	
	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private Map<String, Object> elementMap;
	
	public BpmnModelInstance parse(BpmProcess bpmProcess) throws ReflectiveOperationException {
		
	    ProcessBuilder processBuilder =
	        Bpmn.createExecutableProcess();
	    processBuilder.name(bpmProcess.getTitle());
	    processBuilder.id(bpmProcess.getName());
	    
	    elementMap = new BpmElementMapGenerator().generate(bpmProcess);
	    
	    if (bpmProcess.getEvents() == null) {
		    return processBuilder.done();
	    }

	    for (BpmEvent bpmEvent : bpmProcess.getEvents()) {
	      if (bpmEvent.getFamily() == BPM_EVENT_FAMILY.start) {
	         Object builder = processBuilder.startEvent().name(bpmEvent.getTitle()).id(bpmEvent.getName());
	         builder = processTarget(builder, bpmEvent.getTarget());
	         Method method = builder.getClass().getMethod("done");
	         return (BpmnModelInstance) method.invoke(builder);
	      }
	    }
		    
		return processBuilder.done();
	}
	
	protected Object processTarget(Object builder, String target) throws ReflectiveOperationException {
		
		if (target == null) {
			log.debug("No target element");
			return builder;
		}
		
		Object targetElement = elementMap.get(target);
		
		if (targetElement == null) {
			log.debug("No target element found for the id {}", target);
			return builder;
		}
		elementMap.remove(target);
		
		Method method = this.getClass().getDeclaredMethod("parse" + targetElement.getClass().getSimpleName(), Object.class, targetElement.getClass());
		builder = method.invoke(this, builder, targetElement);
		
		return builder;
	    
	}

	protected Object parseBpmActivity(Object builder, BpmActivity bpmActivity) throws ReflectiveOperationException {
		builder = new BpmActivityParser().parse(builder, bpmActivity);
		log.debug("Parse target of activity {}", bpmActivity.getName());
		return processTarget(builder, bpmActivity.getTarget());
		
	}
	
	protected Object parseBpmEvent(Object builder, BpmEvent bpmEvent) throws ReflectiveOperationException {
		log.debug("Parse target of event {}", bpmEvent.getName());
		builder = new BpmEventParser().parse(builder, bpmEvent);
		return processTarget(builder, bpmEvent.getTarget());
	}
	
	protected Object parseBpmGateway(Object builder, BpmGateway bpmGateway) throws ReflectiveOperationException {
		builder = new BpmGatewayParser().parse(this, builder, bpmGateway);
		return builder;
	}
	
	protected void parseBpmSubProcess(Object builder, BpmSubProcess bpmSubProcess) {
	}
	
	protected void parseBpmDmn(Object builder, BpmDmn bpmDmn) {
	}
	
}
