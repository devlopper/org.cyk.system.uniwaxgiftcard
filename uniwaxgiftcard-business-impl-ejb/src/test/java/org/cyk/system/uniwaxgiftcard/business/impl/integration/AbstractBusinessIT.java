package org.cyk.system.uniwaxgiftcard.business.impl.integration;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.cyk.system.company.business.impl.CompanyBusinessLayer;
import org.cyk.system.company.business.impl.CompanyBusinessLayerAdapter;
import org.cyk.system.company.model.CompanyConstant;
import org.cyk.system.company.model.accounting.AccountingPeriod;
import org.cyk.system.root.business.api.AbstractBusinessException;
import org.cyk.system.root.business.api.GenericBusiness;
import org.cyk.system.root.business.api.party.ApplicationBusiness;
import org.cyk.system.root.business.impl.AbstractFakedDataProducer;
import org.cyk.system.root.business.impl.AbstractFakedDataProducer.Listener.Adapter;
import org.cyk.system.root.business.impl.BusinessIntegrationTestHelper;
import org.cyk.system.root.business.impl.RootBusinessLayer;
import org.cyk.system.root.business.impl.RootBusinessTestHelper;
import org.cyk.system.root.business.impl.RootDataProducerHelper;
import org.cyk.system.root.business.impl.party.ApplicationBusinessImpl;
import org.cyk.system.root.business.impl.validation.DefaultValidator;
import org.cyk.system.root.business.impl.validation.ExceptionUtils;
import org.cyk.system.root.business.impl.validation.ValidatorMap;
import org.cyk.system.root.model.AbstractIdentifiable;
import org.cyk.system.root.model.mathematics.machine.FiniteStateMachine;
import org.cyk.system.root.model.security.Installation;
import org.cyk.system.root.persistence.api.mathematics.machine.FiniteStateMachineStateDao;
import org.cyk.system.root.persistence.impl.GenericDaoImpl;
import org.cyk.system.root.persistence.impl.PersistenceIntegrationTestHelper;
import org.cyk.system.uniwaxgiftcard.business.impl.UniwaxGiftCardBusinessLayer;
import org.cyk.system.uniwaxgiftcard.business.impl.UniwaxGiftCardBusinessTestHelper;
import org.cyk.utility.common.test.TestEnvironmentListener;
import org.cyk.utility.test.ArchiveBuilder;
import org.cyk.utility.test.Transaction;
import org.cyk.utility.test.integration.AbstractIntegrationTestJpaBased;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;

public abstract class AbstractBusinessIT extends AbstractIntegrationTestJpaBased {

	private static final long serialVersionUID = -5752455124275831171L;
	
	static {
		TestEnvironmentListener.COLLECTION.add(new TestEnvironmentListener.Adapter.Default(){
			private static final long serialVersionUID = 1983969363248568780L;
			@Override
			protected Throwable getThrowable(Throwable throwable) {
				return commonUtils.getThrowableInstanceOf(throwable, AbstractBusinessException.class);
			}
    		@Override
    		public void assertEquals(String message, Object expected, Object actual) {
    			Assert.assertEquals(message, expected, actual);
    		}
    		@Override
    		public String formatBigDecimal(BigDecimal value) {
    			return RootBusinessLayer.getInstance().getNumberBusiness().format(value);
    		}
    	});
	}
	
	@Inject protected ExceptionUtils exceptionUtils; 
	@Inject protected DefaultValidator defaultValidator;
	@Inject protected GenericDaoImpl g;
	@Inject protected GenericBusiness genericBusiness;
	@Inject protected ApplicationBusiness applicationBusiness;
	
	@Inject protected ValidatorMap validatorMap;// = ValidatorMap.getInstance();
	
	@Inject protected UniwaxGiftCardBusinessLayer uniwaxgiftcardBusinessLayer;
	@Inject protected UniwaxGiftCardBusinessTestHelper uniwaxgiftcardBusinessTestHelper;
	
	@Inject protected UserTransaction userTransaction;
    
	@Inject protected DataProducer dataProducer;
    @Inject protected FiniteStateMachineStateDao finiteStateMachineStateDao;
	
    @Inject private CompanyBusinessLayer companyBusinessLayer;
    
	@Deployment
    public static Archive<?> createDeployment() {
    	Archive<?> archive = createRootDeployment();
    	return archive;
    }
	
