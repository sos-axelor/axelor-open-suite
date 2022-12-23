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
import java.util.Iterator;

import org.camunda.bpm.model.bpmn.builder.EventBasedGatewayBuilder;
import org.camunda.bpm.model.bpmn.builder.ExclusiveGatewayBuilder;
import org.camunda.bpm.model.bpmn.builder.ParallelGatewayBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axelor.apps.bpm.process.xml.BpmBranch;
import com.axelor.apps.bpm.process.xml.BpmGateway;

public class BpmGatewayParser {
	
	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public Object parse(BpmProcessParser processParser, Object builder, BpmGateway bpmGateway) throws ReflectiveOperationException {
		
		String type = bpmGateway.getType();
		
		if (type == null) {
			type = "parallel" ;
		}
		
		switch(type) {
			case "unique":
				builder = parseExclusiveGateway(processParser, builder, bpmGateway);
				break;
			case "event":
				builder = parseEventGateway(processParser, builder, bpmGateway);
			default:
				builder = parseParallelGateway(processParser, builder, bpmGateway);
		}
		
		return builder;
	}

	private Object parseExclusiveGateway(BpmProcessParser processParser, Object builder, BpmGateway bpmGateway)
			throws ReflectiveOperationException {
		
		Method method = builder.getClass().getMethod("exclusiveGateway");
		ExclusiveGatewayBuilder exclusiveGatewayBuilder = (ExclusiveGatewayBuilder) method.invoke(builder);
		exclusiveGatewayBuilder.id(bpmGateway.getName());
		
		Iterator<BpmBranch> branchIterator = bpmGateway.getBranches().iterator();
		
		do {
			BpmBranch bpmBranch = branchIterator.next();
			exclusiveGatewayBuilder.condition("", bpmBranch.getIfCondition());
			log.debug("Parse target of branch {}", bpmBranch.getName());
			builder = processParser.processTarget(exclusiveGatewayBuilder, bpmBranch.getTarget());
			if (branchIterator.hasNext()) {
				Method moveToNode = builder.getClass().getMethod("moveToNode", String.class);
				exclusiveGatewayBuilder = (ExclusiveGatewayBuilder) moveToNode.invoke(builder, bpmGateway.getName());
			}
		}while (branchIterator.hasNext());
		
		return builder;
	}
	
	private Object parseEventGateway(BpmProcessParser processParser, Object builder, BpmGateway bpmGateway)
			throws ReflectiveOperationException {
		
		Method method = builder.getClass().getMethod("eventBasedGateway");
		EventBasedGatewayBuilder eventBasedGatewayBuilder = (EventBasedGatewayBuilder) method.invoke(builder);
		eventBasedGatewayBuilder.id(bpmGateway.getName());
		
		Iterator<BpmBranch> branchIterator = bpmGateway.getBranches().iterator();
		
		do {
			BpmBranch bpmBranch = branchIterator.next();
			eventBasedGatewayBuilder.condition("", bpmBranch.getIfCondition());
			log.debug("Parse target of branch {}", bpmBranch.getName());
			builder = processParser.processTarget(eventBasedGatewayBuilder, bpmBranch.getTarget());
			if (branchIterator.hasNext()) {
				Method moveToNode = builder.getClass().getMethod("moveToNode", String.class);
				eventBasedGatewayBuilder = (EventBasedGatewayBuilder) moveToNode.invoke(builder, bpmGateway.getName());
			}
		}while (branchIterator.hasNext());
		
		return builder;
	}
	
	private Object parseParallelGateway(BpmProcessParser processParser, Object builder, BpmGateway bpmGateway)
			throws ReflectiveOperationException {
		
		Method method = builder.getClass().getMethod("parallelGateway");
		ParallelGatewayBuilder parallelGatewayBuilder = (ParallelGatewayBuilder) method.invoke(builder);
		parallelGatewayBuilder.id(bpmGateway.getName());
		
		Iterator<BpmBranch> branchIterator = bpmGateway.getBranches().iterator();
		
		do {
			BpmBranch bpmBranch = branchIterator.next();
			log.debug("Parse target of branch {}", bpmBranch.getName());
			builder = processParser.processTarget(parallelGatewayBuilder, bpmBranch.getTarget());
			if (branchIterator.hasNext()) {
				Method moveToNode = builder.getClass().getMethod("moveToNode", String.class);
				parallelGatewayBuilder = (ParallelGatewayBuilder) moveToNode.invoke(builder, bpmGateway.getName());
			}
		}while (branchIterator.hasNext());
		
		return builder;
	}
}
