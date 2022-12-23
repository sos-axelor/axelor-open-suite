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

import java.util.HashMap;
import java.util.Map;

import com.axelor.apps.bpm.process.xml.BpmActivity;
import com.axelor.apps.bpm.process.xml.BpmComment;
import com.axelor.apps.bpm.process.xml.BpmEvent;
import com.axelor.apps.bpm.process.xml.BpmGateway;
import com.axelor.apps.bpm.process.xml.BpmProcess;

public class BpmElementMapGenerator {
	
	private Map<String, Object> elementMap = new HashMap<String, Object>();
	
	public Map<String, Object> generate(BpmProcess bpmProcess) {
		
		elementMap.put(bpmProcess.getName(), bpmProcess);
		addEvents(bpmProcess);
		addGateways(bpmProcess);
		addActivities(bpmProcess);
		addComments(bpmProcess);
		
		return elementMap;
	}

	private void addEvents(BpmProcess bpmProcess) {
		
		if (bpmProcess.getEvents() == null) {
			return;
		}
		
		for (BpmEvent bpmEvent : bpmProcess.getEvents()) {
			elementMap.put(bpmEvent.getName(), bpmEvent);
		}
		
	}
	
	private void addGateways(BpmProcess bpmProcess) {
		
		if (bpmProcess.getGateways() == null) {
			return;
		}
		
		for (BpmGateway bpmGateway : bpmProcess.getGateways()) {
			elementMap.put(bpmGateway.getName(), bpmGateway);
		}
		
	}
	
	private void addActivities(BpmProcess bpmProcess) {
		
		if (bpmProcess.getActivities() == null) {
			return;
		}
		
		for (BpmActivity bpmActivity : bpmProcess.getActivities()) {
			elementMap.put(bpmActivity.getName(), bpmActivity);
		}
		
	}
	
	private void addComments(BpmProcess bpmProcess) {
		
		if (bpmProcess.getComments() == null) {
			return;
		}
		
		for (BpmComment bpmComment : bpmProcess.getComments()) {
			elementMap.put(bpmComment.getName(), bpmComment);
		}
		
	}
}
