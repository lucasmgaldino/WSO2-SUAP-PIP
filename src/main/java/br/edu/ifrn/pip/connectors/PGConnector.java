package br.edu.ifrn.pip.connectors;

import org.apache.commons.lang.StringUtils;

import br.edu.ifrn.pip.AtributosConstantes;
import br.edu.ifrn.pip.util.DatabaseConnection;

public class PGConnector extends AbstractConnector {

	@Override
	public String recuperarValorDeAtributo(String buscaPor) {
		if (StringUtils.isBlank(buscaPor)) {
			throw new IllegalArgumentException("O parâmetro da busca não foi informado.");
		}
		String atributoId = super.getAtributoId();
		String resultado = "";

		switch (atributoId) {
		case AtributosConstantes.ATRIB_CENTRALSERV_DONOTICKET:
			resultado = DatabaseConnection.getInstance().buscarDonoTicket(buscaPor);
			break;

		default:
			break;
		}

		return resultado;
	}

}
