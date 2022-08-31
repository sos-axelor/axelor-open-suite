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

import com.axelor.apps.bpm.db.SurveyResponse;
import com.axelor.apps.bpm.db.repo.SurveyCampaignRepository;
import com.axelor.apps.bpm.exception.IExceptionMessage;
import com.axelor.apps.bpm.service.survey.SurveyResponseService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaJsonModel;
import com.axelor.meta.db.MetaJsonRecord;
import com.axelor.meta.db.repo.MetaJsonModelRepository;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class SurveyResponseController {

  public void openSurveyForm(ActionRequest request, ActionResponse response) {

    SurveyResponse surveyResponse = request.getContext().asType(SurveyResponse.class);

    if (surveyResponse.getSurveyCampaign().getStatusSelect()
        == SurveyCampaignRepository.STATUS_CLOSED) {
      response.setFlash(I18n.get(IExceptionMessage.SURVEY_CAMPAIGN_CLOSED));
      response.setCanClose(true);
    } else {
      openNextForm(response, surveyResponse);
    }
  }

  private void openNextForm(ActionResponse response, SurveyResponse surveyResponse) {

    SurveyResponseService surveyResponseService = Beans.get(SurveyResponseService.class);
    MetaJsonRecord metaJsonRecord = surveyResponseService.getNextForm(surveyResponse);

    if (metaJsonRecord == null) {
      surveyResponseService.completeResponse(surveyResponse);
      response.setFlash(I18n.get(IExceptionMessage.SURVEY_RESPONSE_COMPLETED));
    } else {
      MetaJsonModel jsonModel =
          Beans.get(MetaJsonModelRepository.class).findByName(metaJsonRecord.getJsonModel());
      response.setView(
          ActionView.define(jsonModel.getTitle())
              .model(MetaJsonRecord.class.getName())
              .add("form", "custom-model-" + jsonModel.getName() + "-form")
              .param("show-toolbar", "false")
              .param("forceEdit", "true")
              .context("jsonModel", jsonModel.getName())
              .context("_showRecord", metaJsonRecord.getId())
              .map());
    }
    response.setCanClose(true);
  }

  public void submitSurveyForm(ActionRequest request, ActionResponse response) {

    MetaJsonRecord metaJsonRecord = request.getContext().asType(MetaJsonRecord.class);

    Beans.get(SurveyResponseService.class).submitForm(metaJsonRecord);

    openNextForm(response, metaJsonRecord.getSurveyResponse());
  }

  public void setSurveyFormList(ActionRequest request, ActionResponse response) {

    SurveyResponse surveyResponse = request.getContext().asType(SurveyResponse.class);
  }
}