	protected void installApplication(Boolean fake){
		ApplicationBusinessImpl.Listener.COLLECTION.add(new ApplicationBusinessImpl.Listener.Adapter.Default(){
			private static final long serialVersionUID = 6148913289155659043L;
			@Override
    		public void installationStarted(Installation installation) {
    			installation.getApplication().setUniformResourceLocatorFilteringEnabled(Boolean.FALSE);
    			installation.getApplication().setWebContext("company");
    			installation.getApplication().setName("Uniwax - Gestion des cartes cadeaux");
    			super.installationStarted(installation);
    		}
    	});
    	
    	companyBusinessLayer.getCompanyBusinessLayerListeners().add(new CompanyBusinessLayerAdapter() {
			private static final long serialVersionUID = 5179809445850168706L;

			@Override
			public String getCompanyName() {
				return "Gestion des cartes cadeaux";
			}
			
			@Override
			public void handleAccountingPeriodToInstall(AccountingPeriod accountingPeriod) {
				FiniteStateMachine finiteStateMachine = RootDataProducerHelper.getInstance().createFiniteStateMachine(CompanyConstant.GIFT_CARD_WORKFLOW
		    			, new String[]{CompanyConstant.GIFT_CARD_WORKFLOW_ALPHABET_SEND,CompanyConstant.GIFT_CARD_WORKFLOW_ALPHABET_RECEIVE,CompanyConstant.GIFT_CARD_WORKFLOW_ALPHABET_SELL,CompanyConstant.GIFT_CARD_WORKFLOW_ALPHABET_USE}
						, new String[]{CompanyConstant.GIFT_CARD_WORKFLOW_STATE_ASSIGNED,CompanyConstant.GIFT_CARD_WORKFLOW_STATE_SENT,CompanyConstant.GIFT_CARD_WORKFLOW_STATE_RECEIVED,CompanyConstant.GIFT_CARD_WORKFLOW_STATE_SOLD
						,CompanyConstant.GIFT_CARD_WORKFLOW_STATE_USED}
		    			,CompanyConstant.GIFT_CARD_WORKFLOW_STATE_ASSIGNED, new String[]{CompanyConstant.GIFT_CARD_WORKFLOW_STATE_USED}, new String[][]{
		    			{CompanyConstant.GIFT_CARD_WORKFLOW_STATE_ASSIGNED,CompanyConstant.GIFT_CARD_WORKFLOW_ALPHABET_SEND,CompanyConstant.GIFT_CARD_WORKFLOW_STATE_SENT}
		    			,{CompanyConstant.GIFT_CARD_WORKFLOW_STATE_SENT,CompanyConstant.GIFT_CARD_WORKFLOW_ALPHABET_RECEIVE,CompanyConstant.GIFT_CARD_WORKFLOW_STATE_RECEIVED}
		    			,{CompanyConstant.GIFT_CARD_WORKFLOW_STATE_RECEIVED,CompanyConstant.GIFT_CARD_WORKFLOW_ALPHABET_SELL,CompanyConstant.GIFT_CARD_WORKFLOW_STATE_SOLD}
		    			,{CompanyConstant.GIFT_CARD_WORKFLOW_STATE_SOLD,CompanyConstant.GIFT_CARD_WORKFLOW_ALPHABET_USE,CompanyConstant.GIFT_CARD_WORKFLOW_STATE_USED}
		    	});
				accountingPeriod.getSaleConfiguration().setSalableProductInstanceCashRegisterFiniteStateMachine(finiteStateMachine);
				accountingPeriod.getSaleConfiguration().setSalableProductInstanceCashRegisterSaleConsumeState(finiteStateMachineStateDao.read(CompanyConstant.GIFT_CARD_WORKFLOW_STATE_SOLD));
				accountingPeriod.getSaleConfiguration().setAllowOnlySalableProductInstanceOfCashRegister(Boolean.TRUE);
				accountingPeriod.getSaleConfiguration().setMinimalNumberOfProductBySale(1l);
				accountingPeriod.getSaleConfiguration().setMaximalNumberOfProductBySale(1l);
				super.handleAccountingPeriodToInstall(accountingPeriod);
			}
			
		});
    	uniwaxgiftcardBusinessLayer.installApplication(fake);
    }
    
    protected void installApplication(){
    	long t = System.currentTimeMillis();
    	installApplication(Boolean.TRUE);
    	produce(getFakedDataProducer());
    	System.out.println( ((System.currentTimeMillis()-t)/1000)+" s" );
    }
	
    protected AbstractFakedDataProducer getFakedDataProducer(){
    	return null;
    }
    
    protected void produce(final AbstractFakedDataProducer fakedDataProducer){
    	if(fakedDataProducer==null)
    		return ;
    	new Transaction(this,userTransaction,null){
			@Override
			public void _execute_() {
				fakedDataProducer.produce(fakedDataProducerAdapter());
			}
    	}.run();
    }
    
    protected Adapter fakedDataProducerAdapter(){
    	return new Adapter(){
			private static final long serialVersionUID = -6856905751394551672L;

			@Override
    		public void flush() {
    			super.flush();
    			getEntityManager().flush();
    		}
    	};
    }
    
    @Override
    public EntityManager getEntityManager() {
        return g.getEntityManager();
    }
	
    @Override
    protected void _execute_() {
        super._execute_();
        create();    
        read(); 
        update();    
        delete();    
        finds();
        businesses();
    }
    
	protected void finds(){}
	
	protected abstract void businesses();
	
	/* Shortcut */
    
    protected AbstractIdentifiable create(AbstractIdentifiable object){
        return genericBusiness.create(object);
    }
    
    protected AbstractIdentifiable update(AbstractIdentifiable object){
        return genericBusiness.update(object);
    }
        
    public static Archive<?> createRootDeployment() {
        return  
                new ArchiveBuilder().create().getArchive().
                    addClasses(BusinessIntegrationTestHelper.classes()).
                    addClasses(PersistenceIntegrationTestHelper.classes()).
                    addClasses(RootBusinessLayer.class,RootBusinessTestHelper.class,CompanyBusinessLayer.class).
                    addPackages(Boolean.FALSE, BusinessIntegrationTestHelper.packages()).
                    addPackages(Boolean.TRUE,"org.cyk.system.company").
                    addPackages(Boolean.TRUE,"org.cyk.system.uniwaxgiftcard") 
                ;
    } 
    
    @Override protected void populate() {
    	installApplication();
    }
    
    @Override protected void create() {}
    @Override protected void delete() {}
    @Override protected void read() {}
    @Override protected void update() {}

    
}