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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import gov.nist.auth.hit.core.domain.Account;
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
import hl7.v2.validation.vs.external.client.ExternalValueSetClient;

public abstract class HL7V2MessageValidator implements MessageValidator {

	@Autowired
	private ValidationLogService validationLogService;
	
	@Autowired
	private DomainService domainService;
	
	@Autowired
	private AccountService accountService;

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
				HL7V2TestContext v2TestContext = (HL7V2TestContext) testContext;
				EnhancedReport report = null;
				String contextType = command.getContextType();
				String message = getMessageContent(command);
				String conformanceProfielId = v2TestContext.getConformanceProfile().getSourceId();
				
				
				
				Account account = null;
				String username = accountService.findOne(command.getUserId()) != null ? accountService.findOne(command.getUserId()).getUsername() : "guest";
				String organization = "";
				String operation = "message validation";
				
				
				String c1 = v2TestContext.getConstraints() != null ? v2TestContext.getConstraints().getXml() : null;
				String c2 = v2TestContext.getAddditionalConstraints() != null
						? v2TestContext.getAddditionalConstraints().getXml() : null;
				InputStream c1Stream = c1 != null ? IOUtils.toInputStream(c1, StandardCharsets.UTF_8) : null;
				InputStream c1Stream_2 = c1 != null ? IOUtils.toInputStream(c1, StandardCharsets.UTF_8) : null;
				InputStream c2Stream = c2 != null ? IOUtils.toInputStream(c2, StandardCharsets.UTF_8) : null;
				InputStream c2Stream_2 = c2 != null ? IOUtils.toInputStream(c2, StandardCharsets.UTF_8) : null;

				List<InputStream> cStreams = new ArrayList<InputStream>();				
				if (c1Stream != null)
					cStreams.add(c1Stream);
				if (c2Stream != null)
					cStreams.add(c2Stream);
				List<InputStream> cStreams_2 = new ArrayList<InputStream>();				
				if (c1Stream_2 != null)
					cStreams_2.add(c1Stream_2);
				if (c2Stream_2 != null)
					cStreams_2.add(c2Stream_2);
				
				ConformanceContext c = getConformanceContext(cStreams);				
				ValidationProxy vp = new ValidationProxy(getValidationServiceName(), getProviderName());
				
				
				
				Reader configuration = null;
				Domain domain = domainService.findOneByKey(v2TestContext.getDomain());
				if (domain != null) {
					String conf = domain.getValidationConfiguration();
					if (conf != null) {
						configuration = new StringReader(conf);
					}				
				}
				
						
				
//				boolean newversion = true;		
				
				InputStream valueSetLibraryIS = null ,valueSetBindingsIS = null,coConstraintsIS= null,slicingsIS = null;
				if (v2TestContext.getVocabularyLibrary() != null) {
					valueSetLibraryIS = IOUtils.toInputStream(v2TestContext.getVocabularyLibrary().getXml(), StandardCharsets.UTF_8);
				}
				if (v2TestContext.getValueSetBindings() != null) {
					valueSetBindingsIS = IOUtils.toInputStream(v2TestContext.getValueSetBindings().getXml(), StandardCharsets.UTF_8);
				}
				if (v2TestContext.getCoConstraints() != null) {
					coConstraintsIS = IOUtils.toInputStream(v2TestContext.getCoConstraints().getXml(), StandardCharsets.UTF_8);
				}
				if (v2TestContext.getSlicings() != null) {
					slicingsIS = IOUtils.toInputStream(v2TestContext.getSlicings().getXml(), StandardCharsets.UTF_8);
				}
				
				
				//CloseableHttpClient httpClient = HttpClients.createDefault();// HttpClientBuilder.create().build();
//				CloseableHttpClient httpClient = HttpClients.createDefault();
				
				v2TestContext.getApikeys();
				
				SSLContextBuilder builder = new SSLContextBuilder();
				RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(2 * 1000).setConnectTimeout(2 * 1000)
						.setSocketTimeout(2 * 1000).build();
				SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(builder.build(), NoopHostnameVerifier.INSTANCE);

				CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).disableCookieManagement()
						.setSSLSocketFactory(socketFactory).addInterceptorFirst(new HttpRequestInterceptor() {
							@Override
							public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
								context.getAttribute(ExternalValueSetClient.HTTP_CONTEXT_VS_BINDING_IDENTIFIER).toString();
								request.addHeader("X-API-KEY", v2TestContext
										.getKeyFromIdentifier(context.getAttribute(ExternalValueSetClient.HTTP_CONTEXT_VS_BINDING_IDENTIFIER).toString()));
							}
						}).build();
				
					
				if (configuration != null) {					
//					if (newversion) {
						report = vp.validateNew(message,
								v2TestContext.getConformanceProfile().getXml(),
								valueSetLibraryIS,
								cStreams_2,
								valueSetBindingsIS,
								coConstraintsIS,
								slicingsIS,
								conformanceProfielId,
								Context.valueOf(contextType),
								configuration,httpClient);
//					}else {
						//to be removed 
//						String valueSets = v2TestContext.getVocabularyLibrary().getXml();
//						ValueSetLibrary vsLib = valueSets != null ? getValueSetLibrary(IOUtils.toInputStream(valueSets, StandardCharsets.UTF_8)) : null;
//						report = vp.validate(message, v2TestContext.getConformanceProfile().getXml(), c, vsLib,
//								conformanceProfielId, Context.valueOf(contextType),configuration);
//					}													
					HITStatsLogger.log(username, organization, operation, testContext.getDomain());					
				}else {
//					if (newversion) {
						report = vp.validateNew(message,
								v2TestContext.getConformanceProfile().getXml(),
								valueSetLibraryIS,
								cStreams_2,
								valueSetBindingsIS,
								coConstraintsIS,
								slicingsIS,
								conformanceProfielId,
								Context.valueOf(contextType),
								null,httpClient);
//					}else {
						//to be removed
//						String valueSets = v2TestContext.getVocabularyLibrary().getXml();
//						ValueSetLibrary vsLib = valueSets != null ? getValueSetLibrary(IOUtils.toInputStream(valueSets, StandardCharsets.UTF_8)) : null;
//						report = vp.validate(message, v2TestContext.getConformanceProfile().getXml(), c, vsLib,
//							conformanceProfielId, Context.valueOf(contextType));
//					}
					HITStatsLogger.log(username, organization, operation, testContext.getDomain());
				}
				
				
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