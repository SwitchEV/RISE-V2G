package org.v2gclarity.risev2g.shared.exiCodec;

import javax.xml.namespace.QName;

import com.siemens.ct.exi.context.GrammarContext;
import com.siemens.ct.exi.context.GrammarUriContext;
import com.siemens.ct.exi.context.QNameContext;
import com.siemens.ct.exi.exceptions.UnsupportedOption;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.grammars.event.EndDocument;
import com.siemens.ct.exi.grammars.event.Event;
import com.siemens.ct.exi.grammars.event.StartDocument;
import com.siemens.ct.exi.grammars.event.StartElement;
import com.siemens.ct.exi.grammars.grammar.DocEnd;
import com.siemens.ct.exi.grammars.grammar.Document;
import com.siemens.ct.exi.grammars.grammar.Fragment;
import com.siemens.ct.exi.grammars.grammar.Grammar;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedDocContent;
import com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTagGrammar;

@SuppressWarnings("unused")
public class EXIficient_V2G_CI_AppProtocol implements Grammars {

		/* BEGIN GrammarContext ----- */
	final String ns0 = "";
	final QNameContext qnc0 = new QNameContext(0, 0, new QName(ns0, "AppProtocol"));
	final QNameContext qnc1 = new QNameContext(0, 1, new QName(ns0, "Priority"));
	final QNameContext qnc2 = new QNameContext(0, 2, new QName(ns0, "ProtocolNamespace"));
	final QNameContext qnc3 = new QNameContext(0, 3, new QName(ns0, "ResponseCode"));
	final QNameContext qnc4 = new QNameContext(0, 4, new QName(ns0, "SchemaID"));
	final QNameContext qnc5 = new QNameContext(0, 5, new QName(ns0, "VersionNumberMajor"));
	final QNameContext qnc6 = new QNameContext(0, 6, new QName(ns0, "VersionNumberMinor"));
	final QNameContext[] grammarQNames0 = {qnc0, qnc1, qnc2, qnc3, qnc4, qnc5, qnc6};
	final String[] grammarPrefixes0 = {""};
	final GrammarUriContext guc0 = new GrammarUriContext(0, ns0, grammarQNames0, grammarPrefixes0);
	
	final String ns1 = "http://www.w3.org/XML/1998/namespace";
	final QNameContext qnc7 = new QNameContext(1, 0, new QName(ns1, "base"));
	final QNameContext qnc8 = new QNameContext(1, 1, new QName(ns1, "id"));
	final QNameContext qnc9 = new QNameContext(1, 2, new QName(ns1, "lang"));
	final QNameContext qnc10 = new QNameContext(1, 3, new QName(ns1, "space"));
	final QNameContext[] grammarQNames1 = {qnc7, qnc8, qnc9, qnc10};
	final String[] grammarPrefixes1 = {"xml"};
	final GrammarUriContext guc1 = new GrammarUriContext(1, ns1, grammarQNames1, grammarPrefixes1);
	
	final String ns2 = "http://www.w3.org/2001/XMLSchema-instance";
	final QNameContext qnc11 = new QNameContext(2, 0, new QName(ns2, "nil"));
	final QNameContext qnc12 = new QNameContext(2, 1, new QName(ns2, "type"));
	final QNameContext[] grammarQNames2 = {qnc11, qnc12};
	final String[] grammarPrefixes2 = {"xsi"};
	final GrammarUriContext guc2 = new GrammarUriContext(2, ns2, grammarQNames2, grammarPrefixes2);
	
