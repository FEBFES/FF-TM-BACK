package com.febfes.fftmback.config;

import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
class SecurityConfiguration {
    final Map<String, String> urlMapping = [
            "/login":"permitAll()",
            "/sso/login":"permitAll()",
            "/logout":"permitAll()",
            "/sso/logout":"permitAll()",
            "/css/":"permitAll()",
            "/fonts/":"permitAll()",
            "/js/":"permitAll()",
            "/lib/":"permitAll()",
            "/webjars/":"permitAll()",
            "/widgets/login":"permitAll()",
            "/widgets/info.json":"permitAll()",
            "/widgets/*/script.js":"permitAll()",
            "/widgets/*/style.css":"permitAll()",
            "/widgets/*/template.html":"permitAll()",
            "/widgets/*/settings.html":"permitAll()",
            "/api/tags/load":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/attachedTags/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/documentToUser/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/documentToDocument/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/documentToAccounts/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/wallets/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/clients/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/holdbacks/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/walletSettlements/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/operations/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/operationStates/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/greatStore/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/processingDocument/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/currencies/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/currenciesExchanges/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_BO_RESTRICTED')",
            "/api/*/load**":"hasAnyAuthority('ROLE_USER','ROLE_ADMIN')",
            "/api/accounts/new**":"hasAuthority('ROLE_ADMIN')",
            "/api/accounts/update**":"hasAuthority('ROLE_ADMIN')",
            "/api/accounts/delete**":"hasAuthority('ROLE_ADMIN')",
            "/api/accountCodes/new**":"hasAuthority('ROLE_ADMIN')",
            "/api/accountCodes/update**":"hasAuthority('ROLE_ADMIN')",
            "/api/accountCodes/delete**":"hasAuthority('ROLE_ADMIN')",
            "/api/accountTypes/new**":"hasAuthority('ROLE_ADMIN')",
            "/api/accountTypes/update**":"hasAuthority('ROLE_ADMIN')",
            "/api/accountTypes/delete**":"hasAuthority('ROLE_ADMIN')",
            "/api/getters/new**":"hasAuthority('ROLE_ADMIN')",
            "/api/getters/update**":"hasAuthority('ROLE_ADMIN')",
            "/api/getters/delete**":"hasAuthority('ROLE_ADMIN')",
            "/api/templates/new**":"hasAuthority('ROLE_ADMIN')",
            "/api/templates/update**":"hasAuthority('ROLE_ADMIN')",
            "/api/templates/delete**":"hasAuthority('ROLE_ADMIN')"
            ]
}
