package org.cyk.system.uniwaxgiftcard.business.impl;

import java.io.Serializable;

import javax.inject.Singleton;

import org.cyk.system.root.business.impl.AbstractBusinessTestHelper;

@Singleton
public class UniwaxGiftCardBusinessTestHelper extends AbstractBusinessTestHelper implements Serializable {

	private static final long serialVersionUID = -6893154890151909538L;
	private static UniwaxGiftCardBusinessTestHelper INSTANCE;
	
	/**/
	
	@Override
	protected void initialisation() {
		INSTANCE = this;
		super.initialisation();
	}

	public static UniwaxGiftCardBusinessTestHelper getInstance() {
		return INSTANCE;
	}

}
