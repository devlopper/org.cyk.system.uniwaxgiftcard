package org.cyk.system.uniwaxgiftcard.ui.web.primefaces;

import java.io.Serializable;

import javax.inject.Singleton;

import lombok.Getter;

import org.cyk.system.company.ui.web.primefaces.GiftCardSystemMenuBuilder;
import org.cyk.system.uniwaxgiftcard.business.impl.UniwaxGiftCardBusinessLayer;
import org.cyk.ui.api.command.menu.SystemMenu;
import org.cyk.ui.web.primefaces.AbstractPrimefacesManager;
import org.cyk.ui.web.primefaces.UserSession;
import org.cyk.utility.common.annotation.Deployment;
import org.cyk.utility.common.annotation.Deployment.InitialisationType;

@Singleton @Deployment(initialisationType=InitialisationType.EAGER,order=UniwaxGiftCardWebManager.DEPLOYMENT_ORDER) @Getter
public class UniwaxGiftCardWebManager extends AbstractPrimefacesManager implements Serializable {

	public static final int DEPLOYMENT_ORDER = UniwaxGiftCardBusinessLayer.DEPLOYMENT_ORDER+1;
	private static final long serialVersionUID = 7231721191071228908L;

	private static UniwaxGiftCardWebManager INSTANCE;
	
	@Override
	protected void initialisation() {
		INSTANCE = this;
		identifier = "uniwaxgiftcard";
		super.initialisation();  
		logoFileInfos.setExtension("jpg");
		logoFileInfos.setName("default");
	}
		
	public static UniwaxGiftCardWebManager getInstance() {
		return INSTANCE;
	}

	@Override
	public SystemMenu systemMenu(UserSession userSession) {
		return GiftCardSystemMenuBuilder.getInstance().build((UserSession) userSession);
	}
	
}
