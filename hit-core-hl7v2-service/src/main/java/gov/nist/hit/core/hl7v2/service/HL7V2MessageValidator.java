/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */
package gov.nist.hit.core.hl7v2.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import gov.nist.healthcare.unified.enums.Context;
import gov.nist.healthcare.unified.model.EnhancedReport;
import gov.nist.healthcare.unified.proxy.ValidationProxy;
import gov.nist.hit.core.domain.Domain;
import gov.nist.hit.core.domain.MessageValidationCommand;
import gov.nist.hit.core.domain.MessageValidationResult;
import gov.nist.hit.core.domain.TestContext;
import gov.nist.hit.core.hl7v2.domain.HL7V2TestContext;
import gov.nist.hit.core.service.AccountService;
import gov.nist.hit.core.service.DomainService;
import gov.nist.hit.core.service.MessageValidator;
import gov.nist.hit.core.service.ValidationLogService;
import gov.nist.hit.core.service.exception.MessageException;
import gov.nist.hit.core.service.exception.MessageValidationException;
import gov.nist.hit.logging.HITStatsLogger;
import hl7.v2.validation.content.ConformanceContext;
import hl7.v2.validation.content.DefaultConformanceContext;
import hl7.v2.validation.vs.ValueSetLibrary;
import hl7.v2.validation.vs.ValueSetLibraryImpl;

public abstract class HL7V2MessageValidator implements MessageValidator {

	@Autowired
	private ValidationLogService validationLogService;
	
	@Autowired
	private DomainService domainService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private ValidationProxy vp;

	@Override
	public MessageValidationResult validate(TestContext testContext, MessageValidationCommand command)
			throws MessageValidationException {
		try {
			EnhancedReport report = generateReport(testContext, command);
			if (report != null) {
				Map<String, String> nav = command.getNav();
				if (nav != null && !nav.isEmpty()) {
					report.setTestCase(nav.get("testPlan"), nav.get("testGroup"), nav.get("testCase"),
							nav.get("testStep"));
				}
				return new MessageValidationResult(report.to("json").toString(), report.render("report", null));
			}
			throw new MessageValidationException();
		} catch (MessageException e) {
			throw new MessageValidationException(e.getLocalizedMessage());
		} catch (RuntimeException e) {
			throw new MessageValidationException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new MessageValidationException(e.getLocalizedMessage());
		}
	}

