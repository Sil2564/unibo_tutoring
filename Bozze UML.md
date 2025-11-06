https://www.mermaidchart.com/


Io farei così per l'uml, ognuno fa un piccolo uml (che però prova ad essere dettagliato) sulle parti che ci siamo assegnati da svolgere
Alla fine faremo un unico uml che cerca di racchiudere tutto (escludendo parti poco importanti per aumentarne la leggibilità)

UML ANDREA (INCOMPLETO, DA FINIRE)

---
config:
  theme: dark
---
classDiagram
    class Utente {
        +inviaMessaggio(Chat, testo)
        +visualizzaChat()
    }
    class Chat {
        -int idChat
        -List<Messaggio> messaggi
        -List<Utente> partecipanti
        -DateTime ultimaAttivita
        +aggiungiMessaggio(Messaggio)
        +ottieniMessaggi()
        +mostraCronologia()
    }
    class Messaggio {
        -int idMessaggio
        -String testo
        .DateTime timestamp
        -Utente mittente
        -Utente destinatario
        +segnalaComeLetto()
    }
    class SessioneTutoraggio {
        -int idSessione
        -String stato <<proposta/confermata/completata>>
        -Chat chatAssociata
        +collegaChat(Chat)
    }
    Utente "1" -- "*" Messaggio : invia 
    Messaggio "*" -- "1" Chat : appartiene a 
    Chat "1" -- "*" Messaggio : contiene 
    Chat "*" -- "2" Utente : coinvolge 
    SessioneTutoraggio "1" -- "1" Chat : collega
