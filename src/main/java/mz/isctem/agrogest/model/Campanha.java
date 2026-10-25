package mz.isctem.agrogest.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Campanha agricola (ex.: Campanha 2026/2027). */
public class Campanha implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private LocalDate inicio;
    private LocalDate fim;
    private boolean aberta;

    public Campanha() {
    }

    public Campanha(int id, String nome, LocalDate inicio, LocalDate fim) {
        this.id = id;
        this.nome = nome;
        this.inicio = inicio;
        this.fim = fim;
        this.aberta = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getInicio() { return inicio; }
    public void setInicio(LocalDate inicio) { this.inicio = inicio; }

    public LocalDate getFim() { return fim; }
    public void setFim(LocalDate fim) { this.fim = fim; }

    public boolean isAberta() { return aberta; }
    public void setAberta(boolean aberta) { this.aberta = aberta; }

    public String getInicioFmt() { return fmt(inicio); }
    public String getFimFmt() { return fmt(fim); }

    private static String fmt(LocalDate d) {
        return d == null ? "-" : d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
