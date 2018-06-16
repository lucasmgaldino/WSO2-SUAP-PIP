package br.edu.ifrn.pip.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.edu.ifrn.pip.SuapAttributeFinder;

public class DatabaseConnection {

	private static Log log = LogFactory.getLog(SuapAttributeFinder.class);
	private static DatabaseConnection instance;
	private Connection connection;

	private static final List<String> SGBDS_DISPONIVEIS = Arrays.asList("postgresql", "mysql");

	private static final String CONFIG_DATABASE_VENDOR = "db.vendor";
	private static final String CONFIG_DRIVER = "db.driver";
	private static final String CONFIG_USER = "db.usuario";
	private static final String CONFIG_PASSWORD = "db.senha";
	private static final String CONFIG_HOST = "db.host";
	private static final String CONFIG_PORT = "db.port";
	private static final String CONFIG_DATABASE = "db.database";

	//DAO
	private DatabaseConnection() {
		ConfigUtil config = ConfigUtil.getInstance();
		try {
			Class.forName(config.recuperarValorDeConfiguracao(CONFIG_DRIVER));
			this.connection = DriverManager.getConnection(montarUrlConexaoBanco(),
					config.recuperarValorDeConfiguracao(CONFIG_USER), config.recuperarValorDeConfiguracao(CONFIG_PASSWORD));
		} catch (ClassNotFoundException exception) {
			DatabaseConnection.log.error(
					"A classe do driver do PostgreSQL não foi localizada. Certifique-se de que está definida corretamente no arquivo de configuração e de que do driver está no classpath.",
					exception);
		} catch (SQLException exception) {
			DatabaseConnection.log.error("Falha ao conectar com o banco de dados.", exception);
		}
	}

	/**
	 * Método responsável por montar a URL de conexão com o banco de dados, a partir
	 * das configurações do PIP.
	 *
	 * @return um {@link String} que representa a URL de conexão com o banco de
	 *         dados.
	 */
	private final String montarUrlConexaoBanco() {
		ConfigUtil config = ConfigUtil.getInstance();
		String vendor = StringUtils.deleteWhitespace(config.recuperarValorDeConfiguracao(CONFIG_DATABASE_VENDOR));
		if (!SGBDS_DISPONIVEIS.contains(vendor.toLowerCase())) {
			throw new IllegalArgumentException("Está faltando a configuração de qual tipo de banco de dados será utilizado ('db.vendor').");
		}
		StringBuilder builder = new StringBuilder("jdbc:");
		builder.append(vendor.toLowerCase());
		builder.append("://");
		builder.append(config.recuperarValorDeConfiguracaoComValorPadrao(CONFIG_HOST, "localhost"));
		builder.append(":");
		if (vendor.toLowerCase().equalsIgnoreCase("postgresql")) {
			builder.append(config.recuperarValorDeConfiguracaoComValorPadrao(CONFIG_PORT, "5432"));
		}
		else if (vendor.toLowerCase().equalsIgnoreCase("mysql")) {
			builder.append(config.recuperarValorDeConfiguracaoComValorPadrao(CONFIG_PORT, "3306"));
		}
		builder.append("/");
		builder.append(config.recuperarValorDeConfiguracao(CONFIG_DATABASE));
		return builder.toString();
	}

	public Connection getConnection() {
		return this.connection;
	}

	public static DatabaseConnection getInstance() {
		if (DatabaseConnection.instance == null) {
			DatabaseConnection.instance = new DatabaseConnection();
		} else
			try {
				if (DatabaseConnection.instance.getConnection().isClosed()) {
					DatabaseConnection.instance = new DatabaseConnection();
				}
			} catch (SQLException exception) {
				DatabaseConnection.log.error("Ocorreu um erro ao verificar se a conexão com o banco está fechada.",
						exception);
			}
		return DatabaseConnection.instance;
	}

	public String buscarDonoTicket(String stringBusca) {			//TODO: melhorar nome do método
		String resultadoBusca = "";
		try {
			if (this.getConnection() != null) {
				Integer idChamado = Integer.parseInt(stringBusca);
				PreparedStatement pstmt = this.getConnection().prepareStatement("SELECT A.username " +										//TODO: usar constante
						"FROM centralservicos_chamado C " +
						"INNER JOIN auth_user A ON A.id = C.aberto_por_id " +
						"WHERE C.id = ?");
				pstmt.setInt(1, idChamado);
				ResultSet rs = pstmt.executeQuery();
				while (rs.next()) {
					resultadoBusca = rs.getString(1);
				}
				rs.close();
				pstmt.close();
			}
		} catch (SQLException e) {
			DatabaseConnection.log.error("Ocorreu um erro ao tentar recuperar o dono do ticket de id " + stringBusca,
					e);
		} catch (NumberFormatException exception) {
			DatabaseConnection.log.error("Não foi possível converter '" + stringBusca + "' para um número inteiro.");
		}
		return resultadoBusca;
	}
}