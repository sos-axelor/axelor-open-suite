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

import com.axelor.apps.base.db.AppBpm;
import com.axelor.apps.base.db.repo.AppBpmRepository;
import com.axelor.auth.AuthUtils;
import com.axelor.auth.db.User;
import com.axelor.common.StringUtils;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.studio.exception.IExceptionMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/superset")
public class SupersetRequestHandler {

  @GET
  @Path("/open")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response openSuperSet() {

    try {

      AppBpm appBpm = Beans.get(AppBpmRepository.class).all().fetchOne();
      String supsersetUrl = appBpm.getSupersetUrl();

      if (StringUtils.isEmpty(supsersetUrl)) {
        return Response.serverError()
            .type(MediaType.TEXT_HTML_TYPE)
            .entity(I18n.get(IExceptionMessage.NO_SUPERSET_URL))
            .build();
      }

      User currentUser = AuthUtils.getUser();
      String supersetUser = currentUser.getSupersetUsername();
      String supersetPwd = currentUser.getSupersetPassword();

      if (StringUtils.isEmpty(supersetUser) || StringUtils.isEmpty(supersetPwd)) {
        return Response.serverError()
            .type(MediaType.TEXT_HTML_TYPE)
            .entity(I18n.get(IExceptionMessage.NO_SUPERSET_USER))
            .build();
      }

      Form form = new Form();
      form.param("username", supersetUser);
      form.param("password", supersetPwd);

      Response response1 =
          ClientBuilder.newClient()
              .target(supsersetUrl + "/login/")
              .request()
              .header("Connection", "keep-alive")
              .get();

      String sessionId = response1.getStringHeaders().getFirst("Set-Cookie");

      Response response =
          ClientBuilder.newClient()
              .target(supsersetUrl + "/login/")
              .request()
              .cookie(
                  "session",
                  sessionId.substring(sessionId.indexOf("=") + 1, sessionId.indexOf(";")))
              .post(Entity.form(form), Response.class);

      if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
        return Response.serverError()
            .type(MediaType.TEXT_HTML_TYPE)
            .entity(I18n.get(IExceptionMessage.INVALID_SUPERSET_USER))
            .build();
      }

      return response;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return Response.serverError().build();
  }
}
