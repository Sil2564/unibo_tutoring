package it.unibo.tutoring.model.box;

import java.util.UUID;

public interface BoxTutoraggio {

    UUID getId();

    String getTitolo();

    String getMateria();

    String getDescrizione();

    String getAutoreMatricola();

    BoxType getTipo();
}