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
package com.axelor.apps.bpm.service.survey;

import com.axelor.app.AppSettings;
import com.axelor.apps.base.db.AppBpm;
import com.axelor.apps.base.db.repo.AppBpmRepository;
import com.axelor.apps.bpm.db.SurveyCampaign;
import com.axelor.apps.bpm.db.SurveyResponse;
import com.axelor.apps.bpm.db.repo.SurveyCampaignRepository;
import com.axelor.apps.bpm.db.repo.SurveyResponseRepository;
import com.axelor.apps.bpm.db.repo.WkfModelRepository;
import com.axelor.apps.bpm.db.repo.WkfProcessConfigRepository;
import com.axelor.apps.bpm.exception.IExceptionMessage;
import com.axelor.apps.message.db.EmailAddress;
import com.axelor.apps.message.db.Message;
import com.axelor.apps.message.db.Template;
import com.axelor.apps.message.service.MessageService;
import com.axelor.apps.message.service.TemplateMessageService;
import com.axelor.exception.AxelorException;
import com.axelor.exception.db.repo.TraceBackRepository;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.db.repo.MetaJsonRecordRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.mail.MessagingException;

public class SurveyCampaignServiceImpl implements SurveyCampaignService {

  @Inject protected SurveyCampaignRepository surveyCampaignRepository;

  @Inject protected SurveyResponseRepository surveyResponseRepository;

  @Inject protected TemplateMessageService templateMessageService;

  @Inject protected MessageService messageService;

  @Inject protected MetaJsonRecordRepository metaJsonRecordRepository;

  @Override
  @Transactional
  public void startCampaign(SurveyCampaign surveyCampaign)
      throws AxelorException, ClassNotFoundException, InstantiationException,
          IllegalAccessException, IOException, MessagingException {

    if (surveyCampaign == null || surveyCampaign.getId() == null) {
      return;
    }

    surveyCampaign = surveyCampaignRepository.find(surveyCampaign.getId());

    AppBpm appBpm = Beans.get(AppBpmRepository.class).all().fetchOne();

    if (appBpm.getSurveyEmailTemplate() == null) {
      throw new AxelorException(
          TraceBackRepository.CATEGORY_CONFIGURATION_ERROR,
          I18n.get(IExceptionMessage.NO_SURVEY_EMAIL_TEMPLATE));
    }

    long bpmProcessCount =
        Beans.get(WkfProcessConfigRepository.class)
            .all()
            .filter(
                "self.wkfProcess.wkfModel.statusSelect = ?1 AND self.wkfProcess.wkfModel.isSurvey is true AND self.wkfProcess.wkfModel.survey.id = ?2",
                WkfModelRepository.STATUS_ON_GOING,
                surveyCampaign.getSurvey().getId())
            .count();

    for (EmailAddress emailAddress : surveyCampaign.getSendToEmailAddressSet()) {
      String token = UUID.randomUUID().toString();
      generateEmptyResponse(surveyCampaign, emailAddress.getAddress(), token, bpmProcessCount > 0);
      sendSurveyEmail(surveyCampaign, appBpm.getSurveyEmailTemplate(), emailAddress, token);
    }

    surveyCampaign.setStatusSelect(surveyCampaignRepository.STATUS_RUNNING);

    surveyCampaignRepository.save(surveyCampaign);
  }

  @Transactional
  @Override
  public void generateEmptyResponse(
      SurveyCampaign surveyCampaign, String emailAddress, String token, boolean isBpm) {

    SurveyResponse surveyResponse = new SurveyResponse();
    surveyResponse.setSurveyCampaign(surveyCampaign);
    surveyResponse.setEmail(emailAddress);
    surveyResponse.setToken(token);
    surveyResponse.setIsBpm(isBpm);

    surveyResponseRepository.save(surveyResponse);
  }

  protected void sendSurveyEmail(
      SurveyCampaign surveyCampaign, Template template, EmailAddress emailAddress, String token)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException,
          AxelorException, IOException, MessagingException {

    Message message = templateMessageService.generateMessage(surveyCampaign, template);
    Set<EmailAddress> toEmailIds = new HashSet<>();
    toEmailIds.add(emailAddress);
    message.setToEmailAddressSet(toEmailIds);
    String surveyLink = AppSettings.get().getBaseURL();
    surveyLink += "/ws/survey/" + token;
    String content = message.getContent().replace("{_surveyLink}", surveyLink);
    message.setContent(content);
    messageService.sendByEmail(message);
  }

  @Override
  public long countResponse(
      SurveyCampaign surveyCampaign, String model, boolean completed, boolean partial) {

    long count = 0;

    if (surveyCampaign == null) {
      return count;
    }

    if (model == null) {
      if (completed) {
        count =
            surveyResponseRepository
                .all()
                .filter(
                    "self.surveyCampaign.id = ?1 and self.isCompleted = true ",
                    surveyCampaign.getId())
                .count();
      } else if (partial) {
        count =
            metaJsonRecordRepository
                .all()
                .filter(
                    "self.surveyResponse.surveyCampaign.id = ?1 and (self.isSubmitted = null OR self.isSubmitted is false)",
                    surveyCampaign.getId())
                .count();
      } else {
        count =
            surveyResponseRepository
                .all()
                .filter("self.surveyCampaign.id = ?1", surveyCampaign.getId())
                .count();
      }
    } else {
      if (completed) {
        count =
            metaJsonRecordRepository
                .all()
                .filter(
                    "self.surveyResponse.surveyCampaign.id = ?1 and self.isSubmitted is true and self.jsonModel = ?2)",
                    surveyCampaign.getId(),
                    model)
                .count();
      } else if (partial) {
        count =
            metaJsonRecordRepository
                .all()
                .filter(
                    "self.surveyResponse.surveyCampaign.id = ?1 and (self.isSubmitted = null OR self.isSubmitted is false) and self.jsonModel = ?2",
                    surveyCampaign.getId(),
                    model)
                .count();
      }
    }

    return count;
  }
}