	final String ns3 = "http://www.w3.org/2001/XMLSchema";
	final QNameContext qnc13 = new QNameContext(3, 0, new QName(ns3, "ENTITIES"));
	final QNameContext qnc14 = new QNameContext(3, 1, new QName(ns3, "ENTITY"));
	final QNameContext qnc15 = new QNameContext(3, 2, new QName(ns3, "ID"));
	final QNameContext qnc16 = new QNameContext(3, 3, new QName(ns3, "IDREF"));
	final QNameContext qnc17 = new QNameContext(3, 4, new QName(ns3, "IDREFS"));
	final QNameContext qnc18 = new QNameContext(3, 5, new QName(ns3, "NCName"));
	final QNameContext qnc19 = new QNameContext(3, 6, new QName(ns3, "NMTOKEN"));
	final QNameContext qnc20 = new QNameContext(3, 7, new QName(ns3, "NMTOKENS"));
	final QNameContext qnc21 = new QNameContext(3, 8, new QName(ns3, "NOTATION"));
	final QNameContext qnc22 = new QNameContext(3, 9, new QName(ns3, "Name"));
	final QNameContext qnc23 = new QNameContext(3, 10, new QName(ns3, "QName"));
	final QNameContext qnc24 = new QNameContext(3, 11, new QName(ns3, "anySimpleType"));
	final QNameContext qnc25 = new QNameContext(3, 12, new QName(ns3, "anyType"));
	final QNameContext qnc26 = new QNameContext(3, 13, new QName(ns3, "anyURI"));
	final QNameContext qnc27 = new QNameContext(3, 14, new QName(ns3, "base64Binary"));
	final QNameContext qnc28 = new QNameContext(3, 15, new QName(ns3, "boolean"));
	final QNameContext qnc29 = new QNameContext(3, 16, new QName(ns3, "byte"));
	final QNameContext qnc30 = new QNameContext(3, 17, new QName(ns3, "date"));
	final QNameContext qnc31 = new QNameContext(3, 18, new QName(ns3, "dateTime"));
	final QNameContext qnc32 = new QNameContext(3, 19, new QName(ns3, "decimal"));
	final QNameContext qnc33 = new QNameContext(3, 20, new QName(ns3, "double"));
	final QNameContext qnc34 = new QNameContext(3, 21, new QName(ns3, "duration"));
	final QNameContext qnc35 = new QNameContext(3, 22, new QName(ns3, "float"));
	final QNameContext qnc36 = new QNameContext(3, 23, new QName(ns3, "gDay"));
	final QNameContext qnc37 = new QNameContext(3, 24, new QName(ns3, "gMonth"));
	final QNameContext qnc38 = new QNameContext(3, 25, new QName(ns3, "gMonthDay"));
	final QNameContext qnc39 = new QNameContext(3, 26, new QName(ns3, "gYear"));
	final QNameContext qnc40 = new QNameContext(3, 27, new QName(ns3, "gYearMonth"));
	final QNameContext qnc41 = new QNameContext(3, 28, new QName(ns3, "hexBinary"));
	final QNameContext qnc42 = new QNameContext(3, 29, new QName(ns3, "int"));
	final QNameContext qnc43 = new QNameContext(3, 30, new QName(ns3, "integer"));
	final QNameContext qnc44 = new QNameContext(3, 31, new QName(ns3, "language"));
	final QNameContext qnc45 = new QNameContext(3, 32, new QName(ns3, "long"));
	final QNameContext qnc46 = new QNameContext(3, 33, new QName(ns3, "negativeInteger"));
	final QNameContext qnc47 = new QNameContext(3, 34, new QName(ns3, "nonNegativeInteger"));
	final QNameContext qnc48 = new QNameContext(3, 35, new QName(ns3, "nonPositiveInteger"));
	final QNameContext qnc49 = new QNameContext(3, 36, new QName(ns3, "normalizedString"));
	final QNameContext qnc50 = new QNameContext(3, 37, new QName(ns3, "positiveInteger"));
	final QNameContext qnc51 = new QNameContext(3, 38, new QName(ns3, "short"));
	final QNameContext qnc52 = new QNameContext(3, 39, new QName(ns3, "string"));
	final QNameContext qnc53 = new QNameContext(3, 40, new QName(ns3, "time"));
	final QNameContext qnc54 = new QNameContext(3, 41, new QName(ns3, "token"));
	final QNameContext qnc55 = new QNameContext(3, 42, new QName(ns3, "unsignedByte"));
	final QNameContext qnc56 = new QNameContext(3, 43, new QName(ns3, "unsignedInt"));
	final QNameContext qnc57 = new QNameContext(3, 44, new QName(ns3, "unsignedLong"));
	final QNameContext qnc58 = new QNameContext(3, 45, new QName(ns3, "unsignedShort"));
	final QNameContext[] grammarQNames3 = {qnc13, qnc14, qnc15, qnc16, qnc17, qnc18, qnc19, qnc20, qnc21, qnc22, qnc23, qnc24, qnc25, qnc26, qnc27, qnc28, qnc29, qnc30, qnc31, qnc32, qnc33, qnc34, qnc35, qnc36, qnc37, qnc38, qnc39, qnc40, qnc41, qnc42, qnc43, qnc44, qnc45, qnc46, qnc47, qnc48, qnc49, qnc50, qnc51, qnc52, qnc53, qnc54, qnc55, qnc56, qnc57, qnc58};
	final String[] grammarPrefixes3 = {};
	final GrammarUriContext guc3 = new GrammarUriContext(3, ns3, grammarQNames3, grammarPrefixes3);
	
