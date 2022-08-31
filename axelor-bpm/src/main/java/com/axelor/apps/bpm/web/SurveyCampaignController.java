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
package com.axelor.apps.bpm.web;

import com.axelor.apps.bpm.db.Survey;
import com.axelor.apps.bpm.db.SurveyCampaign;
import com.axelor.apps.bpm.db.SurveyLine;
import com.axelor.apps.bpm.db.repo.SurveyCampaignRepository;
import com.axelor.apps.bpm.db.repo.SurveyRepository;
import com.axelor.apps.bpm.service.survey.SurveyCampaignService;
import com.axelor.exception.service.TraceBackService;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaJsonRecord;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurveyCampaignController {

  public void startCampaign(ActionRequest request, ActionResponse response) {

    try {
      SurveyCampaign surveyCampaign = request.getContext().asType(SurveyCampaign.class);

      Beans.get(SurveyCampaignService.class).startCampaign(surveyCampaign);

      response.setCanClose(true);
    } catch (Exception e) {
      TraceBackService.trace(response, e);
    }
  }

  public void closeCampaign(ActionRequest request, ActionResponse response) {

    response.setValue("statusSelect", SurveyCampaignRepository.STATUS_CLOSED);
  }

  public void setCampaignSurveyFormList(ActionRequest request, ActionResponse response) {

    SurveyCampaign suryCampaign = request.getContext().asType(SurveyCampaign.class);

    Survey survey = suryCampaign.getSurvey();

    if (survey == null) {
      response.setValue("$surveyFormList", null);
    } else {
      survey = Beans.get(SurveyRepository.class).find(survey.getId());

      SurveyCampaignService surveyCampaignService = Beans.get(SurveyCampaignService.class);

      long totalSent = surveyCampaignService.countResponse(suryCampaign, null, false, false);
      long totalCompleted = surveyCampaignService.countResponse(suryCampaign, null, true, false);
      long totalPartiallyCompleted =
          surveyCampaignService.countResponse(suryCampaign, null, false, true);
      long totalNotCompleted = totalSent - totalCompleted - totalPartiallyCompleted;

      List<Object> formList = new ArrayList<>();
      for (SurveyLine surveyLine : survey.getSurveyLineList()) {
        Map<String, Object> record = new HashMap<>();
        String jsonModel = surveyLine.getMetaJsonModel().getName();
        record.put("jsonModel", jsonModel);
        long completed = surveyCampaignService.countResponse(suryCampaign, jsonModel, true, false);
        long partial = surveyCampaignService.countResponse(suryCampaign, jsonModel, false, true);
        record.put("totalCompleted", completed);
        record.put("totalPartiallyCompleted", partial);
        record.put("totalNotCompleted", totalSent - completed - partial);
        formList.add(record);
      }
      response.setValue("$totalSent", totalSent);
      response.setValue("$totalCompleted", totalCompleted);
      response.setValue("$totalPartiallyCompleted", totalPartiallyCompleted);
      response.setValue("$totalNotCompleted", totalNotCompleted);
      response.setValue("$surveyFormList", formList);
    }
  }

  public void openForm(ActionRequest request, ActionResponse response) {

    MetaJsonRecord metaJsonRecord = request.getContext().asType(MetaJsonRecord.class);

    String jsonModel = metaJsonRecord.getJsonModel();

    response.setView(
        ActionView.define(jsonModel)
            .model(MetaJsonRecord.class.getName())
            .add("form", "custom-model-" + jsonModel + "-form")
            .param("popup", "true")
            .param("show-toolbar", "false")
            .param("popup-save", "false")
            .param("show-confirm", "false")
            .context("jsonModel", jsonModel)
            .map());
  }
}
