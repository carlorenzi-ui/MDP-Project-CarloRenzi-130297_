
# AnnoFuoriCorso

## 📌 Descrizione del Progetto

AnnoFuoriCorso è un gioco gestionale e di simulazione accademica realizzato in Java con interfaccia grafica JavaFX. Il giocatore veste i panni dello studente fuoricorso impegnato a superare gli esami del proprio piano di studi, gestendo con equilibrio la fatica, il budget economico e il tempo a disposizione.

Durante le settimane accademiche è possibile scegliere tra studio individuale, uscite sociali per abbattere lo stress accumulato o lavori part-time per incrementare le proprie disponibilità finanziarie. Il giocatore può acquistare e consumare oggetti (RedBull, Pasto Caldo, Appunti, Gruppi di Studio), affrontare appelli d'esame contro docenti rivali selezionando strategie differenti (studio standard, nottata intensa, tentativi disperati) e accumulare crediti formativi fino al raggiungimento di finali alternativi. Lo stato di gioco può essere salvato e ripreso in qualsiasi momento grazie alla persistenza su file in formato JSON.

---

## 🚀 Come eseguire il progetto

### Prerequisiti

* Java 21 (LTS) o superiore
* Gradle (è incluso il wrapper `./gradlew` / `gradlew.bat`, non serve installarlo separatamente)



### Download del repository

```bash
git clone https://github.com/carlorenzi-ui/MDP-Project-CarloRenzi-130297_.git
cd MDP-Project-CarloRenzi-130297_

```

### Build del progetto

* **Linux / macOS:**
```bash
./gradlew build

```


* **Windows (PowerShell o CMD):**
```powershell
.\gradlew.bat build

```



### Esecuzione

* **Linux / macOS:**
```bash
./gradlew run

```


* **Windows (PowerShell o CMD):**
```powershell
.\gradlew.bat run

```



---

## 🏛️ Architettura

Il progetto è strutturato secondo il pattern architetturale **MVC (Model-View-Controller)** e i principi **SOLID**, all'interno del package base `it.unicam.cs.mpgc.afc250713`:

* **`model`** — Racchiude le entità di dominio e le regole del gioco, completamente indipendenti dall'interfaccia grafica:


* `student`: gestione dello stato dello studente, fatica (`FatigueSystem`), finanze (`BudgetSystem`), avanzamento della carriera (`AcademicProgress`) ed equipaggiamento nello zaino (`Zaino`, `StudyEquipmentManager`).


* `action`: azioni settimanali eseguibili (`StudyAction`, `SocialOutingAction`, `PartTimeJobAction`) derivate dal contratto `WeeklyAction`.


* `item`: gerarchia di oggetti consumabili e di potenziamento gestiti tramite `ItemRegistry`.


* `subject` & `exam`: modellazione degli insegnamenti, fabbriche dedicate (`SubjectFactory`, `ExamFactory`) e gestione dei premi d'esame basata su **Visitor Pattern** (`RewardVisitor`, `ItemReward`).


* `challenge` & `strategy`: logica degli scontri d'esame contro i docenti rivali e adozione dello **Strategy Pattern** per definire la condotta d'esame (`LazyStudyStrategy`, `HardStudyStrategy`, `CheatStrategy`).


* `ending`: gestione dei possibili esiti della carriera universitaria dello studente (`EndingType`).




* **`controller`** — Coordina la logica applicativa e il flusso degli stati:


* `core`: orchestrazione generale delle partite tramite `GameManager` ed `ExamManager`.


* `state`: macchina a stati finiti per le fasi di gioco tramite lo **State Pattern** (`CampusState`, `ExamAttemptState`, `EndingState`).


* `events`: disaccoppiamento dei componenti mediante notifica con `EventDispatcher`.




* **`view`** — Livello di presentazione grafico realizzato in JavaFX con interfacce FXML e stili CSS (`GameController`, `StudentStatsController`), avviato tramite `JavaFXApp` e la classe di bootstrap `Launcher`.


* **`persistence`** — Meccanismo di salvataggio e caricamento dello stato basato su Data Transfer Object (`StudentSaveDTO`), mapper per la conversione (`StudentMapper`) e gestione I/O su file (`FileStorageService`, `SaveManager`) in formato JSON.


* **`utils`** — Servizi di supporto per il parsing e caricamento dei dati statici (`stats.json`, `subjects.json`) dalle risorse dell'applicazione (`SubjectLoader`, `ChallengeStatsService`).



---

## 🤖 Uso di strumenti di AI

Per lo sviluppo del progetto sono stati impiegati strumenti di Intelligenza Artificiale (LLM) a supporto dell'attività di programmazione, con costante revisione e adattamento personale di ogni soluzione proposta.

In particolare, l'AI è stata utilizzata per:

* **Supporto al debugging**: identificazione e correzione di errori di compilazione, risoluzione di problemi nei percorsi dei file e configurazione del plugin JavaFX in Gradle.
* **Stesura della documentazione**: revisione stilistica e generazione delle descrizioni Javadoc per metodi e interfacce di package.
* **Generazione di codice boilerplate**: implementazione iniziale di classi DTO, costruttori, metodi getter/setter e mapping dei campi per la persistenza JSON.
* **Studio e applicazione dei Design Pattern**: chiarimento concettuale e validazione dell'integrazione pratica dei pattern architetturali (Visitor, Strategy, State, Factory, Singleton/Registry) nel dominio accademico.

Tutte le scelte architetturali, la struttura dei package, la logica di calcolo del gioco e la verifica del corretto funzionamento sono state gestite e validate direttamente dallo sviluppatore.

---

## 🛠️ Tecnologie utilizzate

* **Java 21** — Linguaggio di programmazione
* **JavaFX** — Framework grafico per interfacce utente (FXML e CSS)


* **Gradle** — Build automation tool con Kotlin DSL (`build.gradle.kts`)


* **Gson** — Libreria per la serializzazione e deserializzazione JSON dei salvataggi
