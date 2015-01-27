package org.meveo.api.account;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.api.dto.account.UserAccountDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.UserAccountService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class UserAccountApi extends AccountApi {

	@Inject
	private UserAccountService userAccountService;

	@Inject
	private BillingAccountService billingAccountService;

	public void create(UserAccountDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getBillingAccount())) {
			Provider provider = currentUser.getProvider();

			BillingAccount billingAccount = billingAccountService.findByCode(postData.getBillingAccount(), provider);
			if (billingAccount == null) {
				throw new EntityDoesNotExistsException(BillingAccount.class, postData.getBillingAccount());
			}

			UserAccount userAccount = new UserAccount();
			populate(postData, userAccount, currentUser);

			userAccount.setBillingAccount(billingAccount);
			userAccount.setProvider(currentUser.getProvider());

			try {
				userAccountService.createUserAccount(billingAccount, userAccount, currentUser);
			} catch (AccountAlreadyExistsException e) {
				throw new EntityAlreadyExistsException(UserAccount.class, postData.getCode());
			}
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getBillingAccount())) {
				missingParameters.add("billingAccount");
			}
		}
	}

	public void update(UserAccountDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getDescription())
				&& !StringUtils.isBlank(postData.getBillingAccount())) {
			Provider provider = currentUser.getProvider();

			UserAccount userAccount = userAccountService.findByCode(postData.getCode(), provider);
			if (userAccount == null) {
				throw new EntityDoesNotExistsException(UserAccount.class, postData.getCode());
			}

			BillingAccount billingAccount = billingAccountService.findByCode(postData.getBillingAccount(), provider);
			if (billingAccount == null) {
				throw new EntityDoesNotExistsException(BillingAccount.class, postData.getBillingAccount());
			}

			updateAccount(userAccount, postData, currentUser);

			userAccount.setBillingAccount(billingAccount);

			userAccountService.update(userAccount, currentUser);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}
			if (StringUtils.isBlank(postData.getDescription())) {
				missingParameters.add("description");
			}
			if (StringUtils.isBlank(postData.getBillingAccount())) {
				missingParameters.add("billingAccount");
			}
		}
	}

	public UserAccountDto find(String userAccountCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(userAccountCode)) {
			UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
			if (userAccount == null) {
				throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
			}

			return new UserAccountDto(userAccount);
		} else {
			missingParameters.add("userAccountCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void remove(String userAccountCode, Provider provider) throws MeveoApiException {
		if (!StringUtils.isBlank(userAccountCode)) {
			UserAccount userAccount = userAccountService.findByCode(userAccountCode, provider);
			if (userAccount == null) {
				throw new EntityDoesNotExistsException(UserAccount.class, userAccountCode);
			}

			userAccountService.remove(userAccount);
		} else {
			missingParameters.add("userAccountCode");

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

}