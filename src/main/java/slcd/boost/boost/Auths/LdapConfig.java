package slcd.boost.boost.Auths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;

@Configuration
public class LdapConfig {

    @Value("${ldap.url}")
    private String ldapUrl;

    @Value("${ldap.managerDn}")
    private String ldapManagerDn;

    @Value("${ldap.managerPassword}")
    private String ldapManagerPassword;

    @Value("${ldap.baseDn}")
    private String ldapBaseDn;

    @Value("${ldap.userDnPattern}")
    private String userDnPattern;

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(ldapUrl);
        contextSource.setUserDn(ldapManagerDn);
        contextSource.setBase(ldapBaseDn);
        contextSource.setPassword(ldapManagerPassword);
        return contextSource;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }


    public String filterUserDnByUsername(String username){
        return new AndFilter()
                .and(new EqualsFilter("objectClass","user"))
                .and(new EqualsFilter("sAMAccountName",username))
                .and(new EqualsFilter("objectCategory","person"))
                .and(new EqualsFilter("company","Solit Clouds"))
                .toString();
    }

    public String filterUserDnByEmail(String email){
        return new AndFilter()
                .and(new EqualsFilter("objectClass","user"))
                .and(new EqualsFilter("mail", email))
                .and(new EqualsFilter("objectCategory","person"))
                .and(new EqualsFilter("company","Solit Clouds"))
                .toString();
    }
}