	final String ns4 = "urn:iso:15118:2:2010:AppProtocol";
	final QNameContext qnc59 = new QNameContext(4, 0, new QName(ns4, "AppProtocolType"));
	final QNameContext qnc60 = new QNameContext(4, 1, new QName(ns4, "idType"));
	final QNameContext qnc61 = new QNameContext(4, 2, new QName(ns4, "priorityType"));
	final QNameContext qnc62 = new QNameContext(4, 3, new QName(ns4, "protocolNameType"));
	final QNameContext qnc63 = new QNameContext(4, 4, new QName(ns4, "protocolNamespaceType"));
	final QNameContext qnc64 = new QNameContext(4, 5, new QName(ns4, "responseCodeType"));
	final QNameContext qnc65 = new QNameContext(4, 6, new QName(ns4, "supportedAppProtocolReq"));
	final QNameContext qnc66 = new QNameContext(4, 7, new QName(ns4, "supportedAppProtocolRes"));
	final QNameContext[] grammarQNames4 = {qnc59, qnc60, qnc61, qnc62, qnc63, qnc64, qnc65, qnc66};
	final String[] grammarPrefixes4 = {};
	final GrammarUriContext guc4 = new GrammarUriContext(4, ns4, grammarQNames4, grammarPrefixes4);
	
	final GrammarUriContext[] grammarUriContexts = {guc0, guc1, guc2, guc3, guc4};
	final GrammarContext gc = new GrammarContext(grammarUriContexts, 67);
	/* END GrammarContext ----- */

	
		/* BEGIN Grammars ----- */
	com.siemens.ct.exi.grammars.grammar.Document g0 = new com.siemens.ct.exi.grammars.grammar.Document();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedDocContent g1 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedDocContent();
	com.siemens.ct.exi.grammars.grammar.DocEnd g2 = new com.siemens.ct.exi.grammars.grammar.DocEnd();
	com.siemens.ct.exi.grammars.grammar.Fragment g3 = new com.siemens.ct.exi.grammars.grammar.Fragment();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFragmentContent g4 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFragmentContent();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g5 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g6 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g7 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g8 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g9 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g10 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g11 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g12 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g13 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g14 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g15 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g16 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g17 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g18 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g19 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g20 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g21 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g22 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g23 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g24 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g25 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g26 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g27 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g28 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g29 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag g30 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedFirstStartTag();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g31 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g32 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g33 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g34 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g35 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g36 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g37 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g38 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g39 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g40 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g41 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g42 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g43 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g44 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g45 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g46 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g47 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g48 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g49 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g50 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g51 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g52 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g53 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g54 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g55 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g56 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g57 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g58 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g59 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g60 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g61 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g62 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g63 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g64 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g65 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g66 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g67 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g68 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g69 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g70 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g71 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g72 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g73 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g74 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g75 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g76 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g77 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g78 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g79 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g80 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	com.siemens.ct.exi.grammars.grammar.SchemaInformedElement g81 = new com.siemens.ct.exi.grammars.grammar.SchemaInformedElement();
	/* END Grammars ----- */

	
		com.siemens.ct.exi.grammars.event.StartElement globalSE65 = new com.siemens.ct.exi.grammars.event.StartElement(qnc65, g5);
	com.siemens.ct.exi.grammars.event.StartElement globalSE66 = new com.siemens.ct.exi.grammars.event.StartElement(qnc66, g11);


