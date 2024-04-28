package ru.maeasoftoworks.normativecontrol.api.security.accessrules;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import ru.maeasoftoworks.normativecontrol.api.domain.users.Role;
import ru.maeasoftoworks.normativecontrol.api.domain.users.User;
import ru.maeasoftoworks.normativecontrol.api.services.JwtService;
import ru.maeasoftoworks.normativecontrol.api.utils.jwt.Jwt;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountsAccessRule implements AccessRule {

    private final JwtService jwtService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext ctx) {
        Long accountId = Long.parseLong(ctx.getVariables().get("account_id"));

        log.info(ctx.getRequest().getRequestURI());

        String accessToken = ctx.getRequest().getHeader("Authorization").substring(("Bearer ").length());
        Jwt jwt = jwtService.getJwtFromAccessTokenString(accessToken);
        User user = jwt.getUser();
        if(user.getRole() == Role.ADMIN)
            return new AuthorizationDecision(true);
        if(user.getId() == accountId)
            return new AuthorizationDecision(true);
        return new AuthorizationDecision(false);
    }
}