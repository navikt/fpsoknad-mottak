package no.nav.foreldrepenger.selvbetjening.cxfclient;

import org.apache.cxf.Bus;
import org.apache.cxf.ws.security.SecurityConstants;
import org.apache.cxf.ws.security.tokenstore.SecurityToken;
import org.apache.cxf.ws.security.tokenstore.TokenStore;
import org.apache.cxf.ws.security.tokenstore.TokenStoreFactory;
import org.apache.cxf.ws.security.trust.STSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import no.nav.foreldrepenger.selvbetjening.openam.util.SecurityContextUtil;

public class NAVSTSClient extends STSClient {
    private static final Logger logger = LoggerFactory.getLogger(
        NAVSTSClient.class);
    private static TokenStore tokenStore;

    public NAVSTSClient(Bus b) {
        super(b);
    }

    @Override
    protected boolean useSecondaryParameters() {
        return false;
    }

    @Override
    public SecurityToken requestSecurityToken(
            String appliesTo, String action, String requestType, String binaryExchange
            ) throws Exception {
        
        String key = chooseCachekey();
        
        ensureTokenStoreExists();

        SecurityToken token;
        if(key != null) {
        	// try to use cache
	        token = tokenStore.getToken(key);
	        if (token == null) {
	            logger.debug("Missing token for {}, fetching it from STS", key);
	            token = super.requestSecurityToken(appliesTo, action, requestType, binaryExchange);
	            tokenStore.add(key, token);
	        } else {
	            logger.debug("Retrived token for {} from tokenStore", key);
	        }
        } else {
        	// skip use of cache since we don't have a key to use
        	logger.debug("No cackekey for this request, skip use of cache");
            token = super.requestSecurityToken(appliesTo, action, requestType, binaryExchange);
        }
        return token;
    }

    private void ensureTokenStoreExists() {
        if (tokenStore == null) {
            createTokenStore();
        }
    }

    private synchronized void createTokenStore() {
        logger.debug("Creating tokenStore");
        if (tokenStore == null) {
            tokenStore = TokenStoreFactory.newInstance().newTokenStore(SecurityConstants.TOKEN_STORE_CACHE_INSTANCE, message);
        }
    }

  private String chooseCachekey() {

    Authentication authentication = SecurityContextUtil.getAuthentication();

    // choose cachekey based on external user or system user
    String key;
    if (authentication != null && SecurityContextUtil.getNavPrincipal() != null) {
      key = SecurityContextUtil.getNavPrincipal().getToken() + SecurityContextUtil.getNavPrincipal()
          .getSecurityLevel();
    } else {
      key = "systemSAML";
    }
    logger.debug("Chosen cachekey for this request is {}", key);
    return key;
  }
}
