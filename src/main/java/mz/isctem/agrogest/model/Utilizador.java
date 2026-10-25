package mz.isctem.agrogest.model;

import java.io.Serializable;

/** Utilizador do sistema (administrador ou cliente). */
public class Utilizador implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String email;
    private String pass;      // guardada como hash (salt:hash SHA-256), nunca em texto puro
    private Perfil perfil;
    private boolean ativo;
    private String telefone;
    private String endereco;

    public Utilizador() {
    }

    public Utilizador(int id, String nome, String email, String pass, Perfil perfil,
                      String telefone, String endereco) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.pass = pass;
        this.perfil = perfil;
        this.telefone = telefone;
        this.endereco = endereco;
        this.ativo = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public boolean isAdmin() { return perfil == Perfil.ADMIN; }
}
