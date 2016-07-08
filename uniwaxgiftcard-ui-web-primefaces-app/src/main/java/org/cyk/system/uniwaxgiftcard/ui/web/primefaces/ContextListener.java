package org.cyk.system.uniwaxgiftcard.ui.web.primefaces;

import java.io.Serializable;
import java.util.Locale;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import org.cyk.system.company.business.impl.sale.SaleBusinessImpl;
import org.cyk.system.company.business.impl.sale.SaleProductInstanceBusinessImpl;
import org.cyk.system.company.model.sale.SalableProduct;
import org.cyk.system.company.ui.web.primefaces.AbstractCompanyContextListener;
import org.cyk.system.company.ui.web.primefaces.CompanyWebManager;
import org.cyk.system.company.ui.web.primefaces.sale.SalableProductEditPage;
import org.cyk.system.company.ui.web.primefaces.sale.SalableProductInstanceEditPage;
import org.cyk.system.company.ui.web.primefaces.sale.SaleEditPage;
import org.cyk.system.root.business.impl.language.LanguageBusinessImpl;
import org.cyk.system.root.business.impl.mathematics.machine.FiniteStateMachineStateLogBusinessImpl;
import org.cyk.system.root.ui.web.primefaces.api.RootWebManager;
import org.cyk.ui.web.primefaces.page.AbstractBusinessEntityFormOnePage;
import org.cyk.ui.web.primefaces.page.AbstractProcessManyPage;
import org.cyk.ui.web.primefaces.page.AbstractSelectManyPage;
import org.cyk.utility.common.helper.StringHelper.CaseType;

@WebListener
public class ContextListener extends AbstractCompanyContextListener implements Serializable {

	private static final long serialVersionUID = -9042005596731665575L;
	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
			
		LanguageBusinessImpl.cache(Locale.FRENCH, "model.entity.product", null, CaseType.FURL, "carte cadeau");
		LanguageBusinessImpl.cache(Locale.FRENCH, "model.entity.product.many", null, CaseType.FURL, "cartes cadeau");
		
		LanguageBusinessImpl.cache(Locale.FRENCH, "model.entity.salableProduct", null, CaseType.FURL, "carte cadeau");
		LanguageBusinessImpl.cache(Locale.FRENCH, "model.entity.salableProduct.many", null, CaseType.FURL, "cartes cadeau");
		
		LanguageBusinessImpl.cache(Locale.FRENCH, "model.entity.salableProductInstance", null, CaseType.FURL, "unité de carte cadeau");
		LanguageBusinessImpl.cache(Locale.FRENCH, "model.entity.salableProductInstance.many", null, CaseType.FURL, "unités de carte cadeau");
		
		LanguageBusinessImpl.cache(Locale.FRENCH, "model.entity.salableProductInstanceCashRegister", null, CaseType.FURL, "association d'unité de carte cadeau et vendeur");
		LanguageBusinessImpl.cache(Locale.FRENCH, "model.entity.salableProductInstanceCashRegister.many", null, CaseType.FURL, "associations d'unité de carte cadeau et vendeur");
		
		super.contextInitialized(event);
		
		RootWebManager.getInstance().setAutoAddToSystemMenu(Boolean.FALSE);
		CompanyWebManager.getInstance().setAutoAddToSystemMenu(Boolean.FALSE);
		
		uiManager.registerApplicationUImanager(RootWebManager.getInstance());
		uiManager.registerApplicationUImanager(CompanyWebManager.getInstance());
		uiManager.registerApplicationUImanager(UniwaxGiftCardWebManager.getInstance());
		
		uiManager.businessEntityInfos(SalableProduct.class).setMale(Boolean.FALSE);
		
		SalableProductEditPage.CREATE_ON_PRODUCT = Boolean.FALSE;
		SalableProductInstanceEditPage.CREATE_ON_SALABLE_PRODUCT = Boolean.FALSE;
		
		SaleBusinessImpl.Listener.COLLECTION.add(new SaleBusinessAdapter());
		SaleProductInstanceBusinessImpl.Listener.COLLECTION.add(new SaleProductInstanceBusinessAdapter());
		FiniteStateMachineStateLogBusinessImpl.Listener.COLLECTION.add(new FiniteStateMachineStateLogBusinessAdapter());
		
		AbstractBusinessEntityFormOnePage.BusinessEntityFormOnePageListener.COLLECTION.add(new BusinessSaleEditPageAdapter());
		SaleEditPage.Listener.COLLECTION.add(new SaleEditPageAdapter());
		
		AbstractSelectManyPage.Listener.COLLECTION.add(new BusinessSelectManySalableProductInstancePageAdapter());
		AbstractProcessManyPage.Listener.COLLECTION.add(new BusinessProcessManySalableProductInstancePageAdapter());
	}
	
}
