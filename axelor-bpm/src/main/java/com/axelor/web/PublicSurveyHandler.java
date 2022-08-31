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
package com.axelor.web;

import com.axelor.app.AppSettings;
import com.axelor.apps.bpm.db.SurveyResponse;
import com.axelor.apps.bpm.db.repo.SurveyCampaignRepository;
import com.axelor.apps.bpm.db.repo.SurveyResponseRepository;
import com.axelor.apps.bpm.exception.IExceptionMessage;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

@Path("/public")
public class PublicSurveyHandler {

  @GET
  @Path("/survey/{token}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response openSurvey(@PathParam("token") String token) throws URISyntaxException {

    String redirectUrl = AppSettings.get().getBaseURL();

    Map<String, String> json = new HashMap<String, String>();
    json.put("username", "admin");
    json.put("password", "admin");

    Response response =
        ClientBuilder.newClient()
            .target(new URI(redirectUrl + "/callback"))
            .request()
            .post(Entity.json(json), Response.class);
    if (token != null) {
      SurveyResponse surveyResponse =
          Beans.get(SurveyResponseRepository.class)
              .all()
              .filter("self.token = ?1", token)
              .fetchOne();

      if (surveyResponse != null) {
        if (surveyResponse.getSurveyCampaign().getStatusSelect()
            == SurveyCampaignRepository.STATUS_CLOSED) {
          return Response.serverError()
              .entity(I18n.get(IExceptionMessage.SURVEY_CAMPAIGN_CLOSED))
              .build();
        } else {
          redirectUrl += "/#/ds/survey.response.all/edit/" + surveyResponse.getId();
        }
      }
    }

    List<NewCookie> cookies = new ArrayList<NewCookie>(response.getCookies().values());

    return Response.temporaryRedirect(new URI(redirectUrl))
        .cookie(cookies.toArray(new NewCookie[0]))
        .build();
  }
}
