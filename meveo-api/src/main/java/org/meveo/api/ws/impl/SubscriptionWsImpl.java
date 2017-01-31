package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.billing.SubscriptionApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.account.ApplyOneShotChargeInstanceRequestDto;
import org.meveo.api.dto.account.ApplyProductRequestDto;
import org.meveo.api.dto.billing.ActivateServicesRequestDto;
import org.meveo.api.dto.billing.InstantiateServicesRequestDto;
import org.meveo.api.dto.billing.SubscriptionDto;
import org.meveo.api.dto.billing.SuspendServicesRequestDto;
import org.meveo.api.dto.billing.SuspendSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionRequestDto;
import org.meveo.api.dto.billing.TerminateSubscriptionServicesRequestDto;
import org.meveo.api.dto.response.billing.GetSubscriptionResponseDto;
import org.meveo.api.dto.response.billing.SubscriptionsResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.ws.SubscriptionWs;
import org.meveo.model.billing.ChargeInstance;

@WebService(serviceName = "SubscriptionWs", endpointInterface = "org.meveo.api.ws.SubscriptionWs")
@Interceptors({ WsRestApiInterceptor.class })
public class SubscriptionWsImpl extends BaseWs implements SubscriptionWs {

    @Inject
    private SubscriptionApi subscriptionApi;

    @Override
    public ActionStatus create(SubscriptionDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.create(postData, getCurrentUser());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(SubscriptionDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.update(postData, getCurrentUser());
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus instantiateServices(InstantiateServicesRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.instantiateServices(postData, getCurrentUser());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus activateServices(ActivateServicesRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.activateServices(postData, null, getCurrentUser(), false);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus applyOneShotChargeInstance(ApplyOneShotChargeInstanceRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.applyOneShotChargeInstance(postData, getCurrentUser());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
    
	@Override
	public ActionStatus applyProduct(ApplyProductRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
        	subscriptionApi.applyProduct(postData, getCurrentUser());
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
	}
	
    @Override
    public ActionStatus terminateSubscription(TerminateSubscriptionRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.terminateSubscription(postData, ChargeInstance.NO_ORDER_NUMBER, getCurrentUser());
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public ActionStatus terminateServices(TerminateSubscriptionServicesRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.terminateServices(postData, ChargeInstance.NO_ORDER_NUMBER, getCurrentUser());
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

    @Override
    public SubscriptionsResponseDto listSubscriptionByUserAccount(String userAccountCode) {
        SubscriptionsResponseDto result = new SubscriptionsResponseDto();

        try {
            result.setSubscriptions(subscriptionApi.listByUserAccount(userAccountCode, getCurrentUser().getProvider()));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

    @Override
    public GetSubscriptionResponseDto findSubscription(String subscriptionCode) {
        GetSubscriptionResponseDto result = new GetSubscriptionResponseDto();

        try {
            result.setSubscription(subscriptionApi.findSubscription(subscriptionCode, getCurrentUser().getProvider()));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

    @Override
    public ActionStatus createOrUpdateSubscription(SubscriptionDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            subscriptionApi.createOrUpdate(postData, getCurrentUser());
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
    }

	@Override
	public ActionStatus suspendSubscription(SuspendSubscriptionRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.suspendSubscription(postData.getSubscriptionCode(), postData.getSuspensionDate(), getCurrentUser());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
	}

	@Override
	public ActionStatus resumeSubscription(SuspendSubscriptionRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.resumeSubscription(postData.getSubscriptionCode(), postData.getSuspensionDate(), getCurrentUser());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
	}

	@Override
	public ActionStatus suspendServices(SuspendServicesRequestDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            subscriptionApi.suspendServices(postData, getCurrentUser());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
	}

	@Override
	public ActionStatus resumeServices(SuspendServicesRequestDto postData) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            subscriptionApi.resumeServices(postData, getCurrentUser());
        } catch (Exception e) {
            processException(e, result);
        }
        return result;
	}
}
