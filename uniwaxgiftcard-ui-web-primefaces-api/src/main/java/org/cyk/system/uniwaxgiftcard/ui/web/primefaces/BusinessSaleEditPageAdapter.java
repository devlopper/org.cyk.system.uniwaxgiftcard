package org.cyk.system.uniwaxgiftcard.ui.web.primefaces;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.ArrayUtils;
import org.cyk.system.company.business.impl.CompanyBusinessLayer;
import org.cyk.system.company.model.CompanyConstant;
import org.cyk.system.company.model.payment.CashRegisterMovementMode;
import org.cyk.system.company.model.sale.SalableProductInstance;
import org.cyk.system.company.model.sale.SalableProductInstanceCashRegister;
import org.cyk.system.company.model.sale.Sale;
import org.cyk.system.company.ui.web.primefaces.GiftCardSystemMenuBuilder;
import org.cyk.system.company.ui.web.primefaces.sale.SaleEditPage;
import org.cyk.system.company.ui.web.primefaces.sale.SaleEditPage.FormOneSaleProduct;
import org.cyk.system.root.business.impl.RootBusinessLayer;
import org.cyk.ui.api.data.collector.control.Input;
import org.cyk.ui.api.data.collector.form.ControlSet;
import org.cyk.ui.web.api.WebManager;
import org.cyk.ui.web.api.data.collector.control.WebInput;
import org.cyk.ui.web.primefaces.data.collector.control.ControlSetAdapter;
import org.cyk.ui.web.primefaces.page.AbstractBusinessEntityFormOnePage;
import org.cyk.utility.common.cdi.AbstractBean;
import org.primefaces.extensions.model.dynaform.DynaFormControl;
import org.primefaces.extensions.model.dynaform.DynaFormLabel;
import org.primefaces.extensions.model.dynaform.DynaFormModel;
import org.primefaces.extensions.model.dynaform.DynaFormRow;

public class BusinessSaleEditPageAdapter extends AbstractBusinessEntityFormOnePage.BusinessEntityFormOnePageListener.Adapter.Default<Sale> implements Serializable {
	private static final long serialVersionUID = 5004002889129536583L;
	
	public BusinessSaleEditPageAdapter() {
		super(Sale.class);
	}
	
	@Override
	public void initialisationEnded(AbstractBean bean) {
		super.initialisationEnded(bean);
		final SaleEditPage saleEditPage = (SaleEditPage) bean;
		
		saleEditPage.getForm().getControlSetListeners().add(new ControlSetAdapter<Object>(){
			@Override
			public Boolean build(Field field) {
				if(GiftCardSystemMenuBuilder.ACTION_SELL_GIFT_CARD.equals(saleEditPage.getActionIdentifier()))
					return ArrayUtils.contains(new Object[]{SaleEditPage.FormOneSaleProduct.FIELD_CASHIER,SaleEditPage.FormOneSaleProduct.FIELD_SALABLE_PRODUCT
							,SaleEditPage.FormOneSaleProduct.FIELD_SALABLE_PRODUCT_INSTANCE,SaleEditPage.FormOneSaleProduct.FIELD_CUSTOMER}
					, field.getName());
				else if(GiftCardSystemMenuBuilder.ACTION_USE_GIFT_CARD.equals(saleEditPage.getActionIdentifier()))
					return ArrayUtils.contains(new Object[]{SaleEditPage.FormOneSaleProduct.FIELD_CASHIER,SaleEditPage.FormOneSaleProduct.FIELD_EXTERNAL_IDENTIFIER
							,SaleEditPage.FormOneSaleProduct.FIELD_SUPPORTING_DOCUMENT_IDENTIFIER
							/*,SaleEditPage.FormOneSaleProduct.FIELD_CASH_REGISTER_MOVEMENT_MODE*/}
					, field.getName());
				else
					return Boolean.FALSE;
			}
			@Override
			public void input(ControlSet<Object, DynaFormModel, DynaFormRow, DynaFormLabel, DynaFormControl, SelectItem> controlSet,
					Input<?, DynaFormModel, DynaFormRow, DynaFormLabel, DynaFormControl, SelectItem> input) {
				super.input(controlSet, input);
				input.setReadOnly(ArrayUtils.contains(new Object[]{SaleEditPage.FormOneSaleProduct.FIELD_COST}, input.getField().getName()));
				if(GiftCardSystemMenuBuilder.ACTION_SELL_GIFT_CARD.equals(saleEditPage.getActionIdentifier())){
					input.setRequired(ArrayUtils.contains(new Object[]{SaleEditPage.FormOneSaleProduct.FIELD_CASHIER,SaleEditPage.FormOneSaleProduct.FIELD_SALABLE_PRODUCT
							,SaleEditPage.FormOneSaleProduct.FIELD_SALABLE_PRODUCT_INSTANCE}
					, input.getField().getName()));
				}else if(GiftCardSystemMenuBuilder.ACTION_USE_GIFT_CARD.equals(saleEditPage.getActionIdentifier())){
					input.setRequired(ArrayUtils.contains(new Object[]{SaleEditPage.FormOneSaleProduct.FIELD_CASHIER
							,SaleEditPage.FormOneSaleProduct.FIELD_SUPPORTING_DOCUMENT_IDENTIFIER
							,SaleEditPage.FormOneSaleProduct.FIELD_CASH_REGISTER_MOVEMENT_MODE}
					, input.getField().getName()));
				}
			}
		});
		
		
	}
	
