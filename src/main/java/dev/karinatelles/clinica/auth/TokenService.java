package dev.karinatelles.clinica.auth;

import java.time.Clock;
import java.time.Instant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    static final String EMISSOR = "clinica-api";

    private final JwtEncoder encoder;
    private final JwtProperties propriedades;
    private final Clock relogio;

    public TokenService(JwtEncoder encoder, JwtProperties propriedades, Clock relogio) {
        this.encoder = encoder;
        this.propriedades = propriedades;
        this.relogio = relogio;
    }

    public DadosToken gerar(String login) {
        var agora = Instant.now(relogio);
        var expiraEm = agora.plus(propriedades.validade());
        var claims = JwtClaimsSet.builder()
                .issuer(EMISSOR)
                .subject(login)
                .issuedAt(agora)
                .expiresAt(expiraEm)
                .build();
        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        var token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new DadosToken(token, "Bearer", expiraEm);
    }
}
