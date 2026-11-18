PROGETTO "THE KNIFE"

AUTORI DEL PROGETTO:
- Alessandro Frigerio, Matricola: 759926, Sede: CO
- Antonio Pardo,       Matricola: 760613, Sede: CO
- Davide Moretti,      Matricola: 762176, Sede: CO


1. STRUTTURA DEL REPOSITORY
	Il repository è organizzato nella cartella radice secondo la seguente struttura:

	- autori.txt           : Elenco dei componenti del gruppo (nome, cognome, matricola, sede) e link al repository GitHub.
	- README.txt           : Il presente file con le istruzioni di installazione, compilazione tramite Maven ed esecuzione.
	- pom.xml              : File di configurazione e build di Apache Maven.
	- michelin_my_maps.csv : Dataset iniziale dei ristoranti per il popolamento automatico del database al primo avvio del server.
	- bin/                 : Contiene gli eseguibili finali compilati (.jar) per il modulo Server (serverTK.jar) e Client (clientTK.jar).
	- doc/                 : Contiene il Manuale Utente (.pdf), il Manuale Tecnico (.pdf), i diagrammi di progettazione (UML ed ER) e la documentazione JavaDoc generata.
	- lib/                 : Contiene eventuali dipendenze/librerie esterne di supporto alla compilazione e all'esecuzione.
	- src/                 : Contiene il codice sorgente Java strutturato nel package principale "theknife" e nei relativi sottopackage (dao, db, essenziali, gestore, gui, remoto) oltre alle risorse grafiche (logo e bandiere).


2. REQUISITI DI SISTEMA E PREREQUISITI D'AMBIENTE
	Per compilare ed eseguire correttamente la piattaforma TheKnife sono necessari:

	1. Java Development Kit (JDK) : Versione 21 o superiore.
	   Verificare l'installazione da terminale con:
	   java -version
	   javac -version

	2. Apache Maven : Versione 3.9.11 o superiore.
	   Verificare l'installazione da terminale con:
	   mvn -version (usando l'interfaccia di maven nell'IDE)

	3. PostgreSQL DBMS : Versione 14 o superiore (con pgAdmin 4).
	   Il servizio PostgreSQL deve essere attivo (porta di default: 5432).


3. INSTALLAZIONE E CONFIGURAZIONE DEL DATABASE (PostgreSQL)
	1. Avviare il server PostgreSQL locale (o remoto).
	2. Creare un database vuoto denominato esattamente:
	   the_knife

	   Tramite terminale psql (o Query Tool di pgAdmin), eseguire:
	   CREATE DATABASE the_knife;


	3. Creazione delle tabelle:
	   Collegarsi al database "the_knife" ed eseguire lo script SQL di creazione
	   delle tabelle (Utenti, RistorantiTheKnife, Recensioni, Preferiti) riportato
	   nel Manuale Tecnico / file di setup SQL:

	   CREATE TABLE IF NOT EXISTS Utenti (
		   id_utente SERIAL PRIMARY KEY,
		   nome VARCHAR(100) NOT NULL,
		   cognome VARCHAR(100) NOT NULL,
		   username VARCHAR(100) UNIQUE NOT NULL,
		   password VARCHAR(255) NOT NULL,
		   data_nascita DATE,
		   luogo_domicilio VARCHAR(100) NOT NULL,
		   ruolo VARCHAR(20) NOT NULL
	   );

	   CREATE TABLE IF NOT EXISTS RistorantiTheKnife (
		   id_ristorante SERIAL PRIMARY KEY,
		   nome VARCHAR(150) NOT NULL,
		   nazione VARCHAR(100) NOT NULL,
		   citta VARCHAR(100) NOT NULL,
		   indirizzo VARCHAR(255) NOT NULL,
		   latitudine DOUBLE PRECISION NOT NULL,
		   longitudine DOUBLE PRECISION NOT NULL,
		   fascia_prezzo INTEGER NOT NULL,
		   delivery BOOLEAN NOT NULL DEFAULT FALSE,
		   prenotazione_online BOOLEAN NOT NULL DEFAULT FALSE,
		   tipo_cucina VARCHAR(150) NOT NULL,
		   id_ristoratore INTEGER REFERENCES Utenti(id_utente) ON DELETE SET NULL
	   );

	   CREATE TABLE IF NOT EXISTS Recensioni (
		   id_recensione SERIAL PRIMARY KEY,
		   id_utente INTEGER NOT NULL REFERENCES Utenti(id_utente) ON DELETE CASCADE,
		   id_ristorante INTEGER NOT NULL REFERENCES RistorantiTheKnife(id_ristorante) ON DELETE CASCADE,
		   stelle INTEGER NOT NULL CHECK (stelle >= 1 AND stelle <= 5),
		   testo VARCHAR(400) NOT NULL,
		   data_recensione DATE NOT NULL DEFAULT CURRENT_DATE,
		   risposta_ristoratore VARCHAR(400),
		   UNIQUE (id_utente, id_ristorante)
	   );

	   CREATE TABLE IF NOT EXISTS Preferiti (
		   id_utente INTEGER NOT NULL REFERENCES Utenti(id_utente) ON DELETE CASCADE,
		   id_ristorante INTEGER NOT NULL REFERENCES RistorantiTheKnife(id_ristorante) ON DELETE CASCADE,
		   PRIMARY KEY (id_utente, id_ristorante)
	   );

	4. Popolamento iniziale dal dataset CSV:
	   Non è necessario importare manualmente il file "michelin_my_maps.csv".
	   Al primo avvio di "serverTK", il modulo "ImportatoreCSV" verifica in
	   automatico se la tabella "RistorantiTheKnife" è vuota e, in caso affermativo,
	   esegue il parsing e l'inserimento automatico dei ristoranti dal file
	   "michelin_my_maps.csv" presente nella cartella di avvio.


