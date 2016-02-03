package org.meveo.api.ws.impl;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jws.WebService;

import org.meveo.api.CustomEntityApi;
import org.meveo.api.CustomFieldTemplateApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CustomEntityInstanceDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.CustomFieldTemplateDto;
import org.meveo.api.dto.EntityActionScriptDto;
import org.meveo.api.dto.EntityCustomizationDto;
import org.meveo.api.dto.response.CustomEntityInstanceResponseDto;
import org.meveo.api.dto.response.CustomEntityTemplateResponseDto;
import org.meveo.api.dto.response.EntityActionScriptResponseDto;
import org.meveo.api.dto.response.EntityCustomizationResponseDto;
import org.meveo.api.dto.response.GetCustomFieldTemplateReponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.logging.LoggingInterceptor;
import org.meveo.api.ws.EntityCustomizationWs;
import org.meveo.model.customEntities.CustomEntityTemplate;

/**
 * @author Andrius Karpavicius
 **/
@WebService(serviceName = "EntityCustomizationWs", endpointInterface = "org.meveo.api.ws.EntityCustomizationWs")
@Interceptors({ LoggingInterceptor.class })
public class EntityCustomizationWsImpl extends BaseWs implements EntityCustomizationWs {

    @Inject
    private CustomFieldTemplateApi customFieldTemplateApi;

    @Inject
    private CustomEntityApi customEntityApi;

    @Inject
    private ScriptInstanceApi scriptInstanceApi;