	@Override
	public void afterInitialisationEnded(AbstractBean bean) {
		final SaleEditPage saleEditPage = (SaleEditPage) bean;
		super.afterInitialisationEnded(bean);
		saleEditPage.setFieldValue(SaleEditPage.FormOneSaleProduct.FIELD_CASH_REGISTER_MOVEMENT_MODE, CompanyBusinessLayer.getInstance().getCashRegisterMovementModeBusiness().find(CashRegisterMovementMode.GIFT_CARD));
		
		if(GiftCardSystemMenuBuilder.ACTION_SELL_GIFT_CARD.equals(saleEditPage.getActionIdentifier())){
			
		}else if(GiftCardSystemMenuBuilder.ACTION_USE_GIFT_CARD.equals(saleEditPage.getActionIdentifier())){
			//saleEditPage.getForm().findInputByClassByFieldName(WebInput.class, FormOneSaleProduct.FIELD_CASHIER).getAjaxListener().setEnabled(Boolean.FALSE);
			((FormOneSaleProduct)saleEditPage.getForm().getData()).setCashRegisterMovementMode(CompanyBusinessLayer.getInstance().getCashRegisterMovementModeBusiness().find(CashRegisterMovementMode.GIFT_CARD));
			saleEditPage.addInputListener(SaleEditPage.FormOneSaleProduct.FIELD_SUPPORTING_DOCUMENT_IDENTIFIER, new WebInput.Listener.Adapter.Default(){
				private static final long serialVersionUID = 1L;
				@Override
				public void validate(FacesContext facesContext,UIComponent uiComponent, Object value)throws ValidatorException {
					super.validate(facesContext, uiComponent, value);
					SalableProductInstance salableProductInstance = CompanyBusinessLayer.getInstance().getSalableProductInstanceBusiness().find((String)value);
					if(salableProductInstance==null)
						WebManager.getInstance().throwValidationException("salableProductInstanceDoesNotExists",null);
					SalableProductInstanceCashRegister.SearchCriteria searchCriteria = new SalableProductInstanceCashRegister.SearchCriteria();
					searchCriteria.addCashRegisters(CompanyBusinessLayer.getInstance().getCashRegisterBusiness().findAll());
					searchCriteria.addFiniteStateMachineStates(Arrays.asList(RootBusinessLayer.getInstance().getFiniteStateMachineStateBusiness().find(CompanyConstant.GIFT_CARD_WORKFLOW_STATE_SOLD)));
					Collection<SalableProductInstanceCashRegister> salableProductInstanceCashRegisters = CompanyBusinessLayer.getInstance()
							.getSalableProductInstanceCashRegisterBusiness().findByCriteria(searchCriteria);
					if(salableProductInstanceCashRegisters.isEmpty())
						WebManager.getInstance().throwValidationException("salableProductInstanceIsNotInSoldState",null);
				}
			});
		}
	}

}
