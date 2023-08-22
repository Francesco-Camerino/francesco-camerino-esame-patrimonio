package cloud.ex.it.patrimonio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "patrimonio")
public class Patrimonio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patrimonio_generator")
    private Long id;


    @Column(name = "nome")
    private String nome;

    @Column(name = "valore")
    private Long valore;


    @Column(name = "annoCreazione")
    private Integer annoCreazione;

    public Patrimonio() {}
    public Patrimonio(String nome, Long valore, Integer annoCreazione) {
        this.nome = nome;
        this.valore = valore;
        this.annoCreazione = annoCreazione;
    }

    @Override
    public String toString() {
        return "Tutorial [id=" + id + ", nome=" + nome + ", valore=" + valore + ", annoCreazione=" + annoCreazione + "]";
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getValore() {
        return valore;
    }

    public void setValore(Long valore) {
        this.valore = valore;
    }

    public Integer getAnnoCreazione() {
        return annoCreazione;
    }

    public void setAnnoCreazione(Integer annoCreazione) {
        this.annoCreazione = annoCreazione;
    }
}

