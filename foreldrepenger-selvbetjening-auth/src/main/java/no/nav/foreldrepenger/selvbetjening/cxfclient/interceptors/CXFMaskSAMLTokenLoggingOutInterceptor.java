package no.nav.foreldrepenger.selvbetjening.cxfclient.interceptors;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.io.CachedOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;


public class CXFMaskSAMLTokenLoggingOutInterceptor extends LoggingOutInterceptor {

  private boolean maskerSAMLToken = true;

  public CXFMaskSAMLTokenLoggingOutInterceptor() {
    super();
  }

  public CXFMaskSAMLTokenLoggingOutInterceptor(int limit) {
    super(limit);
  }

  @Override
  protected void writePayload(StringBuilder builder, CachedOutputStream cos, String encoding,
                              String contentType) throws Exception {
    if (contentType.contains("xml") && maskerSAMLToken) {
      try (CachedOutputStream maskertCos = new CachedOutputStream()) {
        String xmlString = IOUtils.toString(cos.getInputStream());
        maskertCos.write(removeSAMLTokenFromXML(xmlString).getBytes());
        super.writePayload(builder, maskertCos, encoding, contentType);
      }
    } else {
      super.writePayload(builder, cos, encoding, contentType);
    }
  }

  public void setMaskerSAMLToken(boolean maskerSAMLToken) {
    this.maskerSAMLToken = maskerSAMLToken;
  }

  private String removeSAMLTokenFromXML(String xmlString) {
    Document document = Jsoup.parse(xmlString, "", Parser.xmlParser());
    for (Element element : document.getElementsByTag("soap:header").select("*")) {
      if (element.tagName().toLowerCase().endsWith(":security")) {
        element.remove();
      }
    }
    return document.toString();
  }
}
