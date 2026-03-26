/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2026 Axelor (<http://axelor.com>).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.axelor.apps.production.rest.dto;

import com.axelor.apps.base.db.Product;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductLeadTimeDto {

  private Product product;
  private BigDecimal deliveryTime;
  private BigDecimal totalDeliveryTime;
  private BigDecimal requiredQty;
  private BigDecimal availableQty;
  private List<ProductLeadTimeDto> childrenLeadTimes = new ArrayList<ProductLeadTimeDto>();

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product proudct) {
    this.product = proudct;
  }

  public BigDecimal getDeliveryTime() {
    return deliveryTime;
  }

  public void setDeliveryTime(BigDecimal deliveryTime) {
    this.deliveryTime = deliveryTime;
  }

  public List<ProductLeadTimeDto> getChildrenLeadTimes() {
    return childrenLeadTimes;
  }

  public void setChildrenLeadTimes(List<ProductLeadTimeDto> childrenLeadTimes) {
    this.childrenLeadTimes = childrenLeadTimes;
  }

  public void addLeadTime(ProductLeadTimeDto childLeadTime) {
    this.childrenLeadTimes.add(childLeadTime);
  }

  public BigDecimal getTotalDeliveryTime() {
    return totalDeliveryTime;
  }

  public void setTotalDeliveryTime(BigDecimal totalDeliveryTime) {
    this.totalDeliveryTime = totalDeliveryTime;
  }

  public BigDecimal getRequiredQty() {
    return requiredQty;
  }

  public void setRequiredQty(BigDecimal requiredQty) {
    this.requiredQty = requiredQty;
  }

  public BigDecimal getAvailableQty() {
    return availableQty;
  }

  public void setAvailableQty(BigDecimal availableQty) {
    this.availableQty = availableQty;
  }
}
