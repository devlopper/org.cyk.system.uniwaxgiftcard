package org.cyk.system.uniwaxgiftcard.ui.web.primefaces;

import java.io.Serializable;
import java.util.Collection;

import org.cyk.system.company.business.impl.CompanyBusinessLayer;
import org.cyk.system.company.business.impl.sale.SaleBusinessImpl;
import org.cyk.system.company.model.CompanyConstant;
import org.cyk.system.company.model.sale.SalableProductInstance;
import org.cyk.system.company.model.sale.SalableProductInstanceCashRegister;
import org.cyk.system.company.model.sale.Sale;
import org.cyk.system.company.ui.web.primefaces.GiftCardSystemMenuBuilder;
import org.cyk.system.root.business.impl.RootBusinessLayer;
import org.cyk.system.root.business.impl.validation.ExceptionUtils;

public class SaleBusinessAdapter extends SaleBusinessImpl.Listener.Adapter implements Serializable {
	private static final long serialVersionUID = 6995530308698881653L;
	
	@Override
	public void beforeCreate(Sale sale) {
		if(GiftCardSystemMenuBuilder.ACTION_SELL_GIFT_CARD.equals(sale.getProcessing().getIdentifier())){
			
		}else if(GiftCardSystemMenuBuilder.ACTION_USE_GIFT_CARD.equals(sale.getProcessing().getIdentifier())){
			ExceptionUtils.getInstance().exception(sale.getSaleCashRegisterMovements().isEmpty(), "exception.giftacrdinstancerequired");
			String giftCardIdentifier = sale.getSaleCashRegisterMovements().iterator().next().getCashRegisterMovement().getMovement().getSupportingDocumentIdentifier();
			SalableProductInstance salableProductInstance = CompanyBusinessLayer.getInstance().getSalableProductInstanceDao().read(giftCardIdentifier);
			ExceptionUtils.getInstance().exception(salableProductInstance ==null, "exception.giftacrdinstancedoesnotexist");
			Collection<SalableProductInstanceCashRegister> salableProductInstanceCashRegisters = CompanyBusinessLayer.getInstance()
					.getSalableProductInstanceCashRegisterDao().readBySalableProductInstanceByFiniteStateMachineState(salableProductInstance,
							RootBusinessLayer.getInstance().getFiniteStateMachineStateDao().read(CompanyConstant.GIFT_CARD_WORKFLOW_STATE_SOLD));
			
			//No product selling while using gift card
			//while( !sale.getSaleProducts().isEmpty()  )
			//	CompanyBusinessLayer.getInstance().getSaleBusiness().unselectProduct(sale, sale.getSaleProducts().iterator().next());
			sale.getSaleProducts().clear();
			
			//change state to used
			
			
			for(SalableProductInstanceCashRegister salableProductInstanceCashRegister : salableProductInstanceCashRegisters){
				salableProductInstanceCashRegister.setFiniteStateMachineState(RootBusinessLayer.getInstance().getFiniteStateMachineStateDao().read(CompanyConstant.GIFT_CARD_WORKFLOW_STATE_USED));
				salableProductInstanceCashRegister.getFiniteStateMachineState().getProcessing().setParty(sale.getProcessing().getParty());
			}
			CompanyBusinessLayer.getInstance().getSalableProductInstanceCashRegisterBusiness().update(salableProductInstanceCashRegisters);
		}
	}
	@Override
	public Boolean isReportUpdatable(Sale sale) {
		return Boolean.FALSE;
	}
}
