package org.meveo.admin.action.notification;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RejectedImportException;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.commons.utils.CsvReader;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.jobs.ScriptInstance;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.NotificationEventTypeEnum;
import org.meveo.model.notification.StrategyImportTypeEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.notification.EmailNotificationService;
import org.meveo.service.script.ScriptInstanceService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 * Standard backing bean for {@link EmailNotification} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class EmailNotificationBean extends BaseBean<EmailNotification> {

	private static final long serialVersionUID = 6473465285480945644L;

	@Inject
	private EmailNotificationService emailNotificationService;
	
	ParamBean paramBean = ParamBean.getInstance();
	
    @Inject
    CounterTemplateService counterTemplateService;
    
    @Inject
    ScriptInstanceService scriptInstanceService;
    
    CsvBuilder csv = null;
   	private String providerDir=paramBean.getProperty("providers.rootDir","/tmp/meveo_integr");
   	private String existingEntitiesCsvFile=null;
   	
	
	CsvReader csvReader = null;
    private UploadedFile file; 
    
    private StrategyImportTypeEnum strategyImportType;
    
    private static final int CODE= 0;
    private static final int CLASS_NAME_FILTER= 1;
    private static final int EVENT_TYPE_FILTER= 2; 
    private static final int EL_FILTER= 3;
    private static final int ACTIVE= 4; 
    private static final int SCRIPT_INSTANCE_CODE= 5;
    private static final int SENT_FROM= 6;
    private static final int SEND_TO_EL= 7;
    private static final int SEND_TO_MAILING_LIST= 8;
    private static final int SUBJECT= 9;
    private static final int TEXT_BODY= 10;
    private static final int HTML_BODY= 11;
    private static final int COUNTER_TEMPLATE= 12;

	public EmailNotificationBean() {
		super(EmailNotification.class);
	}

	@Override
	protected IPersistenceService<EmailNotification> getPersistenceService() {
		return emailNotificationService;
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}
	
    public void handleFileUpload(FileUploadEvent event) throws Exception {
        try {
            file = event.getFile();
            log.debug("File uploaded " + file.getFileName());
            upload();
            messages.info(new BundleKey("messages", "import.csv.successful"));
        } catch (Exception e) {
            log.error("Failed to handle uploaded file {}", event.getFile().getFileName(), e);
            messages.error(new BundleKey("messages", "import.csv.failed"), e.getClass().getSimpleName() + " " + e.getMessage());
        }
    }
 
    private void upload() throws IOException, BusinessException  {
        if (file == null) {
            return;
        }
        csvReader = new CsvReader(file.getInputstream(), ';', Charset.forName("ISO-8859-1"));
        csvReader.readHeaders();
        String existingEntitiesCSV = paramBean.getProperty("existingEntities.csv.dir", "existingEntitiesCSV");
        File dir = new File(providerDir + File.separator + getCurrentProvider().getCode() + File.separator + existingEntitiesCSV);
        dir.mkdirs();
        existingEntitiesCsvFile = dir.getAbsolutePath() + File.separator + "EmailNotifications_" + new SimpleDateFormat("ddMMyyyyHHmmSS").format(new Date()) + ".csv";
        csv = new CsvBuilder();
        boolean isEntityAlreadyExist = false;
        while (csvReader.readRecord()) {
            String[] values = csvReader.getValues();
            EmailNotification existingEntity = emailNotificationService.findByCode(values[CODE], getCurrentProvider());
            if (existingEntity != null) {
                checkSelectedStrategy(values, existingEntity, isEntityAlreadyExist);
                isEntityAlreadyExist = true;
            } else {
                EmailNotification emailNotif = new EmailNotification();
                emailNotif.setCode(values[CODE]);
                emailNotif.setClassNameFilter(values[CLASS_NAME_FILTER]);
                emailNotif.setEventTypeFilter(NotificationEventTypeEnum.valueOf(values[EVENT_TYPE_FILTER]));
                emailNotif.setElFilter(values[EL_FILTER]);
                emailNotif.setDisabled(Boolean.parseBoolean(values[ACTIVE]));
                if (!StringUtils.isBlank(values[SCRIPT_INSTANCE_CODE])) {
                    ScriptInstance scriptInstance = scriptInstanceService.findByCode(values[SCRIPT_INSTANCE_CODE], getCurrentProvider()); 
                    emailNotif.setScriptInstance(scriptInstance);
                }  
                emailNotif.setEmailFrom(values[SENT_FROM]);
                emailNotif.setEmailToEl(values[SEND_TO_EL]);
                String emails = values[SEND_TO_MAILING_LIST].replace("[","").replace("]","");
                if (!StringUtils.isBlank(emails)) {
                    String[] emailList = emails.split(",");
                    List<String> listMail = Arrays.asList(emailList);
                    for (String email : listMail) {
                    	    email=email.trim();
                        if (emailNotif.getEmails() == null) {
                            emailNotif.setEmails(new HashSet<String>());
                        }
                        emailNotif.getEmails().add(email);
                    }
                }
                emailNotif.setSubject(values[SUBJECT]);
                emailNotif.setBody(values[TEXT_BODY]);
                emailNotif.setHtmlBody(values[HTML_BODY]);
                if (!StringUtils.isBlank(values[COUNTER_TEMPLATE])) {
                    CounterTemplate counterTemplate = counterTemplateService.findByCode(values[COUNTER_TEMPLATE], getCurrentProvider());
                    emailNotif.setCounterTemplate(counterTemplate != null ? counterTemplate : null);
                }

                emailNotificationService.create(emailNotif);
            }
        }
        if (isEntityAlreadyExist && strategyImportType.equals(StrategyImportTypeEnum.REJECT_EXISTING_RECORDS)) {
            csv.writeFile(csv.toString().getBytes(), existingEntitiesCsvFile);
        }
    }

	public void checkSelectedStrategy(String[] values,EmailNotification existingEntity,boolean isEntityAlreadyExist) throws RejectedImportException, IOException{
		if(strategyImportType.equals(StrategyImportTypeEnum.UPDATED)){
			existingEntity.setClassNameFilter(values[CLASS_NAME_FILTER]);
			existingEntity.setEventTypeFilter(NotificationEventTypeEnum
					.valueOf(values[EVENT_TYPE_FILTER]));
			existingEntity.setElFilter(values[EL_FILTER]);
			existingEntity.setDisabled(Boolean.parseBoolean(values[ACTIVE]));
            if (!StringUtils.isBlank(values[SCRIPT_INSTANCE_CODE])) {
                ScriptInstance scriptInstance = scriptInstanceService.findByCode(values[SCRIPT_INSTANCE_CODE], getCurrentProvider()); 
                existingEntity.setScriptInstance(scriptInstance);
            }  
			existingEntity.setEmailFrom(values[SENT_FROM]);
			existingEntity.setEmailToEl(values[SEND_TO_EL]);
			String emails = values[SEND_TO_MAILING_LIST].replace("[","").replace("]","");;
			if (!StringUtils.isBlank(emails)) {
				String[] emailList = emails.split(",");
				List<String> listMail = Arrays.asList(emailList);
				for (String email : listMail) {
					    email=email.trim();
					if (existingEntity.getEmails() == null) {
						existingEntity
								.setEmails(new HashSet<String>());
					}
					existingEntity.getEmails().add(email);
				}
			}
			existingEntity.setSubject(values[SUBJECT]);
			existingEntity.setBody(values[TEXT_BODY]);
			existingEntity.setHtmlBody(values[HTML_BODY]);
			if(!StringUtils.isBlank(values[COUNTER_TEMPLATE])){
				CounterTemplate counterTemplate=counterTemplateService.findByCode(values[COUNTER_TEMPLATE], getCurrentProvider());
				existingEntity.setCounterTemplate(counterTemplate!=null ?counterTemplate: null);
			}
			emailNotificationService.update(existingEntity);
		          }
		else if(strategyImportType.equals(StrategyImportTypeEnum.REJECTE_IMPORT)){
			  throw new RejectedImportException("notification.rejectImport");
			}
		else if (strategyImportType.equals(StrategyImportTypeEnum.REJECT_EXISTING_RECORDS)) {
			if(!isEntityAlreadyExist){		
				csv.appendValue("Code");
				csv.appendValue("Classename filter");
				csv.appendValue("Event type filter");
				csv.appendValue("El filter");
				csv.appendValue("Active");
				csv.appendValue("Script instance code");
				csv.appendValue("Sent from");
				csv.appendValue("Send to EL");
				csv.appendValue("Send to mailing list");
				csv.appendValue("Subject");
				csv.appendValue("Text body");
				csv.appendValue("HTML body");
				csv.appendValue("Counter template");
			}
			csv.startNewLine();
			csv.appendValue(values[CODE]);
			csv.appendValue(values[CLASS_NAME_FILTER]);
			csv.appendValue(values[EVENT_TYPE_FILTER]);
			csv.appendValue(values[EL_FILTER]);
			csv.appendValue(values[ACTIVE]);
			csv.appendValue(values[SCRIPT_INSTANCE_CODE]);
			csv.appendValue(values[SENT_FROM]);
			csv.appendValue(values[SEND_TO_EL]);
			csv.appendValue(values[SEND_TO_MAILING_LIST]);
			csv.appendValue(values[SUBJECT]);
			csv.appendValue(values[TEXT_BODY]);
			csv.appendValue(values[HTML_BODY]);
			csv.appendValue(values[COUNTER_TEMPLATE]);
		}}
	
 

	public StrategyImportTypeEnum getStrategyImportType() {
		return strategyImportType;
	}

	public void setStrategyImportType(StrategyImportTypeEnum strategyImportType) {
		this.strategyImportType = strategyImportType;
	}
}