    @Override
    public ActionStatus createField(CustomFieldTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.create(postData, null, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public ActionStatus updateField(CustomFieldTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.update(postData, null, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public ActionStatus removeField(String customFieldTemplateCode, String appliesTo) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customFieldTemplateApi.remove(customFieldTemplateCode, appliesTo, getCurrentUser().getProvider());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public GetCustomFieldTemplateReponseDto findField(String customFieldTemplateCode, String appliesTo) {
        GetCustomFieldTemplateReponseDto result = new GetCustomFieldTemplateReponseDto();

        try {
            result.setCustomFieldTemplate(customFieldTemplateApi.find(customFieldTemplateCode, appliesTo, getCurrentUser().getProvider()));
        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            result.getActionStatus().setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public ActionStatus createOrUpdateField(CustomFieldTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        try {
            customFieldTemplateApi.createOrUpdate(postData, null, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public CustomEntityTemplateResponseDto findEntityTemplate(String code) {

        CustomEntityTemplateResponseDto result = new CustomEntityTemplateResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setCustomEntityTemplate(customEntityApi.findEntityTemplate(code, getCurrentUser()));

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public ActionStatus removeEntityTemplate(String code) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.removeEntityTemplate(code, getCurrentUser());

        } catch (Exception e) {
            super.processException(e, result);
        }

        log.debug("RESPONSE={}", result);
        return result;

    }

    @Override
    public ActionStatus createEntityTemplate(CustomEntityTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.createEntityTemplate(postData, getCurrentUser());

        } catch (Exception e) {
            super.processException(e, result);
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public ActionStatus updateEntityTemplate(CustomEntityTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.updateEntityTemplate(postData, getCurrentUser());

        } catch (Exception e) {
            super.processException(e, result);
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public ActionStatus createOrUpdateEntityTemplate(CustomEntityTemplateDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.createOrUpdateEntityTemplate(postData, getCurrentUser());

        } catch (Exception e) {
            super.processException(e, result);
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public CustomEntityInstanceResponseDto findCustomEntityInstance(String cetCode, String code) {

        CustomEntityInstanceResponseDto result = new CustomEntityInstanceResponseDto();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setCustomEntityInstance(customEntityApi.findEntityInstance(cetCode, code, getCurrentUser(CustomEntityTemplate.getPermissionResourceName(cetCode), "read")));

        } catch (Exception e) {
            super.processException(e, result.getActionStatus());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public ActionStatus removeCustomEntityInstance(String cetCode, String code) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.removeEntityInstance(cetCode, code, getCurrentUser(CustomEntityTemplate.getPermissionResourceName(cetCode), "modify"));

        } catch (Exception e) {
            super.processException(e, result);
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public ActionStatus createCustomEntityInstance(CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.createEntityInstance(dto, getCurrentUser(CustomEntityTemplate.getPermissionResourceName(dto.getCetCode()), "modify"));

        } catch (Exception e) {
            super.processException(e, result);
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public ActionStatus updateCustomEntityInstance(CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.updateEntityInstance(dto, getCurrentUser(CustomEntityTemplate.getPermissionResourceName(dto.getCetCode()), "modify"));

        } catch (Exception e) {
            super.processException(e, result);
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public ActionStatus createOrUpdateCustomEntityInstance(CustomEntityInstanceDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.createOrUpdateEntityInstance(dto, getCurrentUser(CustomEntityTemplate.getPermissionResourceName(dto.getCetCode()), "modify"));

        } catch (Exception e) {
            super.processException(e, result);
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    /**
     * Define a new entity action
     * 
     * @param dto
     * @return
     */
    @Override
    public ActionStatus createAction(EntityActionScriptDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            scriptInstanceApi.create(dto, null, getCurrentUser());

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    /**
     * Update existing entity action definition
     * 
     * @param dto
     * @return
     */
    @Override
    public ActionStatus updateAction(EntityActionScriptDto dto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            scriptInstanceApi.update(dto, null, getCurrentUser());

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    /**
     * Remove entity action definition given its code and entity it applies to
     * 
     * @param actionCode Entity action code
     * @param appliesTo Entity that action applies to
     * @return
     */
    @Override
    public ActionStatus removeAction(String actionCode, String appliesTo) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            scriptInstanceApi.removeEntityAction(actionCode, appliesTo, getCurrentUser());

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    /**
     * Get entity action definition
     * 
     * @param actionCode Entity action code
     * @param appliesTo Entity that action applies to
     * @return
     */
    @Override
    public EntityActionScriptResponseDto findAction(String actionCode, String appliesTo) {

        EntityActionScriptResponseDto result = new EntityActionScriptResponseDto();

        try {
            result.setEntityAction(scriptInstanceApi.findEntityAction(actionCode, appliesTo, getCurrentUser()));

        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            result.getActionStatus().setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    /**
     * Define new or update existing entity action definition
     * 
     * @param dto
     * @return
     */
    @Override
    public ActionStatus createOrUpdateAction(EntityActionScriptDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            scriptInstanceApi.createOrUpdate(dto, null, getCurrentUser());
        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

    @Override
    public ActionStatus customizeEntity(EntityCustomizationDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customEntityApi.customizeEntity(dto, getCurrentUser());

        } catch (MeveoApiException e) {
            result.setErrorCode(e.getErrorCode());
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        } catch (Exception e) {
            result.setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.setStatus(ActionStatusEnum.FAIL);
            result.setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;

    }

    /**
     * Get customizations made on a standard Meveo entity given its class
     * 
     * @param customizedEntityClass Standard Meveo entity class name
     */
    @Override
    public EntityCustomizationResponseDto findEntityCustomizations(String customizedEntityClass) {

        EntityCustomizationResponseDto result = new EntityCustomizationResponseDto();

        try {
            result.setEntityCustomization(customEntityApi.findEntityCustomizations(customizedEntityClass, getCurrentUser()));

        } catch (MeveoApiException e) {
            result.getActionStatus().setErrorCode(e.getErrorCode());
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        } catch (Exception e) {
            result.getActionStatus().setErrorCode(MeveoApiErrorCodeEnum.GENERIC_API_EXCEPTION);
            result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
            result.getActionStatus().setMessage(e.getMessage());
        }

        log.debug("RESPONSE={}", result);
        return result;
    }

}