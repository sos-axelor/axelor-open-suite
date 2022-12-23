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

import org.camunda.bpm.model.bpmn.builder.UserTaskBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axelor.apps.bpm.process.xml.BpmActivity;

public class BpmActivityParser {
	
	private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public Object parse(Object builder, BpmActivity bpmActivity) throws ReflectiveOperationException {
		
		String type = bpmActivity.getType();
		
		if (type == null) {
			type = "user" ;
		}
		
		switch(type) {
		case "user":
			Method method = builder.getClass().getMethod("userTask");
			UserTaskBuilder userTaskBuilder = (UserTaskBuilder) method.invoke(builder);
			userTaskBuilder.id(bpmActivity.getName());
			builder = userTaskBuilder;
			break;
		}
		
		return builder;
	}
}
