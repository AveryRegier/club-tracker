package com.github.averyregier.club.view;

import com.github.averyregier.club.application.ClubApplication;
import org.openid4java.OpenIDException;
import org.openid4java.association.AssociationSessionType;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.InMemoryConsumerAssociationStore;
import org.openid4java.consumer.InMemoryNonceVerifier;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * ConsumerService - Relying Party Service
 * NOTE: Some part of the code has been adopted from `OpenID4Java` library wiki.
 * */
public class ConsumerService {

	private static final String OPENID_IDENTIFIER = "openid_identifier";
	private static ConsumerManager manager = null;

	static {					
		try {
			manager = new ConsumerManager();
			manager.setAssociations(new InMemoryConsumerAssociationStore());
			manager.setNonceVerifier(new InMemoryNonceVerifier(5000));
			manager.setMinAssocSessEnc(AssociationSessionType.DH_SHA256);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}		

    public void init(ClubApplication app) {
        post("/consumer", "application/x-www-form-urlencoded", (httpRequest, httpResponse) -> {
            try {
                if ("true".equals(httpRequest.queryParams("is_return"))) {
                    ParameterList paramList = new ParameterList(httpRequest.params());
                    return this.processReturn(httpRequest, httpResponse, app, paramList);
                } else {
                    String identifier = httpRequest.queryParams(OPENID_IDENTIFIER);
                    if (identifier != null) {
                        return this.authRequest(identifier, httpRequest, httpResponse);
                    } else {

                        return new ModelAndView(new HashMap<>(), "index.ftl");
                    }
                }
            } catch (ServletException | IOException e) {
                throw new RuntimeException(e);
            }
        }, new FreeMarkerEngine());

        get("/consumer", (httpRequest, httpResponse) -> {
            try {
                if ("true".equals(httpRequest.queryParams("is_return"))) {
                    ParameterList paramList = new ParameterList(httpRequest.queryMap().toMap());
                    return this.processReturn(httpRequest, httpResponse, app, paramList);
                }
                return new ModelAndView(new HashMap<>(), "index.ftl");
            } catch (ServletException | IOException e) {
                throw new RuntimeException(e);
            }
        }, new FreeMarkerEngine());
	}

	private ModelAndView processReturn(Request req, Response httpResponse, ClubApplication app, ParameterList params)
				throws ServletException, IOException
    {
        // verify response to ensure the communication has not been tampered with
        HashMap<Object, Object> model = new HashMap<>();
        Identifier identifier = this.verifyResponse(req, model, params);
        if (identifier != null) {
            app.getUserManager().getUser(identifier.getIdentifier()).ifPresent(u->{
                if(model.containsKey("attributes")) {
                    Map attributes = (Map)model.get("attributes");
                    if (attributes.containsKey("first") || attributes.containsKey("last")) {
                        Object first = attributes.get("first");
                        Object last = attributes.get("last");
                        u.setName(combineName(first, last));
                    }
                }
                Login.resetCookies(req, httpResponse, identifier.getIdentifier(), u);
            });

            //model.put("identifier", identifier.getIdentifier());
//            return new ModelAndView(model, "return.ftl");
		}
        return new ModelAndView(new HashMap<>(), "index.ftl");

    }

    private String combineName(Object first, Object last) {
        return ((first != null ? first : "") +" "+ (last != null ? last : "")).trim();
    }

    @SuppressWarnings("unchecked")
	public ModelAndView authRequest(String userSuppliedString,
                                    Request httpReq,
                                    Response httpResp)
				throws IOException, ServletException {

		try {						
			// configure the return_to URL where your application will receive
			// the authentication responses from the OpenID provider
			String returnToUrl = httpReq.raw().getRequestURL().toString() + "?is_return=true";

			// perform discovery on the user-supplied identifier			
			List<DiscoveryInformation> discoveries = manager.discover(userSuppliedString);

			// attempt to associate with the OpenID provider
			// and retrieve one service endpoint for authentication
			DiscoveryInformation discovered = manager.associate(discoveries);

			// store the discovery information in the user's session
			httpReq.session().attribute("openid-disc", discovered);

			// obtain a AuthRequest message to be sent to the OpenID provider
			AuthRequest authReq = manager.authenticate(discovered, returnToUrl);
			
			// Attribute Exchange example: fetching the 'email' attribute
            FetchRequest fetch = FetchRequest.createFetchRequest();
            fetch.addAttribute("email", // attribute alias
                "http://schema.openid.net/contact/email", // type URI
                true); // required
            fetch.addAttribute("first", // attribute alias
                    "http://schema.openid.net/namePerson/first", // type URI
                    true); // required
            fetch.addAttribute("last", // attribute alias
                    "http://schema.openid.net/namePerson/last", // type URI
                    true); // required

            fetch.addAttribute("prefix", // attribute alias
                    "http://schema.openid.net/namePerson/prefix", // type URI
                    true); // required
            fetch.addAttribute("suffix", // attribute alias
                    "http://schema.openid.net/namePerson/suffix", // type URI
                    true); // required
            fetch.addAttribute("gender", // attribute alias
                    "http://schema.openid.net/gender", // type URI
                    true); // required
            fetch.addAttribute("language", // attribute alias
                    "http://schema.openid.net/pref/language", // type URI
                    true); // required
            fetch.addAttribute("phones", // attribute alias
                    "http://schema.openid.net/contact/phone", // type URI
                    true); // required
            fetch.addAttribute("phone", // attribute alias
                    "http://schema.openid.net/contact/phone/default", // type URI
                    true); // required
            fetch.addAttribute("address1", // attribute alias
                    "http://schema.openid.net/contact/postaladdress/home", // type URI
                    true); // required
            fetch.addAttribute("address2", // attribute alias
                    "http://schema.openid.net/contact/postaladdressadditional/home", // type URI
                    true); // required
            fetch.addAttribute("city", // attribute alias
                    "http://schema.openid.net/contact/city/home", // type URI
                    true); // required
            fetch.addAttribute("state", // attribute alias
                    "http://schema.openid.net/contact/state/home", // type URI
                    true); // required
            fetch.addAttribute("country", // attribute alias
                    "http://schema.openid.net/contact/country/home", // type URI
                    true); // required
            fetch.addAttribute("postalcode", // attribute alias
                    "http://schema.openid.net/contact/postalcode/home", // type URI
                    true); // required

            // attach the extension to the authentication request
            authReq.addExtension(fetch);

			if (!discovered.isVersion2()) {
				httpResp.redirect(authReq.getDestinationUrl(true));
				return null;
			} else {
                HashMap<Object, Object> model = new HashMap<>();
                model.put("message", authReq);
                return new ModelAndView(model, "provider-redirection.ftl");
			}
		} catch (OpenIDException e) {
			throw new ServletException(e);
		}
	}

	// processing the authentication response
	public Identifier verifyResponse(Request httpReq, Map<Object, Object> model, ParameterList params)
			throws ServletException {
		try {
			
			// extract the parameters from the authentication response
            // (which comes in as a HTTP request from the OpenID provider)
            ParameterList parameterList = new ParameterList(httpReq.params());
            parameterList.addParams(params);
			
			// retrieve the previously stored discovery information
			DiscoveryInformation discovered = httpReq.session().attribute("openid-disc");

			// extract the receiving URL from the HTTP request
			StringBuffer receivingURL = httpReq.raw().getRequestURL();
			String queryString = httpReq.queryString();
			if (queryString != null && queryString.length() > 0)
				receivingURL.append("?").append(queryString);

			// verify the response; ConsumerManager needs to be the same
			// (static) instance used to place the authentication request
			VerificationResult verification = manager.verify(receivingURL.toString(), parameterList, discovered);

			// examine the verification result and extract the verified
			// identifier
			Identifier verified = verification.getVerifiedId();
			if (verified != null) {
				AuthSuccess authSuccess = (AuthSuccess) verification.getAuthResponse();

				receiveAttributeExchange(model, authSuccess);
				return verified; // success
			}
		} catch (OpenIDException e) {
			// present error to the user
			throw new ServletException(e);
		}
		return null;
	}


	/**
	 * @param model
	 * @param authSuccess
	 * @throws MessageException
	 */
	private void receiveAttributeExchange(Map<Object, Object> model, AuthSuccess authSuccess) throws MessageException {
		if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
			FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
			List<String> aliases = fetchResp.getAttributeAliases();
			Map<String, String> attributes = new LinkedHashMap<>();
            for (String alias : aliases) {
                List<String> values = fetchResp.getAttributeValues(alias);
                if (values.size() > 0) {
                    String[] arr = new String[values.size()];
                    values.toArray(arr);
                    attributes.put(alias, arr[0]);
                }
            }
			model.put("attributes", attributes);
		}
	}
}