4. ISTRUZIONI PER LA COMPILAZIONE TRAMITE MAVEN
	Aprire un terminale (Prompt dei comandi, PowerShell o Bash) e posizionarsi nella
	cartella radice del progetto (dove è presente il file "pom.xml").

	1. Pulizia e compilazione del codice sorgente:
	   mvn clean compile

	2. Generazione dei pacchetti eseguibili (.jar) per Server e Client:
   	   mvn clean package

	   Questo comando compila il progetto, risolve le dipendenze e genera gli
	   eseguibili JAR per il Server e per il Client.
	   Una versione pronta all'uso dei due eseguibili è già fornita nella
	   directory "bin/" del repository.A


5. ISTRUZIONI PER L'ESECUZIONE DELL'APPLICAZIONE
	L'architettura è di tipo Client/Server basata su Java RMI (porta 1099).
	È necessario avviare prima il modulo Server (serverTK) e successivamente una o
	più istanze del modulo Client (clientTK).

	PASSO A - Avvio del Server (serverTK):
	1. Aprire il file serverTK.jar

	2. Come da specifica, il server richiederà da console:
	   - L'host del database PostgreSQL (premere Invio per usare "localhost").
	   - L'username del DBMS PostgreSQL (premere Invio per usare "postgres").
	   - La password di accesso al DBMS PostgreSQL (in caso di password errata, il sistema segnalerà l'errore richiedendo nuovamente l'inserimento).

	3. Verificata la connessione al DB ed effettuato l'eventuale controllo sul CSV,
	   il server pubblicherà il registro RMI sulla porta 1099 stampando:
	   "serverTK attivo sulla porta 1099 e in attesa del client"

	PASSO B - Avvio del Client (clientTK):
	1. Mantenendo il server attivo, aprire il file clientTK.jar

	2. È possibile avviare più istanze di "clientTK" contemporaneamente: il sistema
	   supporta l'accesso concorrente e sincronizza in tempo reale le modifiche tra i client aperti.


6. LIBRERIE UTILIZZATE E NOTE SULL'USO DI LIBRERIE ESTERNE
	Il progetto è stato sviluppato privilegiando le API standard del JDK:

	1. PostgreSQL JDBC Driver (org.postgresql:postgresql - versione 42.7.x):
	   Unica libreria esterna utilizzata, dichiarata come dipendenza standard nel
	   file "pom.xml" (e inclusa nel JAR eseguibile del server) per consentire a
	   "DatabaseManager" la connessione via JDBC al DBMS PostgreSQL.

	2. Interfaccia Grafica (Java Swing / AWT - javax.swing.*, java.awt.*):
	   Libreria standard integrata nel JDK. Tutti i componenti personalizzati sono
	   stati realizzati internamente tramite la classe "theknife.gui.StileTheKnife"
	   senza ricorrere a librerie UI non standard.

	3. Comunicazione Distribuita e Concorrenza (Java RMI - java.rmi.*):
	   Libreria standard integrata nel JDK per l'esposizione remota dell'interfaccia
	   "ServiziTheKnife" da parte di "ServerTK" e la gestione thread-safe delle
	   richieste concorrenti dei client.

	4. Sicurezza e Cifratura Password (java.security.MessageDigest):
	   Libreria standard integrata nel JDK utilizzata in "UtenteDAO" per l'hashing
	   unidirezionale SHA-256 delle password degli utenti prima del salvataggio
	   nel database.

	Non vengono pertanto utilizzate librerie di terze parti in modo non standard né
	sono richieste installazioni manuali di pacchetti proprietari esterni.