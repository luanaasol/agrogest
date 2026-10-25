package mz.isctem.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Colheita registada para um cultivo. O valor total e calculado (kg x preco por kg). */
public class Colheita implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int cultivoId;
    private LocalDate data;
    private double kg;
    private BigDecimal precoKg;

    public Colheita() {
    }

    public Colheita(int id, int cultivoId, LocalDate data, double kg, BigDecimal precoKg) {
        this.id = id;
        this.cultivoId = cultivoId;
        this.data = data;
        this.kg = kg;
        this.precoKg = precoKg;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCultivoId() { return cultivoId; }
    public void setCultivoId(int cultivoId) { this.cultivoId = cultivoId; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public double getKg() { return kg; }
    public void setKg(double kg) { this.kg = kg; }

    public BigDecimal getPrecoKg() { return precoKg; }
    public void setPrecoKg(BigDecimal precoKg) { this.precoKg = precoKg; }

    /** Valor total calculado pelo sistema: kg x preco por kg. */
    public BigDecimal getValorTotal() {
        return precoKg == null ? BigDecimal.ZERO : precoKg.multiply(BigDecimal.valueOf(kg));
    }

    public String getDataFmt() {
        return data == null ? "-" : data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
