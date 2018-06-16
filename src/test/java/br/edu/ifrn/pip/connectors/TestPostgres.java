package br.edu.ifrn.pip.connectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.edu.ifrn.pip.AtributosConstantes;
import br.edu.ifrn.pip.factory.Factory;
import br.edu.ifrn.pip.util.DatabaseConnection;

public class TestPostgres {

	/**
	 * Propriedade do sistema que define o diretório onde são armazenados os
	 * arquivos de configurações do WSO2 Identity Server.
	 */
	private static final String PROPRIEDADE_DIRETORIO_CONFIG_WSO2 = "carbon.config.dir.path";

	/**
	 * Nome do arquivo de configurações utilizado. Este arquivo deve estar
	 * localizado dentro do projeto.
	 */
	private static final String ARQUIVO_CONFIGURACOES_PIP = "wso2-pip-suap.properties";

	/**
	 * Definindo o diretório de configurações do WSO2 como sendo o diretório
	 * temporário.
	 */
	@BeforeClass
	public static void setUp() {
		FileUtils.deleteQuietly(Paths.get(PROPRIEDADE_DIRETORIO_CONFIG_WSO2, ARQUIVO_CONFIGURACOES_PIP).toFile());
		System.setProperty(PROPRIEDADE_DIRETORIO_CONFIG_WSO2, System.getProperty("java.io.tmpdir"));
	}

	/**
	 * Removendo a configuração do diretório do WSO2.
	 */
	@AfterClass
	public static void tearDown() {
		FileUtils.deleteQuietly(Paths.get(PROPRIEDADE_DIRETORIO_CONFIG_WSO2, ARQUIVO_CONFIGURACOES_PIP).toFile());
		System.getProperties().remove(PROPRIEDADE_DIRETORIO_CONFIG_WSO2);
	}

	@Test
	public void testAtributoDonoTicket() {
		Connector connector = Factory.getInstance().criarConnector(AtributosConstantes.ATRIB_CENTRALSERV_DONOTICKET);
		if (DatabaseConnection.getInstance().getConnection() != null) {
			String valorAtributo = connector.recuperarValorDeAtributo("1");
			assertEquals("O resultado não foi esperado", "01199881430", valorAtributo);
		} else {
			assertTrue(true);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAtributoVazio() {
		Connector connector = Factory.getInstance().criarConnector(AtributosConstantes.ATRIB_CENTRALSERV_DONOTICKET);
		connector.recuperarValorDeAtributo("");
		fail("Deveria ter lançado exceção IllegalArgumentException");
	}
}
