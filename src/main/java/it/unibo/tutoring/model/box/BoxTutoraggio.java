package it.unibo.tutoring.model.box;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public interface BoxTutoraggio {

    UUID getId();

    String getTitolo();

    String getCorso();

    String getMateria();

    String getArgomento();

    LocalDate getData();

    LocalTime getOra();

    int getDurataOre();

    String getAutoreMatricola();

    BoxType getTipo();
}