	public EnhancedReport generateReport(TestContext testContext, MessageValidationCommand command)
			throws MessageValidationException {
		try {
			if (testContext instanceof HL7V2TestContext) {
				
				vp.setInfo(getValidationServiceName(), getProviderName());
				
				HL7V2TestContext v2TestContext = (HL7V2TestContext) testContext;
				EnhancedReport report = null;
				String contextType = command.getContextType();
				String message = getMessageContent(command);
				String conformanceProfielId = v2TestContext.getConformanceProfile().getSourceId();
				
				
				
				String username = accountService.findOne(command.getUserId()) != null ? accountService.findOne(command.getUserId()).getUsername() : "guest";
				String organization = "";
				String operation = "message validation";
				
				
				String c1 = v2TestContext.getConstraints() != null ? v2TestContext.getConstraints().getXml() : null;
				String c2 = v2TestContext.getAddditionalConstraints() != null
						? v2TestContext.getAddditionalConstraints().getXml() : null;
				
				
				//for external validation
				List<String> constraintList = new ArrayList<String>();
				constraintList.add(c1);
				constraintList.add(c2);
				
				
//				InputStream c1Stream = c1 != null ? IOUtils.toInputStream(c1, StandardCharsets.UTF_8) : null;
//				InputStream c1Stream_2 = c1 != null ? IOUtils.toInputStream(c1, StandardCharsets.UTF_8) : null;
//				InputStream c2Stream = c2 != null ? IOUtils.toInputStream(c2, StandardCharsets.UTF_8) : null;
//				InputStream c2Stream_2 = c2 != null ? IOUtils.toInputStream(c2, StandardCharsets.UTF_8) : null;
//
//				List<InputStream> cStreams = new ArrayList<InputStream>();				
//				if (c1Stream != null)
//					cStreams.add(c1Stream);
//				if (c2Stream != null)
//					cStreams.add(c2Stream);
//				List<InputStream> cStreams_2 = new ArrayList<InputStream>();				
//				if (c1Stream_2 != null)
//					cStreams_2.add(c1Stream_2);
//				if (c2Stream_2 != null)
//					cStreams_2.add(c2Stream_2);
//				
//				ConformanceContext c = getConformanceContext(cStreams);				
//				ValidationProxy vp = new ValidationProxy(getValidationServiceName(), getProviderName());
				
				
				
//				Reader configuration = null;
				String conf = null;
				Domain domain = domainService.findOneByKey(v2TestContext.getDomain());
				if (domain != null) {
					conf = domain.getValidationConfiguration();
//					if (conf != null) {
//						configuration = new StringReader(conf);
//					}				
				}
				
						
								
//				InputStream valueSetLibraryIS = null ,valueSetBindingsIS = null,coConstraintsIS= null,slicingsIS = null;
//				if (v2TestContext.getVocabularyLibrary() != null) {
//					valueSetLibraryIS = IOUtils.toInputStream(v2TestContext.getVocabularyLibrary().getXml(), StandardCharsets.UTF_8);
//				}
//				if (v2TestContext.getValueSetBindings() != null) {
//					valueSetBindingsIS = IOUtils.toInputStream(v2TestContext.getValueSetBindings().getXml(), StandardCharsets.UTF_8);
//				}
//				if (v2TestContext.getCoConstraints() != null) {
//					coConstraintsIS = IOUtils.toInputStream(v2TestContext.getCoConstraints().getXml(), StandardCharsets.UTF_8);
//				}
//				if (v2TestContext.getSlicings() != null) {
//					slicingsIS = IOUtils.toInputStream(v2TestContext.getSlicings().getXml(), StandardCharsets.UTF_8);
//				}
				
				

							
				//configure external value set validation/fetching
//				SSLContextBuilder builder = new SSLContextBuilder();
//				RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(2 * 1000).setConnectTimeout(2 * 1000)
//						.setSocketTimeout(2 * 1000).build();
//				SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);
//
//				CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).disableCookieManagement()
//						.setSSLSocketFactory(socketFactory).addInterceptorFirst(new HttpRequestInterceptor() {
//							@Override
//							public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
//								context.getAttribute(ExternalValueSetClient.HTTP_CONTEXT_VS_BINDING_IDENTIFIER).toString();
//								request.addHeader("X-API-KEY", v2TestContext
//										.getKeyFromIdentifier(context.getAttribute(ExternalValueSetClient.HTTP_CONTEXT_VS_BINDING_IDENTIFIER).toString()));
//							}
//						}).build();
				
					
						
				//get hl7v2 validation version if specified
				String externalValidationVersion = null;
				if (v2TestContext.getHl7v2ValidationVersion() != null && !v2TestContext.getHl7v2ValidationVersion().isEmpty()) {
					externalValidationVersion = v2TestContext.getHl7v2ValidationVersion();
				}else if (domain.getHl7v2ValidationVersion() != null && !domain.getHl7v2ValidationVersion().isEmpty()) {
					externalValidationVersion = domain.getHl7v2ValidationVersion();
				}
				
				
					
//						call external service
						report = vp.validate(message,
							v2TestContext.getConformanceProfile().getXml(),
							v2TestContext.getVocabularyLibrary().getXml(),
							constraintList,
							v2TestContext.getValueSetBindings() != null ? v2TestContext.getValueSetBindings().getXml() : null,
							v2TestContext.getCoConstraints() != null ? v2TestContext.getCoConstraints().getXml() : null,
							v2TestContext.getSlicings() != null ? v2TestContext.getSlicings().getXml() : null,
							conformanceProfielId,
							Context.valueOf(contextType),
							conf,
							v2TestContext.getApiHashMap(),externalValidationVersion);
					
						//not local validation dependency
//						report = vp.validateNew(message,
//								v2TestContext.getConformanceProfile().getXml(),
//								valueSetLibraryIS,
//								cStreams_2,
//								valueSetBindingsIS,
//								coConstraintsIS,
//								slicingsIS,
//								conformanceProfielId,
//								Context.valueOf(contextType),
//								configuration,httpClient);
												
					HITStatsLogger.log(username, organization, operation, testContext.getDomain());					
		
				
				
				if (report != null) {
					Map<String, String> nav = command.getNav();
					if (nav != null && !nav.isEmpty()) {
						report.setTestCase(nav.get("testPlan"), nav.get("testGroup"), nav.get("testCase"),
								nav.get("testStep"));
					}
				}

				validationLogService.generateAndSave(command.getUserId(), testContext, report);
				return report;
			}
			throw new MessageValidationException();
		} catch (MessageException e) {
			throw new MessageValidationException(e.getLocalizedMessage());
		} catch (RuntimeException e) {
			throw new MessageValidationException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new MessageValidationException(e.getLocalizedMessage());
		}
	}

	protected ConformanceContext getConformanceContext(List<InputStream> confContexts) {
		ConformanceContext c = DefaultConformanceContext.apply(confContexts).get();
		return c;
	}

	protected ValueSetLibrary getValueSetLibrary(InputStream vsLibXML) {
		ValueSetLibrary valueSetLibrary = ValueSetLibraryImpl.apply(vsLibXML).get();
		return valueSetLibrary;
	}

	public static String getMessageContent(MessageValidationCommand command) throws MessageException {
		String message = command.getContent();
		if (message == null) {
			throw new MessageException("No message provided");
		}
		return message;
	}

	private String organizationName;

	@Override
	public String getProviderName() {
		// TODO Auto-generated method stub
		return organizationName != null ? organizationName : "NIST";
	}

	@Override
	public String getValidationServiceName() {
		// TODO Auto-generated method stub
		return getProviderName() + " Validation Tool";
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public ValidationLogService getValidationLogService() {
		return validationLogService;
	}

	public void setValidationLogService(ValidationLogService validationLogService) {
		this.validationLogService = validationLogService;
	}

}