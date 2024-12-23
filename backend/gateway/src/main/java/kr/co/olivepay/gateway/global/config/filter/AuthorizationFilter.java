package kr.co.olivepay.gateway.global.config.filter;

import kr.co.olivepay.gateway.client.MemberServiceWebClient;
import kr.co.olivepay.gateway.dto.res.MemberRoleRes;
import kr.co.olivepay.gateway.global.config.PathConfig;
import kr.co.olivepay.gateway.global.handler.AppException;
import kr.co.olivepay.gateway.global.util.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;

import static kr.co.olivepay.gateway.global.enums.ErrorCode.*;

@Slf4j
@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<PathConfig> {

    private final String ACCESS_TOKEN = "accessToken";
    private final String MEMBER_ID = "memberId";
    private final String PATH = "path";
    private final String ROLE = "role";
    private final String IS_SKIP = "isSkip";

    private final TokenUtils tokenUtils;
    private final MemberServiceWebClient memberServiceWebClient;

    public AuthorizationFilter(TokenUtils tokenUtils, MemberServiceWebClient memberServiceWebClient) {
        super(PathConfig.class);
        this.tokenUtils = tokenUtils;
        this.memberServiceWebClient = memberServiceWebClient;
    }

    /**
     * 인가 필터
     * @param config
     * @return
     */
    @Override
    public GatewayFilter apply(PathConfig config) {
        return (exchange, chain) -> {
            // 인가 확인이 필요없는 요청이라면, 다음 필터로 넘어감
            boolean isSkip = exchange.getAttributeOrDefault(IS_SKIP, false);
            if(isSkip){
                return chain.filter(exchange);
            }

            // AccessToken, memberId 가져오기
            String accessToken = exchange.getAttribute(ACCESS_TOKEN);
            Long memberId = exchange.getAttribute(MEMBER_ID);

            // 요청된 URL, 권한 가져오기
            String requestUrl = exchange.getAttribute(PATH);
            String tokenRole = exchange.getAttribute(ROLE);

            // URL 권한 일치 여부 확인
            if (!isAuthorized(requestUrl, tokenRole, config)) {
                log.info("AuthorizationFilter: 권한이 일치하지 않음. 요청 URL: {}, 토큰 권한: {}", requestUrl, tokenRole);
                throw new AppException(ACCESS_DENIED);
            }

            // 요청된 accessToken의 권한이 memberId의 권한과 같은지 확인
            return validateTokenRole(tokenRole, memberId)
                    .doOnNext(memberRoleRes -> log.info("권한 검증 성공: {}", memberRoleRes))
                    .doOnError(e -> log.error("권한 검증 중 오류 발생: {}", e.getMessage()))
                    .then(Mono.fromRunnable(() -> {
                        // SecurityContext 권한 설정
                        setAuthentication(memberId, tokenRole);
                    }))
                    .then(chain.filter(exchange)); // 필터 체인 진행
        };
    }

    /**
     * 토큰에서 추출한 Role을 통해 Member 서비스에 등록한 Role과 비교 <br>
     * 비동기 통신
     * @param tokenRole
     * @param memberId
     * @return
     */
    private Mono<MemberRoleRes> validateTokenRole(String tokenRole, Long memberId) {
        return memberServiceWebClient.getMemberRole(memberId)
                 .flatMap(memberRoleRes -> {
                     // 토큰에 맞는 회원이 없음
                     if (memberRoleRes.role() == null || memberRoleRes.role().isEmpty()) {
                         log.info("회원이 없습니다. 역할이 비어 있습니다.");
                         return Mono.error(new AppException(TOKEN_INVALID));
                     }

                     // 요청된 accessToken의 권한과 memberId의 권한을 비교
                     if (!tokenRole.equals(memberRoleRes.role())) {
                         log.info("권한 불일치: tokenRole = {}, memberRole = {}", tokenRole, memberRoleRes.role());
                         return Mono.error(new AppException(ACCESS_DENIED));
                     }

                     return Mono.just(memberRoleRes);
                 });
    }


    /**
     * 허용된 경로 리스트와 정규 표현식 패턴을 결합하여 검사
     * @param requestUrl
     * @param role
     * @param config
     * @return
     */
    private boolean isAuthorized(String requestUrl, String role, PathConfig config) {
        return config.getRoleUrlMappingsExact()
                     .getOrDefault(role, Collections.emptySet())
                     .stream()
                     .anyMatch(requestUrl::equals) ||
                config.getRoleUrlMappingsMatches()
                      .getOrDefault(role, Collections.emptySet())
                      .stream()
                      .anyMatch(requestUrl::matches);
    }

    /**
     * SecurityContextHolder에 유저 등록
     * @param memberId
     * @param role
     */
    private void setAuthentication(Long memberId, String role) {
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(role));

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(memberId, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
}