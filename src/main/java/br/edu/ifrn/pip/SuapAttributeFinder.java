package br.edu.ifrn.pip;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.entitlement.pip.AbstractPIPAttributeFinder;

import br.edu.ifrn.pip.connectors.Connector;
import br.edu.ifrn.pip.factory.Factory;
import br.edu.ifrn.pip.AtributosConstantes;

public class SuapAttributeFinder extends AbstractPIPAttributeFinder {

	private static Log log = LogFactory.getLog(SuapAttributeFinder.class);

	private Set<String> supportedAttributes = new HashSet<String>();

	@Override
	public void init(Properties properties) throws Exception {
		log.info("<<<<<<<<<<<<<<<<< Iniciando PIP " + getModuleName() + "... >>>>>>>>>>>>>>>>>");
		
		log.info(">>>> Registrando atributos...");
		supportedAttributes.add(AtributosConstantes.ATRIB_USUARIO_DEPARTAMENTO);
		supportedAttributes.add(AtributosConstantes.ATRIB_CENTRALSERV_DONOTICKET);
	}

	@Override
	public String getModuleName() {
		return "WSO2-SUAP-PIP";
	}

	@Override
	public Set<String> getAttributeValues(String subjectId, String resourceId, String actionId, String environmentId,
			String attributeId, String issuer) throws Exception {
		log.info(">>>>>>>>>>>>> subjectId: " + subjectId);
		log.info(">>>>>>>>>>>>> resourceId: " + resourceId);
		log.info(">>>>>>>>>>>>> actionId: " + actionId);
		log.info(">>>>>>>>>>>>> environmentId: " + environmentId);
		log.info(">>>>>>>>>>>>> attributeId: " + attributeId);
		log.info(">>>>>>>>>>>>> issuer: " + issuer);

		Set<String> values = new HashSet<String>();
		Connector connector = Factory.getInstance().criarConnector(attributeId);
		values.add(connector.recuperarValorDeAtributo(subjectId));

		return values;
	}

	@Override
	public Set<String> getSupportedAttributes() {
		return supportedAttributes;
	}
}