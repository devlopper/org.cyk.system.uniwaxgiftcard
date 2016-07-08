package org.cyk.system.uniwaxgiftcard.ui.web.primefaces;

import java.io.Serializable;

import org.cyk.system.company.model.sale.SalableProductInstanceCashRegister;
import org.cyk.system.company.ui.web.primefaces.sale.SalableProductInstanceCashRegisterQueryManyFormModel;
import org.cyk.system.root.model.mathematics.machine.FiniteStateMachineState;
import org.cyk.ui.web.api.WebManager;
import org.cyk.ui.web.primefaces.page.AbstractProcessManyPage;

public class BusinessProcessManySalableProductInstancePageAdapter extends AbstractProcessManyPage.Listener.Adapter<SalableProductInstanceCashRegister,Long> implements Serializable {
	private static final long serialVersionUID = 5004002889129536583L;
	
	public BusinessProcessManySalableProductInstancePageAdapter() {
		super(SalableProductInstanceCashRegister.class);
	}
	
	@Override
	public String getContentTitle(AbstractProcessManyPage<?> page,String actionIdentifier) {
		return "Confirmation de la sélection des cartes cadeaux devant être "
				+((FiniteStateMachineState)WebManager.getInstance().getIdentifiableFromRequestParameter(FiniteStateMachineState.class,Boolean.TRUE)).getName();
	}
	
	@Override
	public Class<?> getFormModelClass() {
		return SalableProductInstanceCashRegisterQueryManyFormModel.ProcessPageAdapter.Form.class;
	}
	
	@Override
	public Boolean getShowForm(AbstractProcessManyPage<?> processManyPage,String actionIdentifier) {
		return Boolean.TRUE;
	}
}
