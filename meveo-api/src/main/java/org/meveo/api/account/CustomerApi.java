package org.meveo.api.account;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.account.CustomerDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.Seller;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerBrand;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.crm.impl.CustomerBrandService;
import org.meveo.service.crm.impl.CustomerCategoryService;
import org.meveo.service.crm.impl.CustomerService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class CustomerApi extends AccountApi {

	@Inject
	private CustomerService customerService;

	@Inject
	private CustomerCategoryService customerCategoryService;

	@Inject
	private CustomerBrandService customerBrandService;

	@Inject
	private SellerService sellerService;

	public void create(CustomerDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getCustomerCategory())
				&& !StringUtils.isBlank(postData.getCustomerBrand()) && !StringUtils.isBlank(postData.getSeller())) {
			// check if customer already exists
			if (customerService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(Customer.class, postData.getCode());
			}

			CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategory(),
					currentUser.getProvider());
			if (customerCategory == null) {
				throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCustomerCategory());
			}

			CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCustomerBrand(),
					currentUser.getProvider());
			if (customerBrand == null) {
				throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCustomerBrand());
			}

			Seller seller = sellerService.findByCode(postData.getSeller(), currentUser.getProvider());
			if (seller == null) {
				throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
			}

			Customer customer = new Customer();
			populate(postData, customer, currentUser);

			customer.setCustomerCategory(customerCategory);
			customer.setCustomerBrand(customerBrand);
			customer.setSeller(seller);

			customerService.create(customer, currentUser, currentUser.getProvider());
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getCustomerCategory())) {
				missingParameters.add("customerCategory");
			}
			if (StringUtils.isBlank(postData.getCustomerBrand())) {
				missingParameters.add("customerBrand");
			}
			if (StringUtils.isBlank(postData.getSeller())) {
				missingParameters.add("seller");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(CustomerDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())) {
			// check if customer exists
			if (customerService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
				throw new EntityDoesNotExistsException(Customer.class, postData.getCode());
			}

			CustomerCategory customerCategory = customerCategoryService.findByCode(postData.getCustomerCategory(),
					currentUser.getProvider());
			if (customerCategory == null) {
				throw new EntityDoesNotExistsException(CustomerCategory.class, postData.getCustomerCategory());
			}

			CustomerBrand customerBrand = customerBrandService.findByCode(postData.getCustomerBrand(),
					currentUser.getProvider());
			if (customerBrand == null) {
				throw new EntityDoesNotExistsException(CustomerBrand.class, postData.getCustomerBrand());
			}

			Seller seller = sellerService.findByCode(postData.getSeller(), currentUser.getProvider());
			if (seller == null) {
				throw new EntityDoesNotExistsException(Seller.class, postData.getSeller());
			}

			Customer customer = customerService.findByCode(postData.getCode(), currentUser.getProvider());
			if (customer == null) {
				throw new EntityDoesNotExistsException(Customer.class, postData.getCode());
			}

			updateAccount(customer, postData, currentUser);
			customer.setCustomerCategory(customerCategory);
			customer.setCustomerBrand(customerBrand);
			customer.setSeller(seller);

			customerService.updateAudit(customer, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getCustomerCategory())) {
				missingParameters.add("customerCategory");
			}
			if (StringUtils.isBlank(postData.getCustomerBrand())) {
				missingParameters.add("customerBrand");
			}
			if (StringUtils.isBlank(postData.getSeller())) {
				missingParameters.add("seller");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public CustomerDto find(String customerCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(customerCode)) {
			Customer customer = customerService.findByCode(customerCode, provider);
			if (customer == null) {
				throw new EntityDoesNotExistsException(Customer.class, customerCode);
			}

			return new CustomerDto(customer);
		} else {
			missingParameters.add("customerCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String customerCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(customerCode)) {
			Customer customer = customerService.findByCode(customerCode, provider);
			if (customer == null) {
				throw new EntityDoesNotExistsException(Customer.class, customerCode);
			}

			customerService.remove(customer);
		} else {
			missingParameters.add("customerCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}