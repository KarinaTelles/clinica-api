package dev.karinatelles.clinica.auth;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String senhaHash;

    protected Usuario() {
    }

    public Usuario(String login, String senhaHash) {
        this.login = login;
        this.senhaHash = senhaHash;
    }

    public String getLogin() { return login; }
    public String getSenhaHash() { return senhaHash; }
}
