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
package com.axelor.apps.bpm.db.repo;

import com.axelor.apps.bpm.db.Survey;
import com.axelor.apps.bpm.db.SurveyLine;
import com.axelor.inject.Beans;
import com.axelor.meta.db.MetaView;
import com.axelor.meta.db.repo.MetaJsonModelRepository;
import com.axelor.meta.db.repo.MetaViewRepository;

public class BpmSurveyRepository extends SurveyRepository {

  @Override
  public Survey save(Survey survey) {

    if (survey.getSurveyLineList() == null) {
      return super.save(survey);
    }

    MetaJsonModelRepository metaJsonModelRepository = Beans.get(MetaJsonModelRepository.class);
    MetaViewRepository metaViewRepository = Beans.get(MetaViewRepository.class);

    for (SurveyLine surveyLine : survey.getSurveyLineList()) {
      if (surveyLine.getMetaJsonModel() != null) {
        surveyLine.getMetaJsonModel().setFormWidth("large");
        metaJsonModelRepository.save(surveyLine.getMetaJsonModel());
        MetaView metaView =
            metaViewRepository.findByName(
                "custom-model-" + surveyLine.getMetaJsonModel().getName() + "-form");
        if (metaView != null) {
          String xml = metaView.getXml();

          if (!xml.contains("<button name=\"_submit\"")) {
            xml =
                xml.replace(
                    "</panel>",
                    "<button name=\"_submit\" title=\"Submit\" onClick=\"save,action-survey-response-method-submit-survey-form\" colSpan=\"3\" hideIf=\"$popup()\" /></panel>");
            metaView.setXml(xml);
            metaViewRepository.save(metaView);
          }
        }
      }
    }

    return super.save(survey);
  }
}
