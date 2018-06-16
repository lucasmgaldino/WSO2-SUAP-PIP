package br.edu.ifrn.pip;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.entitlement.pip.AbstractPIPAttributeFinder;

import br.edu.ifrn.pip.connectors.Connector;
import br.edu.ifrn.pip.factory.Factory;
import br.edu.ifrn.pip.util.ConfigUtil;

public class SuapAttributeFinder extends AbstractPIPAttributeFinder {

	private static Log log = LogFactory.getLog(SuapAttributeFinder.class);
	private static final String PREFIXO_ATRIBUTOS_PIP = "pip.attribute.";
	private Set<String> supportedAttributes = new HashSet<String>();

	@Override
	public void init(Properties properties) throws Exception {
		SuapAttributeFinder.log.info("<<<<<<<<<<<<<<<<< Iniciando PIP " + getModuleName() + "... >>>>>>>>>>>>>>>>>");
		registrarAtributosSuportados();
	}

	/**
	 * Método responsável por registrar os atributos suportados pelo PIP.
	 */
	private void registrarAtributosSuportados() {
		SuapAttributeFinder.log.info(">>>> Registrando atributos...");
		String dominoBase = ConfigUtil.getInstance().recuperarValorDeConfiguracao("dominio.base");
		if (!dominoBase.endsWith("/")) {
			dominoBase = dominoBase.concat("/");
		}
		Set<String> configuracoes = ConfigUtil.getInstance().recuperarNomesDeTodasConfiguracoes();
		for (String config : configuracoes) {
			if (config.startsWith(PREFIXO_ATRIBUTOS_PIP)) {
				String atributo = dominoBase.concat(config.replace(PREFIXO_ATRIBUTOS_PIP, ""));
				if (this.supportedAttributes.add(atributo)) {
					SuapAttributeFinder.log.info("\tAtributo '" + atributo + "' [registrado com sucesso]");
				}
			}
		}
	}

	@Override
	public String getModuleName() {
		return "WSO2-SUAP-PIP";
	}

	@Override
	public Set<String> getAttributeValues(String subjectId, String resourceId, String actionId, String environmentId,
			String attributeId, String issuer) throws Exception {
		SuapAttributeFinder.log.info(">>>>>>>>>>>>> subjectId: " + subjectId);
		SuapAttributeFinder.log.info(">>>>>>>>>>>>> resourceId: " + resourceId);
		SuapAttributeFinder.log.info(">>>>>>>>>>>>> actionId: " + actionId);
		SuapAttributeFinder.log.info(">>>>>>>>>>>>> environmentId: " + environmentId);
		SuapAttributeFinder.log.info(">>>>>>>>>>>>> attributeId: " + attributeId);
		SuapAttributeFinder.log.info(">>>>>>>>>>>>> issuer: " + issuer);

		Set<String> values = new HashSet<String>();
		Connector connector = Factory.getInstance().criarConnector(attributeId);
		values.add(connector.recuperarValorDeAtributo(subjectId));

		return values;
	}

	@Override
	public Set<String> getSupportedAttributes() {
		return this.supportedAttributes;
	}
}
