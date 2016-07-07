package org.cyk.system.uniwaxgiftcard.business.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import org.cyk.system.company.business.impl.CompanyBusinessLayer;
import org.cyk.system.company.model.payment.CashRegister;
import org.cyk.system.company.model.payment.Cashier;
import org.cyk.system.company.model.product.Product;
import org.cyk.system.company.model.product.TangibleProduct;
import org.cyk.system.company.model.sale.SalableProduct;
import org.cyk.system.company.model.structure.Employee;
import org.cyk.system.root.business.api.TypedBusiness;
import org.cyk.system.root.business.impl.AbstractBusinessLayer;
import org.cyk.system.root.model.AbstractIdentifiable;
import org.cyk.utility.common.annotation.Deployment;
import org.cyk.utility.common.annotation.Deployment.InitialisationType;

@Singleton @Deployment(initialisationType=InitialisationType.EAGER,order=UniwaxGiftCardBusinessLayer.DEPLOYMENT_ORDER)
public class UniwaxGiftCardBusinessLayer extends AbstractBusinessLayer implements Serializable {

	public static final int DEPLOYMENT_ORDER = CompanyBusinessLayer.DEPLOYMENT_ORDER+1;
	private static final long serialVersionUID = -462780912429013933L;

	private static UniwaxGiftCardBusinessLayer INSTANCE;
		
	@Override
	protected void initialisation() {
		INSTANCE = this;
		super.initialisation();
	}
	
	
	@Override
	protected void persistData() {
		Collection<Product> products = new ArrayList<>();
		Collection<SalableProduct> salableProducts = new ArrayList<>();
		Collection<CashRegister> cashRegisters = new ArrayList<>();
		Collection<Employee> employees = new ArrayList<>();
		Collection<Cashier> cashiers = new ArrayList<>();
		
		for(Object[] values : new Object[][]{{"5000",1,5},{"10000",100,105},{"20000",200,205}} ){
			TangibleProduct tangibleProduct = CompanyBusinessLayer.getInstance().getTangibleProductBusiness().instanciateOne((String)values[0], (String)values[0]);
			products.add(tangibleProduct);
			List<String> instances = new ArrayList<>();
			for(int i = (Integer)values[1]; i <= (Integer)values[2]; i++ )
				instances.add(i+"");
			
			SalableProduct salableProduct = CompanyBusinessLayer.getInstance().getSalableProductBusiness().instanciateOne(tangibleProduct.getCode(), tangibleProduct.getName(), instances.toArray(new String[]{}));
			salableProduct.setItemCodeSeparator(null);
			salableProduct.setProduct(tangibleProduct);
			salableProduct.setPrice(new BigDecimal((String)values[0]));
			salableProducts.add(salableProduct);
		}
		
		CompanyBusinessLayer.getInstance().getProductBusiness().create(products);
		CompanyBusinessLayer.getInstance().getSalableProductBusiness().create(salableProducts);
		
		for(String shop : new String[]{"CAP NORD","CAP SUD","PLTX PR ST-C2D","PLTX  P.A.A","PLTX (BOLLORE)","PLTX","PLATEAU","WOODIN 2PLTX","CASH DJIBI"
				,"PRESIDENCE DE LA REPUBLIQUE","ANAC","Mme DICKOH"}){
			CashRegister cashRegister = new CashRegister(shop, null, null);
			cashRegisters.add(cashRegister);
			
			Employee employee = CompanyBusinessLayer.getInstance().getEmployeeBusiness().instanciateOne();
			employees.add(employee);
			
			Cashier cashier = new Cashier(employee.getPerson(), cashRegister);
			cashiers.add(cashier);
		}
		
		CompanyBusinessLayer.getInstance().getEmployeeBusiness().create(employees);
		CompanyBusinessLayer.getInstance().getCashRegisterBusiness().create(cashRegisters);
		CompanyBusinessLayer.getInstance().getCashierBusiness().create(cashiers);
		
		/*
		Collection<SalableProductInstanceCashRegister> salableProductInstanceCashRegisters = new ArrayList<>();
		SaleConfiguration saleConfiguration = companyBusinessLayer.getAccountingPeriodBusiness().findCurrent().getSaleConfiguration();
		cashRegisters = cashRegisterDao.readAll();
		List<SalableProductInstance> salableProductInstances = new ArrayList<>(salableProductInstanceDao.readAll());
		int i = 0 , size = salableProductInstances.size() / cashRegisters.size();
		Application application = applicationDao.readOneRandomly();
		for(CashRegister cashRegister : cashRegisters){
			for(int j = i; j < i+size && j < salableProductInstances.size() ; j++){				
				SalableProductInstanceCashRegister salableProductInstanceCashRegister = new SalableProductInstanceCashRegister(salableProductInstances.get(j)
						, cashRegister, saleConfiguration.getSalableProductInstanceCashRegisterFiniteStateMachine().getInitialState());
				salableProductInstanceCashRegister.getProcessing().setParty(application);
				salableProductInstanceCashRegister.getFiniteStateMachineState().getProcessing().setParty(application);
				salableProductInstanceCashRegisters.add(salableProductInstanceCashRegister);
			}
			i += size;
		}
		flush(SalableProductInstanceCashRegister.class, salableProductInstanceCashRegisters);
		*/
		//RootRandomDataProvider.createActor(Customer.class, 5);
	}
	
	@SuppressWarnings({ })
    @Override
    public void registerTypedBusinessBean(Map<Class<AbstractIdentifiable>, TypedBusiness<AbstractIdentifiable>> beansMap) {
        
    }
	
	/**/
	
	@Override
	protected void setConstants(){
    	
    }
	
	
	public static UniwaxGiftCardBusinessLayer getInstance() {
		return INSTANCE;
	}
	
	
	/**/
	
	protected void fakeTransactions(){
		
	}    
    
}