	protected String schemaId; 
	
	
	void initGlobalElements() {
			/* BEGIN GlobalElements ----- */
	qnc65.setGlobalStartElement(globalSE65);
	qnc66.setGlobalStartElement(globalSE66);
	/* END GlobalElements ----- */
	
	}
	
	void initGlobalAttributes() {
			/* BEGIN GlobalAttributes ----- */
	/* END GlobalAttributes ----- */

	}
	
	void initTypeGrammars() {
			/* BEGIN TypeGrammar ----- */
	qnc13.setTypeGrammar(g13);
	qnc14.setTypeGrammar(g7);
	qnc15.setTypeGrammar(g7);
	qnc16.setTypeGrammar(g7);
	qnc17.setTypeGrammar(g13);
	qnc18.setTypeGrammar(g7);
	qnc19.setTypeGrammar(g7);
	qnc20.setTypeGrammar(g13);
	qnc21.setTypeGrammar(g7);
	qnc22.setTypeGrammar(g7);
	qnc23.setTypeGrammar(g7);
	qnc24.setTypeGrammar(g7);
	qnc25.setTypeGrammar(g14);
	qnc26.setTypeGrammar(g7);
	qnc27.setTypeGrammar(g15);
	qnc28.setTypeGrammar(g16);
	qnc29.setTypeGrammar(g17);
	qnc30.setTypeGrammar(g18);
	qnc31.setTypeGrammar(g19);
	qnc32.setTypeGrammar(g20);
	qnc33.setTypeGrammar(g21);
	qnc34.setTypeGrammar(g7);
	qnc35.setTypeGrammar(g21);
	qnc36.setTypeGrammar(g22);
	qnc37.setTypeGrammar(g23);
	qnc38.setTypeGrammar(g24);
	qnc39.setTypeGrammar(g25);
	qnc40.setTypeGrammar(g26);
	qnc41.setTypeGrammar(g27);
	qnc42.setTypeGrammar(g28);
	qnc43.setTypeGrammar(g28);
	qnc44.setTypeGrammar(g7);
	qnc45.setTypeGrammar(g28);
	qnc46.setTypeGrammar(g28);
	qnc47.setTypeGrammar(g29);
	qnc48.setTypeGrammar(g28);
	qnc49.setTypeGrammar(g7);
	qnc50.setTypeGrammar(g29);
	qnc51.setTypeGrammar(g28);
	qnc52.setTypeGrammar(g7);
	qnc53.setTypeGrammar(g30);
	qnc54.setTypeGrammar(g7);
	qnc55.setTypeGrammar(g9);
	qnc56.setTypeGrammar(g29);
	qnc57.setTypeGrammar(g29);
	qnc58.setTypeGrammar(g29);
	qnc59.setTypeGrammar(g6);
	qnc60.setTypeGrammar(g9);
	qnc61.setTypeGrammar(g10);
	qnc62.setTypeGrammar(g7);
	qnc63.setTypeGrammar(g7);
	qnc64.setTypeGrammar(g12);
	/* END TypeGrammar ----- */
	
	}
	
	
	void initGrammarEvents() {
			/* BEGIN Grammar Events ----- */
	g0.addProduction(new com.siemens.ct.exi.grammars.event.StartDocument(), g1);
	g1.addProduction(globalSE65, g2);
	g1.addProduction(globalSE66, g2);
	g1.addProduction(new com.siemens.ct.exi.grammars.event.StartElementGeneric(), g2);
	g2.addProduction(new com.siemens.ct.exi.grammars.event.EndDocument(), g32);
	g3.addProduction(new com.siemens.ct.exi.grammars.event.StartDocument(), g4);
	g4.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g4);
	g4.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc1, g10), g4);
	g4.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc2, g7), g4);
	g4.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc3, g12), g4);
	g4.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc4, g9), g4);
	g4.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc5, g8), g4);
	g4.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc6, g8), g4);
	g4.addProduction(globalSE65, g4);
	g4.addProduction(globalSE66, g4);
	g4.addProduction(new com.siemens.ct.exi.grammars.event.StartElementGeneric(), g4);
	g4.addProduction(new com.siemens.ct.exi.grammars.event.EndDocument(), g32);
	g5.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g42);
	g6.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc2, g7), g34);
	g7.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.StringDatatype(qnc63)), g31);
	g8.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.UnsignedIntegerDatatype(qnc56)), g31);
	g9.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.NBitUnsignedIntegerDatatype(com.siemens.ct.exi.values.IntegerValue.valueOf(0), com.siemens.ct.exi.values.IntegerValue.valueOf(255), qnc60)), g31);
	g10.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.NBitUnsignedIntegerDatatype(com.siemens.ct.exi.values.IntegerValue.valueOf(1), com.siemens.ct.exi.values.IntegerValue.valueOf(20), qnc61)), g31);
	g11.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc3, g12), g63);
	g12.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.EnumerationDatatype(new com.siemens.ct.exi.values.Value[] {new com.siemens.ct.exi.values.StringValue("OK_SuccessfulNegotiation"), new com.siemens.ct.exi.values.StringValue("OK_SuccessfulNegotiationWithMinorDeviation"), new com.siemens.ct.exi.values.StringValue("Failed_NoNegotiation")}, new com.siemens.ct.exi.datatype.StringDatatype(qnc52), qnc64)), g31);
	g13.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.ListDatatype(new com.siemens.ct.exi.datatype.StringDatatype(qnc14), qnc13)), g31);
	g14.addProduction(new com.siemens.ct.exi.grammars.event.AttributeGeneric(), g14);
	g14.addProduction(new com.siemens.ct.exi.grammars.event.StartElementGeneric(), g66);
	g14.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g14.addProduction(new com.siemens.ct.exi.grammars.event.CharactersGeneric(), g66);
	g15.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.BinaryBase64Datatype(qnc27)), g31);
	g16.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.BooleanDatatype(qnc28)), g31);
	g17.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.NBitUnsignedIntegerDatatype(com.siemens.ct.exi.values.IntegerValue.valueOf(-128), com.siemens.ct.exi.values.IntegerValue.valueOf(127), qnc29)), g31);
	g18.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.date, qnc30)), g31);
	g19.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.dateTime, qnc31)), g31);
	g20.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DecimalDatatype(qnc32)), g31);
	g21.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.FloatDatatype(qnc33)), g31);
	g22.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.gDay, qnc36)), g31);
	g23.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.gMonth, qnc37)), g31);
	g24.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.gMonthDay, qnc38)), g31);
	g25.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.gYear, qnc39)), g31);
	g26.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.gYearMonth, qnc40)), g31);
	g27.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.BinaryHexDatatype(qnc41)), g31);
	g28.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.IntegerDatatype(qnc42)), g31);
	g29.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.UnsignedIntegerDatatype(qnc47)), g31);
	g30.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.time, qnc53)), g31);
	g31.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g33.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.StringDatatype(qnc63)), g31);
	g34.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc5, g8), g36);
	g35.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.UnsignedIntegerDatatype(qnc56)), g31);
	g36.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc6, g8), g37);
	g37.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc4, g9), g39);
	g38.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.NBitUnsignedIntegerDatatype(com.siemens.ct.exi.values.IntegerValue.valueOf(0), com.siemens.ct.exi.values.IntegerValue.valueOf(255), qnc60)), g31);
	g39.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc1, g10), g31);
	g40.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.NBitUnsignedIntegerDatatype(com.siemens.ct.exi.values.IntegerValue.valueOf(1), com.siemens.ct.exi.values.IntegerValue.valueOf(20), qnc61)), g31);
	g41.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc2, g7), g34);
	g42.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g43);
	g42.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g43.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g44);
	g43.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g44.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g45);
	g44.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g45.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g46);
	g45.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g46.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g47);
	g46.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g47.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g48);
	g47.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g48.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g49);
	g48.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g49.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g50);
	g49.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g50.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g51);
	g50.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g51.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g52);
	g51.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g52.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g53);
	g52.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g53.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g54);
	g53.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g54.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g55);
	g54.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g55.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g56);
	g55.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g56.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g57);
	g56.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g57.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g58);
	g57.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g58.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g59);
	g58.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g59.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g60);
	g59.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g60.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g31);
	g60.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g61.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc0, g6), g42);
	g62.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.EnumerationDatatype(new com.siemens.ct.exi.values.Value[] {new com.siemens.ct.exi.values.StringValue("OK_SuccessfulNegotiation"), new com.siemens.ct.exi.values.StringValue("OK_SuccessfulNegotiationWithMinorDeviation"), new com.siemens.ct.exi.values.StringValue("Failed_NoNegotiation")}, new com.siemens.ct.exi.datatype.StringDatatype(qnc52), qnc64)), g31);
	g63.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc4, g9), g31);
	g63.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g64.addProduction(new com.siemens.ct.exi.grammars.event.StartElement(qnc3, g12), g63);
	g65.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.ListDatatype(new com.siemens.ct.exi.datatype.StringDatatype(qnc14), qnc13)), g31);
	g66.addProduction(new com.siemens.ct.exi.grammars.event.StartElementGeneric(), g66);
	g66.addProduction(new com.siemens.ct.exi.grammars.event.EndElement(), g32);
	g66.addProduction(new com.siemens.ct.exi.grammars.event.CharactersGeneric(), g66);
	g67.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.BinaryBase64Datatype(qnc27)), g31);
	g68.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.BooleanDatatype(qnc28)), g31);
	g69.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.NBitUnsignedIntegerDatatype(com.siemens.ct.exi.values.IntegerValue.valueOf(-128), com.siemens.ct.exi.values.IntegerValue.valueOf(127), qnc29)), g31);
	g70.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.date, qnc30)), g31);
	g71.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.dateTime, qnc31)), g31);
	g72.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DecimalDatatype(qnc32)), g31);
	g73.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.FloatDatatype(qnc33)), g31);
	g74.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.gDay, qnc36)), g31);
	g75.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.gMonth, qnc37)), g31);
	g76.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.gMonthDay, qnc38)), g31);
	g77.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.gYear, qnc39)), g31);
	g78.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.gYearMonth, qnc40)), g31);
	g79.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.BinaryHexDatatype(qnc41)), g31);
	g80.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.IntegerDatatype(qnc42)), g31);
	g81.addProduction(new com.siemens.ct.exi.grammars.event.Characters(new com.siemens.ct.exi.datatype.DatetimeDatatype(com.siemens.ct.exi.types.DateTimeType.time, qnc53)), g31);
	/* END Grammar Events ----- */

	}
	
	void initFirstStartGrammars() {
			/* BEGIN FirstStartGrammar ----- */
	g5.setElementContentGrammar(g61);
	g6.setElementContentGrammar(g41);
	g7.setElementContentGrammar(g33);
	g8.setElementContentGrammar(g35);
	g8.setTypeCastable(true);
	g9.setElementContentGrammar(g38);
	g10.setElementContentGrammar(g40);
	g11.setElementContentGrammar(g64);
	g12.setElementContentGrammar(g62);
	g13.setElementContentGrammar(g65);
	g14.setElementContentGrammar(g66);
	g15.setElementContentGrammar(g67);
	g16.setElementContentGrammar(g68);
	g17.setElementContentGrammar(g69);
	g18.setElementContentGrammar(g70);
	g19.setElementContentGrammar(g71);
	g20.setElementContentGrammar(g72);
	g21.setElementContentGrammar(g73);
	g22.setElementContentGrammar(g74);
	g23.setElementContentGrammar(g75);
	g24.setElementContentGrammar(g76);
	g25.setElementContentGrammar(g77);
	g26.setElementContentGrammar(g78);
	g27.setElementContentGrammar(g79);
	g28.setElementContentGrammar(g80);
	g29.setElementContentGrammar(g35);
	g30.setElementContentGrammar(g81);
	/* END FirstStartGrammar ----- */

	}
	
	public EXIficient_V2G_CI_AppProtocol() {
		initGlobalElements();
		initGlobalAttributes();
		initTypeGrammars();
		initGrammarEvents();
		initFirstStartGrammars();
	}
	

	public boolean isSchemaInformed() {
		return true;
	}

	public String getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(String schemaId) throws UnsupportedOption {
		this.schemaId = schemaId;
	}

	public boolean isBuiltInXMLSchemaTypesOnly() {
		return false;
	}

	public Grammar getDocumentGrammar() {
		return g0;
	}

	public Grammar getFragmentGrammar() {
		return g3;
	}
	

	
	public GrammarContext getGrammarContext() {
		return gc;
	}

}
