package org.cyk.system.uniwaxgiftcard.ui.web.primefaces;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;

import org.cyk.system.company.business.impl.CompanyBusinessLayer;
import org.cyk.system.company.model.CompanyConstant;
import org.cyk.system.company.model.payment.CashRegister;
import org.cyk.system.company.model.sale.SalableProduct;
import org.cyk.system.company.model.sale.SalableProductInstance;
import org.cyk.system.company.model.sale.Sale;
import org.cyk.system.company.model.sale.SaleCashRegisterMovement;
import org.cyk.system.company.ui.web.primefaces.GiftCardSystemMenuBuilder;
import org.cyk.system.company.ui.web.primefaces.sale.SaleEditPage;
import org.cyk.system.root.business.impl.RootBusinessLayer;
import org.cyk.system.root.model.mathematics.machine.FiniteStateMachineState;

public class SaleEditPageAdapter extends SaleEditPage.Listener.Adapter.Default implements Serializable {
	private static final long serialVersionUID = -1473884331127075090L;
	@Override
	public Collection<SalableProductInstance> getSalableProductInstances(SalableProduct salableProduct,CashRegister cashRegister) {
		FiniteStateMachineState finiteStateMachineState = RootBusinessLayer.getInstance().getFiniteStateMachineStateBusiness().find(CompanyConstant.GIFT_CARD_WORKFLOW_STATE_RECEIVED);
		return CompanyBusinessLayer.getInstance().getSalableProductInstanceBusiness().findByCollectionByCashRegisterByFiniteStateMachineState(salableProduct,cashRegister, finiteStateMachineState);
	}
	
	@Override
	public BigDecimal getCost(Sale sale) {
		if(GiftCardSystemMenuBuilder.ACTION_SELL_GIFT_CARD.equals(sale.getProcessing().getIdentifier())){
			return super.getCost(sale);
		}else if(GiftCardSystemMenuBuilder.ACTION_USE_GIFT_CARD.equals(sale.getProcessing().getIdentifier())){
			SalableProductInstance salableProductInstance = CompanyBusinessLayer.getInstance().getSalableProductInstanceBusiness()
					.find(sale.getSaleCashRegisterMovements().iterator().next().getCashRegisterMovement().getMovement().getSupportingDocumentIdentifier());
			if(salableProductInstance==null)
				return BigDecimal.ZERO;
			return salableProductInstance.getCollection().getPrice();
		}else
			return super.getCost(sale);
	}
	
	@Override
	public void processSaleCashRegisterMovement(SaleCashRegisterMovement saleCashRegisterMovement) {
		saleCashRegisterMovement.getSale().getCost().setValue(saleCashRegisterMovement.getAmountIn());
	}
}
