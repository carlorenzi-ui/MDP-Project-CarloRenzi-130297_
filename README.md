# Anno Fuori Corso - "L'Ultimo Appello"

Progetto per Metodologie di Programmazione / Modellazione e Gestione della
Conoscenza (AA 2025/26), sviluppato seguendo la stessa architettura e gli
stessi pattern di progettazione della codebase di riferimento fornita, ma
applicati a un gioco completamente diverso: un life-simulator RPG in cui si
interpreta uno studente fuori corso da 3 anni che deve laurearsi prima di
esaurire morale e budget.

## Come si gioca

- **Morale (0-100)**: la salute mentale. Se arriva a 0, "abbandoni gli studi".
- **Budget (0-200)**: i soldi di famiglia + lavoretti. Se arriva a 0, il
  morale crolla (bancarotta).
- **Stanchezza (0-100)**: cresce a ogni tentativo d'esame. Se arriva al
  massimo, va in burnout (morale a 0).
- **CFU / Media**: il progresso verso la laurea e la qualita' del finale.

Ogni settimana scegli un'azione (Studia / Lavoretto / Esci con gli amici).
Ogni 4 settimane si apre la sessione d'esame: puoi tentare una materia
scegliendo una tattica:
- **Ripasso dell'ultimo minuto** (studio matto): 80% successo, -40 morale.
- **Domanda a piacere** (studio pigro): buone chance solo se la media > 24.
- **Copiare** (dilemma etico): 100% successo, ma 30% di essere beccati
  (esame annullato, morale a zero).

Il gioco termina con uno di quattro finali: **Laurea con Lode**, **Il Pezzo
di Carta**, **Abbandono**, **Cambio Facolta'**.

## Come avviare il progetto

```
./gradlew run
```

## Mappatura dei pattern rispetto al progetto di riferimento

| Pattern / concetto              | Riferimento (Dungeon Crawler)         | Questo progetto (Anno Fuori Corso)             |
|----------------------------------|----------------------------------------|--------------------------------------------------|
| Combattimento / entita'           | `Combatant`, `Damageable`, `Attacker`, `AbstractCombatant`, `CombatStats` | `Rival`, `Vulnerable`, `Challenger`, `AbstractRival`, `ChallengeStats` |
| Personaggio giocante              | `AbstractHero`, `Warrior`             | `AbstractStudent`, `Fuoricorso`                   |
| Risorsa "fame"                    | `HungerSystem`                        | `FatigueSystem` (stanchezza / burnout)            |
| Progressione                      | `LevelSystem` (XP/livello)            | `AcademicProgress` (CFU/anno/media)               |
| Nuova risorsa economica           | -                                      | `BudgetSystem` (soldi, bancarotta)                |
| Equipaggiamento (Strategy-ish)    | `EquipmentManager`, `BuffType`         | `StudyEquipmentManager`, `StudyBuffType`          |
| Inventario                        | `Inventory`                            | `Zaino`                                           |
| Oggetti (Strategy pattern)        | `Item`, `HealthPotion`, `Food`, `Sword`, `Armor` | `Item`, `RedBull`, `PastoCaldo`, `Appunti`, `GruppoStudio` |
| Nemico / boss                     | `Enemy`, `EnemyFactory`               | `Exam`, `ExamFactory`                             |
| Area esplorabile                  | `Dungeon`, `DungeonFactory`           | `Subject`, `SubjectFactory`                       |
| Ricompense (Visitor pattern)      | `Loot`, `LootVisitor`, `ResourceLoot` | `Reward`, `RewardVisitor`, `ItemReward`           |
| Nuovo: tattiche d'esame (Strategy)| -                                      | `ExamStrategy` -> `HardStudyStrategy`, `LazyStudyStrategy`, `CheatStrategy` |
| Nuovo: azioni settimanali (Strategy)| -                                    | `WeeklyAction` -> `StudyAction`, `PartTimeJobAction`, `SocialOutingAction` |
| Nuovo: finali multipli            | -                                      | `EndingType` (enum a 4 valori)                    |
| Orchestratore centrale            | `GameManager`                         | `GameManager`                                     |
| Gestione turno di scontro         | `CombatManager<F1,F2>`                | `ExamManager` (risoluzione one-shot via Strategy) |
| Macchina a stati (State pattern)  | `GameState`, `HubState`, `CombatState`, `GameOverState`, `StateType` | `AcademicState`, `CampusState`, `ExamAttemptState`, `EndingState`, `AcademicPhase` |
| Eventi / log                      | `EventDispatcher`                     | `EventDispatcher`                                 |
| Persistenza (DTO + Mapper)        | `HeroSaveDTO`, `HeroMapper`, `SaveManager`, `StorageService`, `FileStorageService` | `StudentSaveDTO`, `StudentMapper`, `SaveManager`, `StorageService`, `FileStorageService` |
| GUI (JavaFX MVC)                  | `JavaFXApp`, `GameController`, `HeroStatsController`, `UILootRendererVisitor` | `JavaFXApp`, `GameController`, `StudentStatsController`, `RewardRendererVisitor` |

## Note di progettazione

- **State pattern**: il flusso di gioco (`CampusState` -> `ExamAttemptState`
  -> `EndingState`) impedisce azioni non valide per costruzione (es. non puoi
  tentare un secondo esame mentre sei gia' dentro uno).
- **Strategy pattern**: sia le tattiche d'esame (`ExamStrategy`) sia le
  azioni settimanali (`WeeklyAction`) sono intercambiabili e aggiungibili
  senza modificare `GameManager` (Open/Closed Principle).
- **Visitor pattern**: le ricompense (`Reward`) vengono renderizzate in UI
  tramite `RewardRendererVisitor`, disaccoppiando la logica di dominio dalla
  presentazione.
- **Dependency Inversion**: la persistenza dipende dall'astrazione
  `StorageService`, non da un file system concreto - sostituibile con
  database o cloud senza toccare `SaveManager`.
- **Dilemma etico**: la scelta "Copiare" (`CheatStrategy`) e il suo esito
  vengono registrati nello storico (`ethicalChoiceHistory`) e persistiti nel
  salvataggio, come richiesto dalla specifica.
