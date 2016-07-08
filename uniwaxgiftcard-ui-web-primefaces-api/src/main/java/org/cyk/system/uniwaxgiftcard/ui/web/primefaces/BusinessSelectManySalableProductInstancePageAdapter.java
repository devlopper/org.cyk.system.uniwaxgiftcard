package org.cyk.system.uniwaxgiftcard.ui.web.primefaces;

import java.io.Serializable;

import org.cyk.system.company.model.sale.SalableProductInstanceCashRegister;
import org.cyk.system.root.model.mathematics.machine.FiniteStateMachineAlphabet;
import org.cyk.ui.web.api.WebManager;
import org.cyk.ui.web.primefaces.page.AbstractSelectManyPage;
import org.cyk.ui.web.primefaces.page.SelectManyPage;
import org.cyk.utility.common.cdi.AbstractBean;

public class BusinessSelectManySalableProductInstancePageAdapter extends AbstractSelectManyPage.Listener.Adapter<SalableProductInstanceCashRegister,Long> implements Serializable {
	private static final long serialVersionUID = 5004002889129536583L;
	
	public BusinessSelectManySalableProductInstancePageAdapter() {
		super(SalableProductInstanceCashRegister.class);
	}
	
	@Override
	public void afterInitialisationEnded(AbstractBean bean) {
		super.afterInitialisationEnded(bean);
		((SelectManyPage)bean).setContentTitle("Sélection des cartes cadeaux à "
				+((FiniteStateMachineAlphabet)WebManager.getInstance().getIdentifiableFromRequestParameter(FiniteStateMachineAlphabet.class,Boolean.TRUE)).getName());
	}

}
