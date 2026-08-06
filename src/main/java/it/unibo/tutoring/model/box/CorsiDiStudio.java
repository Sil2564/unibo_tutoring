package it.unibo.tutoring.model.box;

import java.util.List;

/**
 * Elenco unico dei corsi di laurea (triennali + magistrali) usato sia dal
 * filtro "Corso" nella dashboard, sia dal menu "Corso" nella pagina di
 * creazione di un nuovo annuncio, cosi' che le due liste non vadano mai
 * fuori sincrono.
 */
public final class CorsiDiStudio {

    public static final List<String> TUTTI = List.of(
        "Architettura",
        "Biomedical Engineering",
        "Digital Transformation Management",
        "Ingegneria biomedica",
        "Ingegneria e scienze informatiche",
        "Ingegneria elettronica",
        "Ingegneria elettronica per l'intelligenza artificiale",
        "Neuroscienze e riabilitazione neuropsicologica",
        "Psicologia clinica",
        "Psicologia scolastica e di comunità",
        "Scienze e cultura della gastronomia",
        "Scienze e tecnologie alimentari",
        "Tecnologie alimentari",
        "Tecnologie dei sistemi informatici",
        "Viticoltura ed enologia",
        "Work, Organizational and Personnel Psychology"
    );

    private CorsiDiStudio() {
    }
}
