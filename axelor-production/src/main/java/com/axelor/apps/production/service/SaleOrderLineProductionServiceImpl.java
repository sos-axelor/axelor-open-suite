/*
 * Axelor Business Solutions
 *
 * Copyright (C) 2005-2025 Axelor (<http://axelor.com>).
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
package com.axelor.apps.production.service;

import com.axelor.apps.base.AxelorException;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.ProductRepository;
import com.axelor.apps.base.service.exception.TraceBackService;
import com.axelor.apps.production.db.BillOfMaterial;
import com.axelor.apps.production.db.BillOfMaterialLine;
import com.axelor.apps.production.rest.dto.ProductLeadTimeDto;
import com.axelor.apps.sale.db.SaleOrderLine;
import com.axelor.apps.stock.service.StockLocationLineFetchService;
import com.axelor.apps.supplychain.service.app.AppSupplychainService;
import com.axelor.apps.supplychain.service.saleorderline.SaleOrderLineServiceSupplyChainImpl;
import com.axelor.inject.Beans;
import com.google.inject.Inject;
import java.math.BigDecimal;
import java.time.Duration;

public class SaleOrderLineProductionServiceImpl extends SaleOrderLineServiceSupplyChainImpl
    implements SaleOrderLineProductionService {

  @Inject
  public SaleOrderLineProductionServiceImpl(
      AppSupplychainService appSupplychainService,
      StockLocationLineFetchService stockLocationLineFetchService) {
    super(appSupplychainService, stockLocationLineFetchService);
  }

  @Override
  public BigDecimal computeQtyToProduce(SaleOrderLine saleOrderLine, SaleOrderLine parentSol) {
    BigDecimal produceQty = saleOrderLine.getQty();
    if (parentSol != null) {
      produceQty = produceQty.multiply(parentSol.getQtyToProduce());
    }
    return produceQty;
  }

  @Override
  protected BigDecimal computeLeadTime(
      SaleOrderLine saleOrderLine, Product product, BigDecimal solQty) {

    BigDecimal leadTime = super.computeLeadTime(saleOrderLine, product, solQty);
    if (leadTime != null) {
      return leadTime;
    }
    return generateProductLeadTimeDto(product, saleOrderLine, solQty).getTotalDeliveryTime();
  }

  @Override
  public ProductLeadTimeDto generateProductLeadTimeDto(
      Product product, SaleOrderLine saleOrderLine, BigDecimal stockRequired) {
    ProductLeadTimeDto dto = new ProductLeadTimeDto();
    dto.setProduct(product);
    BigDecimal leadTime = BigDecimal.ZERO;
    BigDecimal availableStock = getAvailableStockPerEstimatedDeliveryDate(saleOrderLine, product);
    dto.setRequiredQty(stockRequired);
    dto.setAvailableQty(availableStock);
    if (availableStock.compareTo(stockRequired) > 0) {
      dto.setDeliveryTime(leadTime);
      dto.setTotalDeliveryTime(leadTime);
      return dto;
    }

    stockRequired = stockRequired.subtract(availableStock);
    if (!product
        .getProcurementMethodSelect()
        .equals(ProductRepository.PROCUREMENT_METHOD_PRODUCE)) {
      if (product.getDefaultSupplierPartner() != null) {
        leadTime = BigDecimal.valueOf(product.getSupplierDeliveryTime());
        dto.setTotalDeliveryTime(leadTime);
      }
    } else {
      BillOfMaterial bom = product.getDefaultBillOfMaterial();
      long duration = 0l;
      if (bom.getProdProcess() != null) {
        try {
          duration =
              Beans.get(ProdProcessComputationService.class)
                  .getLeadTime(bom.getProdProcess(), BigDecimal.ONE);
        } catch (AxelorException e) {
          TraceBackService.trace(e);
        }
      }
      leadTime = BigDecimal.valueOf(Duration.ofSeconds(duration).toDays()).multiply(stockRequired);
      BigDecimal maxChildTime = BigDecimal.ZERO;
      for (BillOfMaterialLine line : bom.getBillOfMaterialLineList()) {
        ProductLeadTimeDto child =
            generateProductLeadTimeDto(
                line.getProduct(), saleOrderLine, line.getQty().multiply(stockRequired));
        maxChildTime =
            child.getTotalDeliveryTime().compareTo(maxChildTime) > 0
                ? child.getTotalDeliveryTime()
                : maxChildTime;
        dto.addLeadTime(child);
      }
      dto.setTotalDeliveryTime(maxChildTime.add(leadTime));
    }
    dto.setDeliveryTime(leadTime);
    return dto;
  }
}
