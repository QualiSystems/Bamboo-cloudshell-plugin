package com.quali.cloudshell.bamboo.sandbox.plugin.servlet;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.quali.cloudshell.RestResponse;
import com.quali.cloudshell.SandboxApiGateway;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdminServlet extends HttpServlet {
	private final TransactionTemplate transactionTemplate;
	private final PluginSettingsFactory pluginSettingsFactory;
	private static final long serialVersionUID = 1L;

	private final TemplateRenderer renderer;

	public AdminServlet(PluginSettingsFactory pluginSettingsFactory, TemplateRenderer renderer,
                        TransactionTemplate transactionTemplate) {
		this.pluginSettingsFactory = pluginSettingsFactory;
		this.renderer = renderer;
		this.transactionTemplate = transactionTemplate;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> context = new HashMap<String, Object>();
		
		resp.setContentType("text/html;charset=utf-8");

		PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();

		String url= getConfigKey(pluginSettings, AdminServletConst.URL);
		String user= getConfigKey(pluginSettings, AdminServletConst.CS_USER);
		String password= getConfigKey(pluginSettings, AdminServletConst.CS_PASSWORD);
		String domain= getConfigKey(pluginSettings, AdminServletConst.CS_DOMAIN);

		ValidateKey(context, url, AdminServletConst.URL, AdminServletConst.URL_ERROR,
				"Please set a CloudShell server URL");

		ValidateKey(context, user, AdminServletConst.CS_USER, AdminServletConst.CS_USER_ERROR,
				"Please set a CloudShell user name");

		ValidateKey(context, password, AdminServletConst.CS_PASSWORD, AdminServletConst.CS_PASSWORD_ERROR,
				"Please set a CloudShell password");

		ValidateKey(context, domain, AdminServletConst.CS_DOMAIN, AdminServletConst.CS_DOMAIN_ERROR,
				"Please set a CloudShell domain");

		context.put(AdminServletConst.GENERAL_ERROR, "");
		context.put(AdminServletConst.GENERAL_MSG, "");

		renderer.render(AdminServletConst.CS_ADMIN_LAYOUT, context, resp.getWriter());
	}

	private void ValidateKey(Map<String, Object> context, String key, String userKey, String userKeyError, String errorMessage) {
		if (key != null) {
			context.put(userKey, key);
			context.put(userKeyError, "");
		} else {
			context.put(userKey, "");
			context.put(userKeyError, errorMessage);
		}
	}

	private String getConfigKey(PluginSettings pluginSettings, String config){
		return (String) pluginSettings.get(Config.class.getName() + '.' +
				config);
	}

	@Override
	protected void doPost(final HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Map<String, Object> context = new HashMap<String, Object>();
		resp.setContentType("text/html;charset=utf-8");

		String url = req.getParameter(AdminServletConst.URL).trim();
		String user = req.getParameter(AdminServletConst.CS_USER).trim();
		String password = req.getParameter(AdminServletConst.CS_PASSWORD).trim();
		String domain = req.getParameter(AdminServletConst.CS_DOMAIN).trim();

		SandboxApiGateway sandboxApiGateway =
				new SandboxApiGateway(url, user, password, domain, true, null);

		RestResponse restResponse = null;
		try {
			restResponse = sandboxApiGateway.TryLogin();
		} catch (Exception e) {
			e.printStackTrace(); //TODO: handle
		}

		if (restResponse.getHttpCode() == 200) {
			transactionTemplate.execute(new TransactionCallback() {
				public Object doInTransaction() {
					PluginSettings pluginSettings = pluginSettingsFactory.createGlobalSettings();
					pluginSettings.put(Config.class.getName() + '.' + AdminServletConst.URL, req.getParameter(AdminServletConst.URL).trim());
					pluginSettings.put(Config.class.getName() + '.' + AdminServletConst.CS_USER, req.getParameter(AdminServletConst.CS_USER).trim());
					pluginSettings.put(Config.class.getName() + '.' + AdminServletConst.CS_PASSWORD, req.getParameter(AdminServletConst.CS_PASSWORD).trim());
					pluginSettings.put(Config.class.getName() + '.' + AdminServletConst.CS_DOMAIN, req.getParameter(AdminServletConst.CS_DOMAIN).trim());
					return null;
				}
			});
			context.put(AdminServletConst.GENERAL_ERROR, "");
			context.put(AdminServletConst.GENERAL_MSG, "Connected successfully to CloudShell");

		} else {
            context.put(AdminServletConst.GENERAL_ERROR, "Failed to test CloudShell login. \n" + restResponse.getContent());
			context.put(AdminServletConst.GENERAL_MSG, "");
        }

		context.put(AdminServletConst.CS_USER_ERROR, "");
		context.put(AdminServletConst.CS_PASSWORD_ERROR, "");
		context.put(AdminServletConst.CS_DOMAIN_ERROR, "");
		context.put(AdminServletConst.URL_ERROR, "");

		context.put(AdminServletConst.URL, url);
		context.put(AdminServletConst.CS_USER, user);
		context.put(AdminServletConst.CS_PASSWORD, password);
		context.put(AdminServletConst.CS_DOMAIN, domain);

		renderer.render(AdminServletConst.CS_ADMIN_LAYOUT, context, resp.getWriter());
	}

	
	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class Config {
		@XmlElement
		private String userkey;


		public String getUserkey() {
			return userkey;
		}
		public void setUserkey(String userkey) {
			this.userkey = userkey;
		}
	}
}