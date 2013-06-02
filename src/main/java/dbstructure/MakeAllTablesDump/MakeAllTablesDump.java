package dbstructure.MakeAllTablesDump;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

import javax.xml.parsers.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.*;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

/**
 * MakeAllTablesDump class with main function
 */
public class MakeAllTablesDump {
	private final Logger LOGGER = LoggerFactory.getLogger(MakeAllTablesDump.class);
	private VelocityEngine myVelocity;

	/**
	 * main
	 * @throws Exception
	 */
	public static void main (String[] args) {
		String strConfigFile = null;

		for (int int_index = 0; int_index < args.length; int_index++) {
			if (int_index == 0) {
				strConfigFile = args[int_index];
			}
		}

		if (strConfigFile != null && strConfigFile.length() > 0) {
			new MakeAllTablesDump (strConfigFile);
		} else {
			System.out.println ("expected configuration XML filename as parameter");
		}
	}

	/**
	 * MakeAllTablesDump::MakeAllTablesDump
	 */
	public MakeAllTablesDump (final String strConfigFile) {
		File                    file                              = null;
		NamedNodeMap            namedNodeMap                      = null;
		TDumpParams             DumpParams                        = null;
		HashMap<String, String> mapMakeAllTablesDumpGlobalOptions = new HashMap<String, String>();
		HashMap<String, String> mapMakeAllTablesDumpOptions       = new HashMap<String, String>();

		try {
			myVelocity = new VelocityEngine();

			myVelocity.setProperty (
				 RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS
				,"org.apache.velocity.runtime.log.Log4JLogChute"
			);

			myVelocity.setProperty (
				 "runtime.log.logsystem.log4j.logger"
				,LOGGER.getName()
			);

			myVelocity.init();

			file = new File (strConfigFile);

			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse (file);
			NodeList nodeList = document.getElementsByTagName ("MakeAllTablesDump");

			for (int int_root_index = 0; int_root_index < nodeList.getLength(); int_root_index++) {

				mapMakeAllTablesDumpGlobalOptions.clear();

				NodeList nodeListParameter = ((Element)nodeList.item(int_root_index)).getElementsByTagName ("parameter");
				for (int int_index_parameter = 0; int_index_parameter < nodeListParameter.getLength(); int_index_parameter++) {
					NamedNodeMap namedNodeMapParameter = ((Element)(nodeListParameter.item(int_index_parameter))).getAttributes();

					for (int int_index = 0; int_index < namedNodeMapParameter.getLength(); int_index++) {
						Node node = namedNodeMapParameter.item(int_index);
						mapMakeAllTablesDumpGlobalOptions.put(node.getNodeName(), node.getNodeValue());
					}
				}

				NodeList nodeListTemplate = ((Element)nodeList.item(int_root_index)).getElementsByTagName ("template");

				NodeList nodeLoginList = ((Element)nodeList.item(int_root_index)).getElementsByTagName ("login");
				for (int int_login_index = 0; int_login_index < nodeLoginList.getLength(); int_login_index++) {

					mapMakeAllTablesDumpOptions.clear();
					mapMakeAllTablesDumpOptions.putAll(mapMakeAllTablesDumpGlobalOptions);
	
					// add or replace parameters for the instance
					namedNodeMap = ((Element)(nodeLoginList.item(int_login_index))).getAttributes();

					for (int int_index = 0; int_index < namedNodeMap.getLength(); int_index++) {
						Node node = namedNodeMap.item(int_index);
						mapMakeAllTablesDumpOptions.put(node.getNodeName(), node.getNodeValue());
					}

					MakeAllTablesDumpObject myobj = new MakeAllTablesDumpObject (
						 mapMakeAllTablesDumpOptions
						,myVelocity
						,nodeListTemplate
					);
	
					DumpParams = new TDumpParams();

					NodeList nodeListDb = ((Element)nodeLoginList.item(int_login_index)).getElementsByTagName ("db");
					for (int int_db_index = 0; int_db_index < nodeListDb.getLength(); int_db_index++) {
						Element element = (Element)(nodeListDb.item(int_db_index));
						String strDbName = element.getAttribute("name");

						NodeList nodeObjectList = ((Element)nodeListDb.item(int_db_index)).getElementsByTagName ("object");
						for (int int_object_index = 0; int_object_index < nodeObjectList.getLength(); int_object_index++){
							Element elementObject = (Element)(nodeObjectList.item(int_object_index));
							namedNodeMap = elementObject.getAttributes();

							for (int int_index = 0; int_index < namedNodeMap.getLength(); int_index++) {
								Node node = namedNodeMap.item(int_index);

								if (node.getNodeName().compareTo("type") == 0 ||
								    node.getNodeName().compareTo("name") == 0) {
									continue;
								}

								DumpParams.setDbOptionValue (
								 strDbName,
								 elementObject.getAttribute("type"),
								 elementObject.getAttribute("name"),
								 node.getNodeName(),
								 node.getNodeValue()
								);
							}

							NodeList nodeObjectBcpList = ((Element)nodeObjectList.item(int_object_index)).getElementsByTagName ("bcp");
							for (int int_object_bcp_index = 0; int_object_bcp_index < nodeObjectBcpList.getLength(); int_object_bcp_index++) {
								Element elementBcp = (Element)(nodeObjectBcpList.item(int_object_bcp_index));
								namedNodeMap = elementBcp.getAttributes();

								ArrayList<TParamNameValue> arrayList = new ArrayList<TParamNameValue>();
								for (int int_index = 0; int_index < namedNodeMap.getLength(); int_index++) {
									Node node = namedNodeMap.item(int_index);
									TParamNameValue ParamNameValue = new TParamNameValue (node.getNodeName(), node.getNodeValue());
									arrayList.add (ParamNameValue);
								}

								DumpParams.setDbOptionValue (
								 strDbName,
								 elementObject.getAttribute("type"),
								 elementObject.getAttribute("name"),
								 "bcp",
								 arrayList
								);
							}
						}
					}

					if (DumpParams.getSize() > 0) {
						myobj.DumpDatabase(DumpParams);
					}

					NodeList nodeListReport = ((Element)nodeLoginList.item(int_login_index)).getElementsByTagName ("report");
					for (int int_index_report = 0; int_index_report < nodeListReport.getLength(); int_index_report++) {
						Element element = (Element)(nodeListReport.item(int_index_report));
						String strReportName = element.getAttribute("name");
						String strReportSQL = element.getAttribute("dbstructure");
						String strReportDatabase = element.getAttribute("db");

						LOGGER.info(strReportName);
						LOGGER.info(strReportSQL);
						LOGGER.info(strReportDatabase);
					}
				}
			}
		} catch (ParserConfigurationException | IOException | SAXException | ClassNotFoundException | SQLException e) {
            LOGGER.error("Error while processing", e);
        }
    }
}
