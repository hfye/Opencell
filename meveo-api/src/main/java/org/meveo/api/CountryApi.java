package org.meveo.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.CountryDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingCountryService;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
@Stateless
public class CountryApi extends BaseApi {

	@Inject
	private CountryService countryService;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private CurrencyService currencyService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	public void create(CountryDto countryDto, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(countryDto.getCountryCode())
				&& !StringUtils.isBlank(countryDto.getName())
				&& !StringUtils.isBlank(countryDto.getCurrencyCode())) {

			// If countryCode exist in the trading country table
			// ("billing_trading_country"), return error.

			Provider provider = currentUser.getProvider();
			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(countryDto.getCountryCode(),
							provider);

			if (StringUtils.isBlank(countryDto.getCurrencyCode())) {
				throw new EntityDoesNotExistsException(Currency.class,
						countryDto.getCurrencyCode());
			}

			if (tradingCountry == null) {
				// check currency
				Country country = new Country();

				country = countryService
						.findByCode(countryDto.getCountryCode());
				Currency currency = null;
				Auditable auditable = new Auditable();

				// If country code doesn't exist in the reference table, create
				// the country in this table ("adm_country") with the currency
				// code for the default provider.
				if (country == null) {
					// If country code don't exist in the reference table,
					// create the country in this table ("adm_country") with the
					// currency code for the default provider.
					country = new Country();
					auditable.setCreated(new Date());
					auditable.setCreator(currentUser);
					country.setDescriptionEn(countryDto.getName());
					country.setCountryCode(countryDto.getCountryCode());
				} else {
					// If country code exist in the reference table but the
					// currencyCode values in the two tables are different,
					// change the value of Currencycode in the country reference
					auditable.setUpdated(new Date());
					auditable.setUpdater(currentUser);
				}
				country.setAuditable(auditable);

				if (countryDto.getCurrencyCode() != null) { //
					currency = currencyService.findByCode(countryDto
							.getCurrencyCode());
					// If currencyCode don't exist in reference table
					// ("adm_currency"), return error.
					if (currency == null) {
						throw new EntityDoesNotExistsException(Currency.class,
								countryDto.getCurrencyCode());
					}
				} else {
					if (provider.getCurrency() != null) {
						currency = provider.getCurrency();
					}
				}
				country.setCurrency(currency);

				if (country.isTransient()) {
					countryService.create(em, country, currentUser, provider);
				}

				Auditable auditableTrading = new Auditable();
				auditableTrading.setCreated(new Date());
				auditableTrading.setCreator(currentUser);

				// If country don't exist in the trading country table, create
				// the country in this table ("billing_trading_country").
				tradingCountry = new TradingCountry();
				tradingCountry.setCountry(country);
				tradingCountry.setProvider(provider);
				tradingCountry.setActive(true);
				tradingCountry.setPrDescription(countryDto.getName());
				tradingCountry.setAuditable(auditableTrading);
				tradingCountryService.create(em, tradingCountry, currentUser,
						provider);

				// If currencyCode exist in reference table ("adm_currency") and
				// don't exist in the trading currency table, create the
				// currency in the trading currency table
				// ('billing_trading_currency").
				if (!StringUtils.isBlank(countryDto.getCurrencyCode())
						&& tradingCurrencyService.findByTradingCurrencyCode(
								countryDto.getCurrencyCode(), provider) == null) {
					TradingCurrency tradingCurrency = new TradingCurrency();
					tradingCurrency.setActive(true);
					tradingCurrency.setCurrency(currency);
					tradingCurrency.setAuditable(auditableTrading);
					tradingCurrency.setCurrencyCode(countryDto
							.getCurrencyCode());
					tradingCurrency.setPrDescription(countryDto
							.getCurrencyCode());
					tradingCurrencyService.create(em, tradingCurrency,
							currentUser, provider);
				}
			} else {
				throw new EntityAlreadyExistsException(
						TradingCountry.class.getName(),
						tradingCountry.getCountryCode());
			}

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(countryDto.getCountryCode())) {
				missingFields.add("countryCode");
			}
			if (StringUtils.isBlank(countryDto.getName())) {
				missingFields.add("name");
			}
			if (StringUtils.isBlank(countryDto.getCurrencyCode())) {
				missingFields.add("currencyCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

	public CountryDto find(String countryCode, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(countryCode)) {
			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(countryCode,
							currentUser.getProvider());

			if (tradingCountry != null) {
				Country country = countryService.findByCode(countryCode);
				return new CountryDto(tradingCountry, country);
			}

			throw new EntityDoesNotExistsException(TradingCountry.class,
					countryCode);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(countryCode)) {
				missingFields.add("countryCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

	public void remove(String countryCode, String currencyCode, User currentUser)
			throws MeveoApiException {
		Provider provider = currentUser.getProvider();

		if (!StringUtils.isBlank(countryCode)
				&& !StringUtils.isBlank(currencyCode)) {
			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(countryCode, provider);
			Currency currency = currencyService.findByCode(currencyCode);
			if (tradingCountry != null && currency != null) {
				if (tradingCountry != null) {
					tradingCountryService.remove(tradingCountry);
				}
			} else {
				if (tradingCountry == null) {
					throw new EntityDoesNotExistsException(
							TradingCountry.class, countryCode);
				} else {
					throw new EntityDoesNotExistsException(Currency.class,
							currencyCode);
				}
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(countryCode)) {
				missingFields.add("countryCode");
			}
			if (StringUtils.isBlank(currencyCode)) {
				missingFields.add("currencyCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

	public void update(CountryDto countryDto, User currentUser)
			throws MeveoApiException {
		Provider provider = currentUser.getProvider();
		if (!StringUtils.isBlank(countryDto.getCountryCode())
				&& !StringUtils.isBlank(countryDto.getCurrencyCode())) {
			Currency currency = currencyService.findByCode(countryDto
					.getCurrencyCode());
			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(countryDto.getCountryCode(),
							provider);

			if (currency != null && tradingCountry != null) {
				Country country = countryService.findByCode(em,
						countryDto.getCountryCode());
				if (country != null
						&& !StringUtils.isBlank(countryDto.getName())) {
					if (!country.getDescriptionEn()
							.equals(countryDto.getName())) {
						tradingCountry.setPrDescription(countryDto.getName());
						country.setCurrency(currency);
						country.setDescriptionEn(countryDto.getName());
					}

					TradingCurrency tradingCurrency = tradingCurrencyService
							.findByTradingCurrencyCode(
									countryDto.getCurrencyCode(), provider);
					if (tradingCurrency == null) {
						Auditable auditableTrading = new Auditable();
						auditableTrading.setCreated(new Date());
						auditableTrading.setCreator(currentUser);

						tradingCurrency = new TradingCurrency();
						tradingCurrency.setActive(true);
						tradingCurrency.setCurrency(currency);
						tradingCurrency.setAuditable(auditableTrading);
						tradingCurrency.setCurrencyCode(countryDto
								.getCurrencyCode());
						tradingCurrency.setPrDescription(countryDto
								.getCurrencyCode());
						tradingCurrencyService.create(em, tradingCurrency,
								currentUser, provider);
					}
				} else {
					create(countryDto, currentUser);
				}

			} else {
				if (currency == null) {
					throw new EntityDoesNotExistsException(Currency.class,
							countryDto.getCurrencyCode());
				}
				if (tradingCountry == null) {
					throw new EntityDoesNotExistsException(
							TradingCountry.class, countryDto.getCountryCode());
				}
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(countryDto.getCountryCode())) {
				missingFields.add("countryCode");
			}
			if (StringUtils.isBlank(countryDto.getCurrencyCode())) {
				missingFields.add("currencyCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}
}
