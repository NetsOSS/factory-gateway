package eu.nets.factory.gateway.web;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@Transactional
public class ScenarioController {

    private final Logger log = getLogger(getClass());

//    @Autowired
//    private ScenarioRepository scenarioRepository;
//
//    @Autowired
//    private InvoiceRestClient invoiceRestClient;
//
//    @Autowired
//    private TestBenchSettings testBenchSettings;
//
//    @Autowired
//    private KundeportalWebServiceImpl kundeportalWebServiceImpl;

    @RequestMapping(method = GET, value = "/data/scenario", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ScenarioModel> getScenarios() {
        List<ScenarioModel> scenarios = new ArrayList<>();
//        for (Scenario scenario : customer.getScenarios()) {
//            scenarios.add(new ScenarioModel(scenario));
//        }
        return scenarios;
    }

/*
    @RequestMapping(method = GET, value = "/data/scenario/{scenarioType}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ScenarioModel getScenarioByType(TestBenchCustomer customer, @PathVariable String scenarioType) {
        Scenario scenario = customer.findScenarioType(ScenarioType.valueOf(scenarioType));
        return new ScenarioModel(scenario);
    }

    @RequestMapping(method = POST, value = "/data/scenario/{scenarioType}/validate-and-submit", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ValidateAndSubmitResponse runValidation(TestBenchCustomer customer, @PathVariable String scenarioType, @RequestParam("file") MultipartFile file) throws Exception {
        Meldingstype meldingstype = EHF_2;

        ScenarioType type = ScenarioType.valueOf(scenarioType);

        Scenario scenario = customer.findScenarioType(type);

        log.info("Uploading new file for scenario {}", type);

        File f = testBenchSettings.fakeFile();
        byte[] buffer;
        if (f != null) {
            buffer = IOUtils.toByteArray(new FileInputStream(f));
        } else {
            buffer = file.getBytes();
        }

        SchematronError[] schematronErrors;

        if (testBenchSettings.fakeValidation()) {
            if(type == INVALID_INVOICE) {
                schematronErrors = new SchematronError[]{
                        new SchematronError(null, "UBL-T10-BiiProfiles.xsl:[BIIPROFILE-T10-R001]-An invoice transaction T10 must only be used in Profiles 4, 5, 6 or xy.", ". = 'urn:www.cenbii.eu:profile:bii04:ver2.0' or . = 'urn:www.cenbii.eu:profile:bii05:ver2.0' or . = 'urn:www.cenbii.eu:profile:bii06:ver2.0' or . = 'urn:www.cenbii.eu:profile:biixy:ver2.0'", "*/
/*:Invoice[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:Invoice-2'][1]*/
/*:ProfileID[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2'][1]"),
                        new SchematronError(null, "UBL-T10-BiiRulesCodes.xsl:[CL-010-009]-UBL version  must be 2.1", "( ( not(contains(normalize-space(.),' ')) and contains( ' 2.1 ',concat(' ',normalize-space(.),' ') ) ) )", "*/
/*:Invoice[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:Invoice-2'][1]*/
/*:UBLVersionID[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonBasicComponents-2'][1]"),
                        new SchematronError(null, "UBL-T10-Nogov.xsl:[NOGOV-T10-R015]-Registration name for AccountingCustomerParty MUST be provided according to EHF.", "(cac:PartyLegalEntity/cbc:RegistrationName != '') and (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'NO') or not((//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'NO')) or               (//cac:AdditionalDocumentReference/cbc:DocumentType = 'efakturareferanse')", "*/
/*:Invoice[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:Invoice-2'][1]*/
/*:AccountingCustomerParty[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]*/
/*:Party[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]"),
                        new SchematronError(null, "UBL-T10-Nonat.xsl:[NONAT-T10-R008]-The Norwegian legal registration name for the supplier MUST be provided according to \"FOR 2004-12-01 nr 1558 - § 5-1-1. Point 2\"", "(cac:PartyLegalEntity/cbc:RegistrationName != '') and (//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'NO') or not((//cac:AccountingSupplierParty/cac:Party/cac:PostalAddress/cac:Country/cbc:IdentificationCode = 'NO'))", "*/
/*:Invoice[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:Invoice-2'][1]*/
/*:AccountingSupplierParty[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]*/
/*:Party[namespace-uri()='urn:oasis:names:specification:ubl:schema:xsd:CommonAggregateComponents-2'][1]")
                };
            } else {
                schematronErrors = new SchematronError[0];
            }
        } else {
            try {
                schematronErrors = invoiceRestClient.validate(buffer, meldingstype);
            } catch (IOException e) {
                log.warn("Error while validating file", e);
                return new ValidateAndSubmitResponse(file.getOriginalFilename(), TECHNICAL_ERROR, null, null, false);
            }
        }

        boolean validationError = schematronErrors.length > 0;

        boolean canProceed = type == VALID_INVOICE && !validationError ||
                type == INVALID_INVOICE && validationError;

        Long fileId = null;
        if (canProceed) {
            fileId = invoiceRestClient.uploadFile(customer.getOrgno(), file, meldingstype);

            scenario.setFileId(fileId);
        }

        if (validationError) {
            // TODO: save schematron errors on scenario
        }

        log.info("scenario = " + scenario);
        scenarioRepository.save(scenario);
        log.info("scenario = " + scenario);

        ValidationResult validationResult = validationError ? VALIDATION_FAILURE : OK;
        return new ValidateAndSubmitResponse(file.getOriginalFilename(), validationResult, fileId, schematronErrors,
                canProceed);
    }
    
    
    
    @RequestMapping(method = GET, value = "/data/scenario/{scenarioType}/processing-result", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProcessingResult getProcessingResult(TestBenchCustomer customer, @PathVariable String scenarioType) throws Exception {
        //ScenarioType type = ScenarioType.valueOf(scenarioType);
    	
       // Scenario scenario = customer.findScenarioType(type);
        
    	ForsendelseStatus forsendelseStatus=invoiceRestClient.getProcessingResut(982529743);
        ProcessingResult processResult =null;
        Long forsendelseId=Long.valueOf(forsendelseStatus.getForsendelseId());
    	switch (forsendelseStatus.getStatus()) {
		case  INITIELL:
			processResult = new ProcessingResult(Boolean.FALSE,ScenarioStatus.INPROGRESS);
			break;
		case TIL_BEHANDLING:
			processResult = new ProcessingResult(Boolean.FALSE,ScenarioStatus.INPROGRESS);
			break;
		case BEHANDLET:
			processResult = new ProcessingResult(Boolean.TRUE,ScenarioStatus.VALID);
			processResult.setNotificationInfo(invoiceRestClient.getNotificationReport(forsendelseId));
			processResult.setReceiptInfo(invoiceRestClient.getReceiptDetails(forsendelseId));
			//hardcoding bbsref for now to get the docUrl,should be removed later
			processResult.setDocumentURL(kundeportalWebServiceImpl.getDocumentUrl(5393997L));
            processResult.setEmail(customer.getNotificationEmail());
			break;
		case AVVIST:
			processResult = new ProcessingResult(Boolean.TRUE,ScenarioStatus.FAILURE);
		default:
			throw new RuntimeException("Not a Valid Forsendelse Status");	
		}
       return processResult;
    }
    
    
    
    @RequestMapping(method = GET, value = "/scenario/VALID_INVOICE/{fileId}/download", produces = {"text/xml", "application/pdf", "application/zip", "application/octet-stream"})
    @ResponseBody
    public void getFileContent(@PathVariable Long fileId,HttpServletResponse response) throws Exception {
       
    	InputStream inputStreams=null;
        
        try {
        		ResponseEntity<byte[]> responseEntity=invoiceRestClient.getFileContent(fileId);
        	  // inputStreams=invoiceRestClient.getFileContent(fileId);
        		//HttpHeaders headers=responseEntity.getHeaders();
    	        response.setHeader("Content-Disposition", "attachment; filename=\"receipt.xml\"" );
    	        response.addHeader("Content-Type", "application/octet-stream");
        	   IOUtils.copy(new ByteArrayInputStream(responseEntity.getBody()), response.getOutputStream());
        	   
	       
		} catch (IOException e) {
		    log.error("Unable to download filecontent for fild id {} " + fileId);  
		    IOUtils.write(e.getMessage(), response.getOutputStream());
        } finally {
		    IOUtils.closeQuietly(inputStreams);
		}
       
    }
    
    
    @RequestMapping(method = POST, value = "/data/scenario/update/{notifcationId}", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String,Boolean> resendReport(@PathVariable Long notifcationId) throws Exception {
     
    	Map<String, Boolean> map= new HashMap<String,Boolean>();
    	try{
    	invoiceRestClient.resendReport(notifcationId);
    	map.put("updateStatus", Boolean.TRUE);
    	}
    	catch(RuntimeException e){
    		map.put("updateStatus", Boolean.FALSE);
    		 log.error("Unable to resend report for notification id {} " + notifcationId);
    	}
    	return map ;
    }
    
    
    
    */
    public static class ScenarioModel {
    }

    /*

    enum ValidationResult {
        OK, TECHNICAL_ERROR, VALIDATION_FAILURE
    }

    public static class ValidateAndSubmitResponse {
        public String fileName;

        public ValidationResult status;

        public Long fileId;

        public SchematronError[] schematronErrors;

        public boolean canProceed;

        public ValidateAndSubmitResponse(String fileName, ValidationResult status, Long fileId, SchematronError[] schematronErrors, boolean canProceed) {
            this.fileName = fileName;
            this.status = status;
            this.fileId = fileId;
            this.schematronErrors = schematronErrors;
            this.canProceed = canProceed;
        }
    }

    public static class ProcessingResult {

        public boolean done;

        public ScenarioStatus status;

        public ReceiptInfo receiptInfo;

        public NotificationInfo notificationInfo;

        public List<String> documentURL;

        public String email;

        public ProcessingResult() {
        }

        public ProcessingResult(boolean done, ScenarioStatus receipt) {
            this.done = done;
            this.status = receipt;
        }

        public void setReceiptInfo(ReceiptInfo receiptInfo) {
            this.receiptInfo = receiptInfo;
        }

        public void setNotificationInfo(NotificationInfo notificationInfo) {
            this.notificationInfo = notificationInfo;
        }

        public void setDocumentURL(List<String> documentURL) {
            this.documentURL = documentURL;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
*/
}
