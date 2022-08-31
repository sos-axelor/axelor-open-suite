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

import com.axelor.apps.bpm.db.SurveyCampaign;
import com.axelor.exception.AxelorException;
import java.io.IOException;
import javax.mail.MessagingException;

public interface SurveyCampaignService {

  public void startCampaign(SurveyCampaign surveyCampaign)
      throws AxelorException, ClassNotFoundException, InstantiationException,
          IllegalAccessException, IOException, MessagingException;

  public void generateEmptyResponse(
      SurveyCampaign surveyCampaign, String emailAddress, String token, boolean isBpm);

  public long countResponse(
      SurveyCampaign surveyCampaign, String model, boolean completed, boolean partial);
}
